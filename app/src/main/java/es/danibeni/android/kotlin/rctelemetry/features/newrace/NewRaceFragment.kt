package es.danibeni.android.kotlin.rctelemetry.features.newrace

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.text.SpannableStringBuilder
import android.util.Log
import android.view.*
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import es.danibeni.android.kotlin.rctelemetry.R
import es.danibeni.android.kotlin.rctelemetry.core.extension.*
import es.danibeni.android.kotlin.rctelemetry.core.navigation.Navigator
import es.danibeni.android.kotlin.rctelemetry.core.platform.BaseFragment
import javax.inject.Inject
import es.danibeni.android.kotlin.rctelemetry.core.exception.Failure
import es.danibeni.android.kotlin.rctelemetry.core.exception.Failure.NetworkConnectionToSSID
import es.danibeni.android.kotlin.rctelemetry.features.newrace.NewRaceFailure.VehiculeCommunicationFailure
import es.danibeni.android.kotlin.rctelemetry.features.newrace.NewRaceFailure.NoVehiclesFound
import android.widget.*
import es.danibeni.android.kotlin.rctelemetry.data.Circuit
import es.danibeni.android.kotlin.rctelemetry.data.Race
import kotlinx.android.synthetic.main.circuitdetails_fragment.*
import kotlinx.android.synthetic.main.newrace_fragment.*

class NewRaceFragment : BaseFragment() {
    private val TAG: String = NewRaceFragment::class.java.simpleName
    private val COARSE_LOCATION_REQUEST_CODE = 100
    private val REFRESH_VEHICLES = "es.danibeni.android.kotlin.rctelemetry.features.newrace.refreshvehicles"
    private var refreshVehicles: Boolean = false

    private var refreshActionVisible: Boolean = false
    private var nextActionMenuVisible: Boolean = false

    @Inject
    lateinit var navigator: Navigator

    @Inject
    lateinit var vehiclesAdapter: NewRaceVehiclesAdapter

    private var vehiclePosition: Int = -1

    private lateinit var newRaceViewModel: NewRaceViewModel

    override fun layoutId() = R.layout.newrace_fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            refreshVehicles = true
        } else {
            refreshVehicles = savedInstanceState.getBoolean(REFRESH_VEHICLES)
        }
        appComponent.inject(this)
        newRaceViewModel = viewModel(viewModelFactory) {
            observe(circuitsLiveData, ::renderCircuitsList)
            observe(circuitLiveData, ::renderCircuitDetails)
            observe(driversLiveData, ::renderDriversList)
            observe(vehiclesLiveData, ::renderVehiclesList)
            observe(vehicleLiveData, ::renderVehicleStatus)
            observe(lastVehicleClicked, ::updateVehicleClicked)
            failure(failure, ::handleFailure)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeView()
    }

    private fun initializeView() {
        setHasOptionsMenu(true)

        val newRaceVehiclesRVManager = LinearLayoutManager(this.context)
        newRaceVehiclesRV.layoutManager = newRaceVehiclesRVManager

        newRaceVehiclesRV.adapter = vehiclesAdapter
        val itemDecoration = DividerItemDecoration(activity, newRaceVehiclesRVManager.orientation)
        newRaceVehiclesRV.addItemDecoration(itemDecoration)

        vehiclesAdapter.clickListener = { vehicle, position ->
            newRaceContainerCL.hideKeyboard()
            if (vehiclePosition != -1) {
                newRaceViewModel.vehiclesLiveData.value!!.get(vehiclePosition).status = getString(R.string.new_race_vehicle_available)
            }
            newRaceViewModel.vehiclesLiveData.value!!.get(position).status = getString(R.string.new_race_vehicle_blinking)
            renderVehiclesList(newRaceViewModel.vehiclesLiveData.value)
            newRaceViewModel.identifyVehicle(vehicle, position)
        }

        newRaceRefreshButton.setOnClickListener {
            refreshVehiclesList()
        }

        newRaceNextButton.setOnClickListener {
            onNextActionButtonPressed()
        }

        if (newRaceViewModel.vehicleLiveData.value != null) {
            renderVehiclesList(newRaceViewModel.vehiclesLiveData.value)
        }
    }

    private fun loadCityFromLocation() {
        setupLocationPermissions()
    }

    private fun loadCircuitsList() {
        newRaceViewModel.loadCircuits()
    }

    private fun loadDriversList() {
        newRaceViewModel.loadDrivers()
    }

    private fun loadVehiclesList() {
        newRaceVehiclesRV.invisible()
        newRaceVehiclesMsgBoxLL.invisible()
        setRefreshActionButtonViews(false)
        setNextActionButtonViews(false)
        newRaceLoadingVehiclesRL.visible()
        newRaceViewModel.loadVehicles()
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        if (inflater != null) {
            inflater.inflate(R.menu.new_race_nav_menu, menu)
            val next_item: MenuItem = menu!!.findItem(R.id.newRaceActionNext)
            next_item.isVisible = nextActionMenuVisible
        }
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle presses on the action bar menu items
        when (item.itemId) {
            R.id.newRaceActionNext -> {
                onNextActionButtonPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        setNextActionButtonViews(newRaceViewModel.nextActionVisible)
        if (refreshVehicles) {
            refreshVehicles = false
            loadCityFromLocation()
            loadCircuitsList()
            loadDriversList()
            loadVehiclesList()
        }

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(REFRESH_VEHICLES, refreshVehicles)

    }

    private fun setupLocationPermissions() {
        val permissionLocation = ContextCompat.checkSelfPermission(activity!!,
                Manifest.permission.ACCESS_COARSE_LOCATION)

        if (permissionLocation != PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "Permission to location denied")
            if (shouldShowRequestPermissionRationale(
                            android.Manifest.permission.ACCESS_COARSE_LOCATION)) {
                renderPermissionsAskDialog(R.string.circuit_details_coarse_location_permission_title, R.string.circuit_details_coarse_location_permission_message)
            } else {
                makeRequest()
            }
        } else {
            val locationManager: LocationManager = context!!.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            try {
                val city = getCity(location)
                circuitDetailsCity.text = SpannableStringBuilder(city)
            } catch (e: Exception) {
                Snackbar.make(newRaceContainerCoordL, R.string.circuit_details_city_not_located, Snackbar.LENGTH_LONG).show()
            }
        }
    }

    private fun renderPermissionsAskDialog(title: Int, message: Int) {
        context!!.showDialog {
            setTitle(title)
            setMessage(message)
            positiveButton(getString(R.string.action_ok)) {
                makeRequest()
            }
        }
    }

    private fun makeRequest() {
        requestPermissions(
                arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION),
                COARSE_LOCATION_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            COARSE_LOCATION_REQUEST_CODE -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    loadCityFromLocation()
                }
                return
            }
        }
    }

    fun getCity(location: Location): String {
        var cityName = ""

        val geocoder = android.location.Geocoder(context, java.util.Locale.getDefault())

        val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 10)
        if (addresses.size > 0) {
            for (address in addresses) {
                if (address.locality != null && address.locality.length > 0) {
                    cityName = address.locality
                    break
                }
            }
        }
        return cityName
    }

    private fun refreshVehiclesList() {
        loadVehiclesList()
    }

    private fun renderCircuitsList(circuits: List<NewRaceCircuitView>?) {
        if (circuits != null) {
            if (circuits.isNotEmpty()) {
                val circuitsNames = circuits.map { it.name }
                val circuitsAdapter = ArrayAdapter(context, android.R.layout.simple_expandable_list_item_1, circuitsNames)
                circuitDetailsName.setAdapter(circuitsAdapter)
                circuitDetailsName.threshold = 1
                circuitDetailsName.setOnItemClickListener { _, _, position, _ ->
                    newRaceViewModel.setCircuit(circuits[position])
                }
            }
        }
    }

    private fun renderCircuitDetails(circuitView: NewRaceCircuitView?) {

        if (circuitView != null) {
            circuitDetailsCity.text = SpannableStringBuilder(circuitView.city)
            circuitDetailsLenght.text = SpannableStringBuilder(circuitView.lenght.toString())
        }
    }

    private fun renderDriversList(drivers: List<String>?) {
        if (drivers != null) {
            val driversAdapter = ArrayAdapter(context, android.R.layout.simple_expandable_list_item_1, drivers)
            newRaceDriver.setAdapter(driversAdapter)
            newRaceDriver.threshold = 1
        }
    }

    private fun renderVehiclesList(vehicles: List<NewRaceVehicleView>?) {
        if (vehicles != null) {
            vehiclesAdapter.collection = vehicles.orEmpty()
        }
        newRaceVehiclesRV.visible()
        newRaceLoadingVehiclesRL.invisible()
        setRefreshActionButtonViews(true)
    }

    private fun renderVehicleStatus(vehicle: NewRaceVehicleView?) {
        if (vehiclePosition != -1) {
            if (vehicle != null) {
                newRaceViewModel.vehiclesLiveData.value!!.get(newRaceViewModel.lastVehicleClicked.value!!).status = getString(R.string.new_race_vehicle_available)
                newRaceViewModel.vehiclesLiveData.value!!.get(vehiclePosition).status = getString(R.string.new_race_vehicle_selected)
                renderVehiclesList(newRaceViewModel.vehiclesLiveData.value)
                setNextActionButtonViews(true)
            }
        }
    }

    private fun updateVehicleClicked(lastClickedPosition: Int?) {
        vehiclePosition = lastClickedPosition!!
    }

    private fun handleFailure(failure: Failure?) {
        newRaceLoadingVehiclesRL.invisible()
        newRaceVehiclesRV.invisible()
        newRaceRefreshButton.visible()
        when (failure) {
            is NetworkConnectionToSSID -> renderPickUpWifiDialog(R.string.new_race_no_connection_rctelemetry_network, R.string.new_race_no_connection_rctelemetry_network_message)
            is NoVehiclesFound -> renderNoVehicleDetectedDialog(R.string.new_race_no_vehicle_detected, R.string.new_race_no_vehicle_detected_message)
            is VehiculeCommunicationFailure -> renderVehicleFailure()
        }
    }

    private fun renderPickUpWifiDialog(title: Int, message: Int) {
        context!!.showDialog {
            setTitle(title)
            setMessage(message)
            positiveButton(getString(R.string.action_wifi_settings)) {
                refreshVehicles = true
                navigator.pickUpWifi(activity!!)
            }
            negativeButton(getString(R.string.action_cancel)) { renderVehiclesMessageBox(getString(R.string.new_race_no_connection_rctelemetry_network)) }
        }
    }

    private fun renderNoVehicleDetectedDialog(title: Int, message: Int) {
        context!!.showDialog {
            setTitle(title)
            setMessage(message)
            positiveButton(getString(R.string.action_ok)) { renderVehiclesMessageBox(getString(R.string.new_race_no_vehicle_detected)) }
        }
    }

    private fun renderVehiclesMessageBox(msg: String) {
        newRaceVehiclesMsgBox.text = msg
        newRaceVehiclesMsgBoxLL.visible()
    }

    private fun renderVehicleFailure() {
        if (vehiclePosition != -1) {
            newRaceViewModel.vehiclesLiveData.value!!.get(vehiclePosition).status = getString(R.string.new_race_vehicle_no_response)
            vehiclesAdapter.notifyItemChanged(vehiclePosition, getString(R.string.new_race_vehicle_no_response))
        }
    }

    private fun setNextActionButtonViews(state: Boolean) {
        val animNextButton: Animation = AnimationUtils.loadAnimation(activity!!, R.anim.next_button_anim)
        val orientation = activity!!.getResources().getConfiguration().orientation
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            nextActionMenuVisible = false
            when (state) {
                true -> {

                    newRaceNextButton.visible()
                    newRaceNextButton.startAnimation(animNextButton)
                }
                false -> {
                    newRaceNextButton.invisible()
                    newRaceNextButton.clearAnimation()
                }
            }
        } else {
            nextActionMenuVisible = state
            newRaceNextButton.invisible()
            newRaceNextButton.clearAnimation()
        }
        activity!!.invalidateOptionsMenu()
        newRaceViewModel.nextActionVisible = state
    }

    private fun setRefreshActionButtonViews(state: Boolean) {
        when (state) {
            true -> {
                newRaceRefreshButton.visible()
            }
            false -> {
                newRaceRefreshButton.invisible()
            }
        }
        refreshActionVisible = state
        activity!!.invalidateOptionsMenu()
    }

    private fun onNextActionButtonPressed() {
        goToNext(true)
    }

    private fun goToNext(goto: Boolean?) {
        if (circuitDetailsName.text.isNullOrEmpty()) {
            circuitDetailsNameTIL.error = getString(R.string.new_race_provide_circuit)
        } else if (circuitDetailsLenght.text.isNotEmpty() and (circuitDetailsLenght.text.toString().toFloatOrNull() == null)) {
            circuitDetailsLenghtTIL.error = getString(R.string.new_race_circuit_lenght_must_be_numeric)
        } else {
            var circuitView = newRaceViewModel.circuitLiveData.value
            if (circuitView == null) {
                val circuit = Circuit(
                        name = circuitDetailsName.text.toString(),
                        city = circuitDetailsCity.text.toString()
                )
                val lenght = circuitDetailsLenght.text.toString().toFloatOrNull()
                if (lenght != null) {
                    circuit.lenght = lenght
                }
                circuitView = NewRaceCircuitView(
                        id = circuit.id,
                        name = circuit.name,
                        city = circuit.city,
                        lenght = circuit.lenght!!,
                        threshold = circuit.threshold,
                        belowThresholdNum = circuit.belowThresholdNum,
                        aboveThresholdNum = circuit.aboveThresholdNum,
                        count = circuit.count)
            }
            val vehicle = newRaceViewModel.vehicleLiveData.value
            val newRaceView = NewRaceView(0, driver = newRaceDriver.text.toString(), vehicleName = vehicle!!.name, vehicleAddress = vehicle.addressId)

            navigator.reconnaissanceLap(activity!!, newRaceView, circuitView)
        }
    }
}
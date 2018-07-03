package es.danibeni.android.kotlin.rctelemetry.features.newrace


import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import es.danibeni.android.kotlin.rctelemetry.R
import es.danibeni.android.kotlin.rctelemetry.core.extension.*
import es.danibeni.android.kotlin.rctelemetry.core.navigation.Navigator
import es.danibeni.android.kotlin.rctelemetry.core.platform.BaseFragment
import kotlinx.android.synthetic.main.newrace_fragment.*
import javax.inject.Inject
import es.danibeni.android.kotlin.rctelemetry.core.exception.Failure
import es.danibeni.android.kotlin.rctelemetry.core.exception.Failure.NetworkConnectionToSSID
import es.danibeni.android.kotlin.rctelemetry.features.newrace.NewRaceFailure.VehiculeCommunicationFailure
import es.danibeni.android.kotlin.rctelemetry.features.newrace.NewRaceFailure.NoVehiclesFound
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.android.synthetic.main.vehicle_item.view.*


class NewRaceFragment : BaseFragment() {
    private val TAG: String = NewRaceFragment::class.java.simpleName
    private val REFRESH_VEHICLES = "es.danibeni.android.kotlin.rctelemetry.features.newrace.refreshvehicles"
    private var refreshVehicles: Boolean = false

    private var nextActionVisible: Boolean = false
    private var refreshActionVisible: Boolean = false

    @Inject
    lateinit var navigator: Navigator

    @Inject
    lateinit var vehiclesAdapter: VehiclesAdapter

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
            observe(vehiclesLiveData, ::renderVehiclesList)
            observe(vehicleLiveData, ::renderVehicleStatus)
            failure(failure, ::handleFailure)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeView()
    }

    private fun initializeView() {
        activity!!.toolbar.navigationIcon = ContextCompat.getDrawable(context!!, R.drawable.ic_back)
        activity!!.toolbar.setNavigationOnClickListener { activity?.onBackPressed() }
        setHasOptionsMenu(true)

        val manager = LinearLayoutManager(this.context)
        newRaceVehiclesRV.layoutManager = manager

        newRaceVehiclesRV.adapter = vehiclesAdapter
        val itemDecoration = DividerItemDecoration(activity, manager.orientation)
        newRaceVehiclesRV.addItemDecoration(itemDecoration)

        vehiclesAdapter.clickListener = { vehicle, position ->
            addRaceContainer.hideKeyboard()
            if (newRaceViewModel.lastVehicleClicked != -1) {
                newRaceViewModel.vehiclesLiveData.value!!.get(newRaceViewModel.lastVehicleClicked).status = getString(R.string.new_race_vehicle_available)
                vehiclesAdapter.notifyItemChanged(newRaceViewModel.lastVehicleClicked, getString(R.string.new_race_vehicle_available))
            }
            newRaceViewModel.vehiclesLiveData.value!!.get(position).status = getString(R.string.new_race_vehicle_blinking)
            vehiclesAdapter.notifyItemChanged(position, getString(R.string.new_race_vehicle_blinking))
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

    private fun loadVehiclesList() {
        newRaceVehiclesRV.invisible()
        newRaceVehiclesMsgBoxLL.invisible()
        setRefreshActionButtonViews(false)
        setNextActionButtonViews(false)
        newRaceLoadingVehiclesRL.visible()
        newRaceViewModel.loadVehicles()
    }

    override fun onResume() {
        super.onResume()
        if (refreshVehicles) {
            refreshVehicles = false
            loadVehiclesList()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(REFRESH_VEHICLES, refreshVehicles)

    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        if (inflater != null) {
            inflater.inflate(R.menu.new_race_nav_menu, menu)
            val next_item: MenuItem = menu!!.findItem(R.id.newRaceActionNext)
            val refresh_item: MenuItem = menu!!.findItem(R.id.newRaceActionRefresh)
            next_item.isVisible = nextActionVisible
            refresh_item.isVisible = refreshActionVisible
        }
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle presses on the action bar menu items
        when (item.itemId) {
            R.id.newRaceActionRefresh -> {
                refreshVehiclesList()
                return true
            }
            R.id.newRaceActionNext -> {
                onNextActionButtonPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun refreshVehiclesList() {
        if (newRaceViewModel.lastVehicleClicked != -1) {
            newRaceVehiclesRV.layoutManager.findViewByPosition(newRaceViewModel.lastVehicleClicked).vehicleStatusTV.text = getString(R.string.new_race_vehicle_available)
            newRaceVehiclesRV.layoutManager.findViewByPosition(newRaceViewModel.lastVehicleClicked).vehicleStatusTV.setTextColor(ContextCompat.getColor(context!!, R.color.colorTextPrimary))
        }
        loadVehiclesList()
    }

    private fun renderVehiclesList(vehicles: List<VehicleView>?) {
        if (vehicles != null) {
            vehiclesAdapter.collection = vehicles.orEmpty()
        }
        newRaceVehiclesRV.visibility = View.VISIBLE
        newRaceLoadingVehiclesRL.visibility = View.GONE
        setRefreshActionButtonViews(true)
    }

    private fun renderVehicleStatus(vehicle: VehicleView?) {
        newRaceVehiclesRV.layoutManager.findViewByPosition(newRaceViewModel.lastVehicleClicked).vehicleStatusTV.clearAnimation()
        if (vehicle != null) {
            newRaceVehiclesRV.layoutManager.findViewByPosition(newRaceViewModel.lastVehicleClicked).vehicleStatusTV.text = getString(R.string.new_race_vehicle_selected)
            newRaceNextButton.visibility = View.VISIBLE
            setNextActionButtonViews(true)
        }
    }

    private fun handleFailure(failure: Failure?) {
        newRaceLoadingVehiclesRL.visibility = View.GONE
        newRaceRefreshButton.visibility = View.VISIBLE
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
        newRaceVehiclesMsgBoxLL.visibility = View.VISIBLE
    }

    private fun renderVehicleFailure() {
        newRaceViewModel.vehiclesLiveData.value!!.get(newRaceViewModel.lastVehicleClicked).status = getString(R.string.new_race_vehicle_no_response)
        vehiclesAdapter.notifyItemChanged(newRaceViewModel.lastVehicleClicked, getString(R.string.new_race_vehicle_no_response))
    }

    private fun setNextActionButtonViews(state: Boolean) {
        val animNextButton: Animation = AnimationUtils.loadAnimation(activity!!, R.anim.next_button_anim)
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
        nextActionVisible = state
        activity!!.invalidateOptionsMenu()
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
        if (newRaceCircuit.text.isNullOrEmpty()) {
            newRaceCircuitTIL.error = getString(R.string.new_race_provide_circuit)
        } else {
            navigator.reconnaissanceLap(activity!!, newRaceViewModel.vehicleLiveData.value!!)
        }
    }
}
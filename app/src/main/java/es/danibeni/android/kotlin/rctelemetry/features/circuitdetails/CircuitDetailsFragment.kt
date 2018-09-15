package es.danibeni.android.kotlin.rctelemetry.features.circuitdetails

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat.checkSelfPermission
import android.support.v7.app.AlertDialog
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.StaggeredGridLayoutManager
import android.text.SpannableStringBuilder
import android.util.Log
import android.view.*
import android.widget.Toast
import es.danibeni.android.kotlin.rctelemetry.R
import es.danibeni.android.kotlin.rctelemetry.core.exception.Failure
import es.danibeni.android.kotlin.rctelemetry.core.extension.*
import es.danibeni.android.kotlin.rctelemetry.core.navigation.Navigator
import es.danibeni.android.kotlin.rctelemetry.core.platform.BaseFragment
import es.danibeni.android.kotlin.rctelemetry.features.circuits.CircuitFailure
import es.danibeni.android.kotlin.rctelemetry.features.newrace.LapView
import es.danibeni.android.kotlin.rctelemetry.features.newrace.LapsAdapter
import es.danibeni.android.kotlin.rctelemetry.features.races.RaceFailure
import kotlinx.android.synthetic.main.circuit_item.*
import kotlinx.android.synthetic.main.circuitdetails_fragment.*
import kotlinx.android.synthetic.main.racedetails_fragment.*
import java.util.jar.Manifest
import javax.inject.Inject

class CircuitDetailsFragment : BaseFragment() {
    private val TAG: String = CircuitDetailsFragment::class.java.simpleName
    lateinit var circuitDetailsViewModel: CircuitDetailsViewModel

    @Inject
    lateinit var navigator: Navigator

    companion object {
        private const val PARAM_CIRCUIT_ID = "param_circuit_id"
        private const val COARSE_LOCATION_REQUEST_CODE = 100

        fun forCircuitDetails(circuitId: Long): CircuitDetailsFragment {
            val circuitDetailsFragment = CircuitDetailsFragment()
            val arguments = Bundle()
            arguments.putLong(PARAM_CIRCUIT_ID, circuitId)
            circuitDetailsFragment.arguments = arguments

            return circuitDetailsFragment
        }
    }

    override fun layoutId(): Int = R.layout.circuitdetails_fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appComponent.inject(this)
        circuitDetailsViewModel = viewModel(viewModelFactory) {
            observe(circuitDetailsLiveData, ::renderCircuitDetails)
            observe(closeCircuitDetailsLiveData, ::close)
            failure(failure, ::handleFailure)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeView()
    }

    private fun initializeView() {
        setHasOptionsMenu(true)
        val circuitId = arguments?.get(PARAM_CIRCUIT_ID) as Long
        if (circuitId > 0) {
            circuitDetailsViewModel.getCircuitDetails(circuitId)
        } else {
            loadCityFromLocation()
        }
    }

    private fun loadCityFromLocation() {
        setupLocationPermissions()
    }
    private fun setupLocationPermissions() {
        val permissionLocation = checkSelfPermission(activity!!,
                android.Manifest.permission.ACCESS_COARSE_LOCATION)

        if (permissionLocation != PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "Permission to location denied")
            if (shouldShowRequestPermissionRationale(
                            android.Manifest.permission.ACCESS_COARSE_LOCATION)) {
                renderPermissionsAskDialog(R.string.circuit_details_coarse_location_permission_title, R.string.circuit_details_coarse_location_permission_message)
            } else {
                makeRequest()
            }
        } else {
            val locationManager: LocationManager =  context!!.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            try {
                val city = getCity(location)
                circuitDetailsCity.text = SpannableStringBuilder(city)
            } catch (e: Exception) {
                Snackbar.make(circuitDetailsContainer, R.string.circuit_details_city_not_located, Snackbar.LENGTH_LONG).show()
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
    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        if (inflater != null) {
            inflater.inflate(R.menu.circuit_details_nav_menu, menu)
            val delete_item: MenuItem = menu!!.findItem(R.id.circuitDetailsActionDelete)
            val circuitId = arguments?.get(PARAM_CIRCUIT_ID) as Long
            delete_item.isVisible = true
            if (circuitId == 0L) {
                delete_item.isVisible = false
            }
        }

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle presses on the action bar menu items
        when (item.itemId) {
            R.id.circuitDetailsActionDelete -> {
                deleteCircuit()
                return true
            }
            R.id.circuitDetailsActionDone -> {
                saveAndCloseCircuit()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun renderCircuitDetails(circuitView: CircuitDetailsView?) {
        if (circuitView != null) {
            circuitDetailsName.text = SpannableStringBuilder(circuitView.name)
            circuitDetailsCity.text = SpannableStringBuilder(circuitView.city)
            circuitDetailsLenght.text = SpannableStringBuilder(circuitView.lenght.toString())
        }
    }

    private fun saveAndCloseCircuit() {
        val circuitDetailsView = CircuitDetailsView(
                name = circuitDetailsName.text.toString(),
                city = circuitDetailsCity.text.toString(),
                lenght = circuitDetailsLenght.text.toString().toFloatOrNull()
        )
        if (circuitDetailsName.text.isNullOrEmpty()) {
            circuitDetailsNameTIL.error = getString(R.string.circuit_details_provide_name)
        } else if (circuitDetailsLenght.text.toString().toFloatOrNull() == null) {
            circuitDetailsLenghtTIL.error = getString(R.string.circuit_details_lenght_must_be_numeric)
        } else {
            val circuitId = arguments?.get(PARAM_CIRCUIT_ID) as Long
            if (circuitId == 0L) {
                circuitDetailsViewModel.storeNewCircuit(circuitDetailsView)
            } else {
                circuitDetailsViewModel.updateCircuitDetails(circuitDetailsView)
            }
        }
    }

    private fun deleteCircuit() {
        renderCircuitDetailsDeleteDialog(R.string.circuit_details_delete_dialog_title, R.string.circuit_details_delete_message)
    }

    private fun close(close: Boolean?) {
        activity!!.finish()
        navigator.showCircuits(activity!!)
    }

    private fun renderCircuitDetailsDeleteDialog(title: Int, message: Int) {
        context!!.showDialog {
            setTitle(title)
            setMessage(message)
            positiveButton(getString(R.string.action_yes)) {
                circuitDetailsViewModel.deleteRaces()
            }
            negativeButton(getString(R.string.action_no))
        }
    }

    private fun handleFailure(failure: Failure?) {
        when (failure) {
            is CircuitFailure.CircuitNotFound -> renderFailures("Circuit not found in database. Probably deleted.")
            is RaceFailure.RaceDeleteFailed -> renderFailures("Races run in tha Circuit could not be deleted properly")
            is CircuitFailure.CircuitDeleteFailed -> renderFailures("Circuit could not be deleted properly.")
            is CircuitFailure.CircuitNotUpdated -> renderFailures("Circuit could not be updated properly")
        }
    }

    private fun renderFailures(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

}
package es.danibeni.android.kotlin.rctelemetry.features.racedetails

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.StaggeredGridLayoutManager
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
import kotlinx.android.synthetic.main.racedetails_fragment.*
import kotlinx.android.synthetic.main.reconnaissancelap_fragment.*
import javax.inject.Inject

class RaceDetailsFragment : BaseFragment() {
    private val TAG: String = RaceDetailsFragment::class.java.simpleName
    lateinit var raceDetailsViewModel: RaceDetailsViewModel

    @Inject
    lateinit var  navigator: Navigator
    @Inject
    lateinit var lapsAdapter: LapsAdapter

    companion object {
        private const val PARAM_RACE_ID = "param_race_id"

        fun forRaceDetails(raceId: Long): RaceDetailsFragment {
            val raceDetailsFragment = RaceDetailsFragment()
            val arguments = Bundle()
            arguments.putLong(PARAM_RACE_ID, raceId)
            raceDetailsFragment.arguments = arguments

            return raceDetailsFragment
        }
    }

    override fun layoutId(): Int = R.layout.racedetails_fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appComponent.inject(this)
        raceDetailsViewModel = viewModel(viewModelFactory) {
            observe(raceDetailsLiveData, ::renderRaceDetails)
            observe(bestLapLiveData, ::renderBestLap)
            observe(closeRaceDetailsLiveData, ::close)
            failure(failure, ::handleFailure)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeView()
    }

    private fun initializeView() {
        setHasOptionsMenu(true)

        raceDetaisLapTimesRV.layoutManager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
        raceDetaisLapTimesRV.adapter = lapsAdapter
        val manager = LinearLayoutManager(this.context)
        raceDetaisLapTimesRV.layoutManager = manager
        val itemDecoration = DividerItemDecoration(context, manager.orientation)
        raceDetaisLapTimesRV.addItemDecoration(itemDecoration)

        view!!.isFocusableInTouchMode = true
        view!!.requestFocus()
        view!!.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    Snackbar.make(raceDetailsCoordLayout, R.string.race_details_back_press_disabled, Snackbar.LENGTH_SHORT).show()
                    return@setOnKeyListener true
                }
            }
            false
        }

        raceDetailsViewModel.getRaceDetails(arguments?.get(PARAM_RACE_ID) as Long)

    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        if (inflater != null) {
            inflater.inflate(R.menu.race_details_nav_menu, menu)
        }
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle presses on the action bar menu items
        when (item.itemId) {
            R.id.raceDetailsActionDelete -> {
                deleteRace()
                return true
            }
            R.id.raceDetailsActionDone -> {
                raceDetailsViewModel.closeRaceDetails()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        //super.onBackPressed()
        Snackbar.make(raceDetailsCoordLayout, R.string.race_details_back_press_disabled, Snackbar.LENGTH_SHORT).show()
    }

    private fun renderRaceDetails(raceView: RaceDetailsView?) {
        if (raceView != null) {
            raceDetailsCircuit.text = raceView.circuitName
            raceDetailsDriver.text = raceView.driver
            raceDetailsVehicle.text = raceView.vehicle
            raceDetailsRaceTime.text = raceView.raceTime
            lapsAdapter.collection = raceView.laps.orEmpty()
        }
    }

    private fun renderBestLap(lapView: LapView?) {
        if (lapView != null) {
            raceDetailsBestLapNumber.text = lapView.lapNumber.toString()
            raceDetailsBestLapTime.text = String.minutesFormat(lapView.lapTime)
            raceDetailsBestLapVel.text = String.oneDecimalFormat(lapView.lapVel)
        }
    }

    private fun deleteRace() {
        renderRaceDetailsDeleteDialog(R.string.race_details_delete_dialog_title, R.string.race_details_delete_message)
    }

    private fun close(close: Boolean?) {
        activity!!.finish()
        navigator.showMain(activity!!)
    }

    private fun renderRaceDetailsDeleteDialog(title: Int, message: Int) {
        context!!.showDialog {
            setTitle(title)
            setMessage(message)
            positiveButton(getString(R.string.action_yes)) {
                raceDetailsViewModel.deleteRace(arguments?.get(PARAM_RACE_ID) as Long)
            }
            negativeButton(getString(R.string.action_no))
        }
    }

    private fun handleFailure(failure: Failure?) {
        when (failure) {
            is RaceFailure.RaceNotFound -> renderFailures("Race not found in database. Probably deleted.")
            is RaceFailure.RaceDeleteFailed -> renderFailures("Race could not be deleted properly.")
            is RaceFailure.RaceUpdateFailed -> renderFailures("Race could not be updated properly")
            is CircuitFailure.CircuitNotUpdated -> renderFailures("Circuit could not be updated properly")
        }
    }

    private fun renderFailures(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

}
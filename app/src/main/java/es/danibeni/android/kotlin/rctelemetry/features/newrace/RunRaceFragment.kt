package es.danibeni.android.kotlin.rctelemetry.features.newrace

import android.content.Intent
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.support.v7.preference.PreferenceManager
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.StaggeredGridLayoutManager
import android.util.Log
import android.view.*
import es.danibeni.android.kotlin.rctelemetry.R
import es.danibeni.android.kotlin.rctelemetry.core.constants.Constants
import es.danibeni.android.kotlin.rctelemetry.core.exception.Failure
import es.danibeni.android.kotlin.rctelemetry.core.extension.*
import es.danibeni.android.kotlin.rctelemetry.core.navigation.Navigator
import es.danibeni.android.kotlin.rctelemetry.core.platform.BaseFragment
import es.danibeni.android.kotlin.rctelemetry.core.platform.CountDownAnimation
import es.danibeni.android.kotlin.rctelemetry.data.LapMeasures
import kotlinx.android.synthetic.main.runrace_fragment.*

import javax.inject.Inject

class RunRaceFragment : BaseFragment(), CountDownAnimation.CountDownListener {

    private val TAG: String = RunRaceFragment::class.java.simpleName
    lateinit var newRaceView: NewRaceView
    lateinit var circuitView: NewRaceCircuitView
    lateinit var runRaceViewModel: RunRaceViewModel
    lateinit var countDownAnimation: CountDownAnimation

    @Inject
    lateinit var navigator: Navigator
    @Inject
    lateinit var lapsAdapter: LapsAdapter

    private var vehicleWifiStrengthErrorTimes: Int = 0

    companion object {
        private const val PARAM_NEW_RACE = "param_new_race"
        private const val PARAM_CIRCUIT = "param_circuit"

        fun forRunRace(newRace: NewRaceView, circuit: NewRaceCircuitView): RunRaceFragment {
            val runRaceFragment = RunRaceFragment()
            val arguments = Bundle()
            arguments.putParcelable(PARAM_NEW_RACE, newRace)
            arguments.putParcelable(PARAM_CIRCUIT, circuit)
            runRaceFragment.arguments = arguments

            return runRaceFragment
        }
    }

    override fun layoutId(): Int = R.layout.runrace_fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appComponent.inject(this)
        runRaceViewModel = viewModel(viewModelFactory) {
            observe(vehicleWifiStrengthLiveData, ::checkLap)
            observe(actualLapLiveData, ::checkLapDone)
            observe(raceIdLiveData, ::handleRaceStored)
            observe(lapsLiveData, ::renderLapList)
            failure(failure, ::handleFailure)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeView()
    }

    private fun initializeView() {
        activity!!.window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        setHasOptionsMenu(true)

        newRaceView = arguments?.get(PARAM_NEW_RACE) as NewRaceView
        circuitView = arguments?.get(PARAM_CIRCUIT) as NewRaceCircuitView

        runRaceLapTimesRV.layoutManager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
        runRaceLapTimesRV.adapter = lapsAdapter
        val manager = LinearLayoutManager(this.context)
        runRaceLapTimesRV.layoutManager = manager

        val itemDecoration = DividerItemDecoration(activity, manager.orientation)
        runRaceLapTimesRV.addItemDecoration(itemDecoration)

        countDownAnimation = CountDownAnimation(runRaceCountDown, 3)
        countDownAnimation.setCountDownListener(this)

        runRaceStartButton.setOnClickListener {
            runRaceStartButton.invisible()
            runRaceStopButton.isEnabled = false
            runRaceStopButton.visible()
            vehicleWifiStrengthErrorTimes = 0
            runRaceCountDownCL.visible()
            countDownAnimation.start()
        }

        runRaceStopButton.setOnClickListener {
            runRaceStopButton.invisible()
            runRaceStartButton.visible()
            runRaceChrono.stop()
            runRaceViewModel.storeNewRace(newRaceView, circuitView.id)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        if (inflater != null) {
            inflater.inflate(R.menu.run_race_nav_menu, menu)
        }
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle presses on the action bar menu items
        when (item.itemId) {
            R.id.runRaceActionPreferences -> {
                startActivity(Intent(activity!!, RunRacePreferenceActivity::class.java))
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        runRaceVehicleWifiStrengthRefVal.text = circuitView.threshold.toString()
        runRaceVehicleRefCountVal.text = circuitView.count.toString()

        val sharedPref = PreferenceManager.getDefaultSharedPreferences(context)
        val showVehicleInfo = sharedPref.getBoolean(getString(R.string.run_race_pref_vehicle_show_info_key), false)
        if (showVehicleInfo) {
            runRaceVehicleInfoCL.visible()
        } else {
            runRaceVehicleInfoCL.invisible()
        }
    }

    override fun onCountDownTick(count: Int) {
        Log.i(TAG, "TICK COUNTDOWN")
        val toneGenerator: ToneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, 500)
        val duration = 500
        if (count == 0) {
            toneGenerator.startTone(ToneGenerator.TONE_SUP_DIAL, duration)
        } else {
            toneGenerator.startTone(ToneGenerator.TONE_PROP_BEEP, duration)
        }
        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({
            toneGenerator.release()
        }, (duration + 50).toLong())
    }

    override fun onCountDownEnd(animation: CountDownAnimation) {
        runRaceCountDownCL.invisible()
        runRaceStopButton.isEnabled = true
        runRaceChrono.base = SystemClock.elapsedRealtime()
        runRaceChrono.start()
        runRaceViewModel.measureVehicleWifiStrength(newRaceView)
    }

    private fun checkLap(wifiStrength: Float?) {
        val lapTime = SystemClock.elapsedRealtime() - runRaceChrono.base
        runRaceVehicleWifiStrengthVal.text = wifiStrength.toString()
        if (runRaceStopButton.isVisible()) {
            vehicleWifiStrengthErrorTimes = 0
        }
        runRaceViewModel.checkLapMeasures(wifiStrength!!, lapTime, circuitView)
    }

    private fun checkLapDone(lapMeasure: LapMeasures?) {
        runRaceVehicleActualCountVal.text = lapMeasure!!.count.toString()
        if (lapMeasure.done) {
            Log.i(TAG, "LAP DONE WITH TIME " + lapMeasure.time)
            runRaceChrono.stop()
            runRaceChrono.base = SystemClock.elapsedRealtime()
            runRaceChrono.start()
            runRaceViewModel.addLap(circuitView, lapMeasure.time)
        } else {
            if (runRaceStopButton.isVisible()) {
                Handler().postDelayed({
                    runRaceViewModel.measureVehicleWifiStrength(newRaceView)
                }, Constants.RCTELEMETRY_VEHICLE_WIFI_STRENGTH_REFRESH_TIME_MS.toLong())
            }
        }
    }

    private fun renderLapList(laps: MutableList<LapView>?) {
        if (laps != null) {
            lapsAdapter.collection = laps.orEmpty()
        }
        if (runRaceStopButton.isVisible()) {
            Handler().postDelayed({
                runRaceViewModel.measureVehicleWifiStrength(newRaceView)
            }, Constants.RCTELEMETRY_VEHICLE_WIFI_STRENGTH_REFRESH_TIME_MS.toLong())
        }
    }

    private fun handleRaceStored(raceId: Long?) {
        if (raceId != null) {
            Log.i(TAG, "Navigate to Race Details with ID " + raceId)
            navigator.raceDetails(activity!!, raceId)
        }
    }

    private fun handleFailure(failure: Failure?) {
        when (failure) {
            is NewRaceFailure.BadResponseFromVehicle -> handleBadResponseFromVehicle()
            is NewRaceFailure.NoLapDetected -> renderRunRaceErrorDialog(R.string.run_race_no_lap_detected_error_title, R.string.run_race_no_lap_detected_error_message)
        }
    }

    private fun handleBadResponseFromVehicle() {
        val wifiStrength = runRaceViewModel.vehicleWifiStrengthLiveData.value
        if (wifiStrength != null) {
            if (wifiStrength > Constants.RCTELEMETRY_VEHICLE_WIFI_STRENGTH_WARN) {
                vehicleWifiStrengthErrorTimes++
            }
        } else {
            vehicleWifiStrengthErrorTimes++
        }
        if (vehicleWifiStrengthErrorTimes < 5) {
            Handler().postDelayed({
                runRaceViewModel.measureVehicleWifiStrength(newRaceView)
            }, Constants.RCTELEMETRY_VEHICLE_WIFI_STRENGTH_REFRESH_TIME_MS.toLong())
        } else {
            runRaceChrono.stop()
            renderRunRaceErrorDialog(R.string.vehicle_comm_error_dialog_title, R.string.vehicle_comm_error_dialog_message)
        }

    }

    private fun renderRunRaceErrorDialog(title: Int, message: Int) {
        context!!.showDialog {
            setTitle(title)
            setMessage(message)
            positiveButton(getString(R.string.action_yes)) {
                navigator.showMain(context)
            }
            negativeButton(getString(R.string.action_no)){
                runRaceStopButton.invisible()
                runRaceStartButton.visible()
            }
        }
    }

}
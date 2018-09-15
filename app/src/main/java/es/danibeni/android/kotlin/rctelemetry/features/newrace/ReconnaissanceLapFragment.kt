package es.danibeni.android.kotlin.rctelemetry.features.newrace

import android.content.Intent
import android.content.res.Configuration
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.support.v7.preference.PreferenceManager
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import es.danibeni.android.kotlin.rctelemetry.R
import es.danibeni.android.kotlin.rctelemetry.core.constants.Constants
import es.danibeni.android.kotlin.rctelemetry.core.extension.*
import es.danibeni.android.kotlin.rctelemetry.core.platform.BaseFragment
import es.danibeni.android.kotlin.rctelemetry.core.exception.Failure
import es.danibeni.android.kotlin.rctelemetry.core.navigation.Navigator
import es.danibeni.android.kotlin.rctelemetry.core.platform.CountDownAnimation
import es.danibeni.android.kotlin.rctelemetry.features.circuits.CircuitFailure
import kotlinx.android.synthetic.main.newrace_fragment.*
import kotlinx.android.synthetic.main.reconnaissancelap_fragment.*
import javax.inject.Inject

class ReconnaissanceLapFragment : BaseFragment(), CountDownAnimation.CountDownListener {

    private val TAG: String = ReconnaissanceLapFragment::class.java.simpleName
    private lateinit var reconnaissanceLapViewModel: ReconnaissanceLapViewModel
    private lateinit var newRaceView: NewRaceView
    private lateinit var circuitView: NewRaceCircuitView

    @Inject
    lateinit var navigator: Navigator

    lateinit var countDownAnimation: CountDownAnimation

    private var nextActionVisible: Boolean = false
    private var nextActionMenuVisible: Boolean = false
    private var vehicleWifiStrengthErrorTimes: Int = 0


    companion object {
        private const val PARAM_NEW_RACE = "param_new_race"
        private const val PARAM_CIRCUIT = "param_circuit"
        private val VEHICLEWIFISTRENGTHCHARTXMIN: Double = 0.0
        private val VEHICLEWIFISTRENGTHCHARTXMAX: Double = 200.0
        private val VEHICLEWIFISTRENGTHCHARTDATAPOINTSMAX: Int = 1000

        fun forReconnaissanceLap(newRace: NewRaceView, circuit: NewRaceCircuitView): ReconnaissanceLapFragment {
            val reconnaissanceLapFragment = ReconnaissanceLapFragment()
            val arguments = Bundle()
            arguments.putParcelable(PARAM_NEW_RACE, newRace)
            arguments.putParcelable(PARAM_CIRCUIT, circuit)
            reconnaissanceLapFragment.arguments = arguments

            return reconnaissanceLapFragment
        }
    }

    override fun layoutId(): Int = R.layout.reconnaissancelap_fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appComponent.inject(this)
        reconnaissanceLapViewModel = viewModel(viewModelFactory) {
            observe(vehicleWifiStrengthLiveData, ::renderVehicleWifiStrength)
            observe(circuitPatternLiveData, ::handleCircuitPatternUpdated)
            observe(circuitLiveData, ::handleCircuitStored)
            failure(failure, ::handleFailure)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeView()
    }

    private fun initializeView() {
        setHasOptionsMenu(true)

        newRaceView = arguments?.get(PARAM_NEW_RACE) as NewRaceView
        circuitView = arguments?.get(PARAM_CIRCUIT) as NewRaceCircuitView

        recLapVehicleNameVal.text = newRaceView.vehicleName
        recLapVehicleIPAddressVal.text = newRaceView.vehicleAddress
        reconnaissanceLapViewModel.vehicleWifiStrengthGraphSeries = LineGraphSeries<DataPoint>()
        recLapVehicleWifiStrengthGV.viewport.isXAxisBoundsManual = true
        recLapVehicleWifiStrengthGV.viewport.setMinX(VEHICLEWIFISTRENGTHCHARTXMIN)
        recLapVehicleWifiStrengthGV.viewport.setMaxX(VEHICLEWIFISTRENGTHCHARTXMAX)

        if (circuitView.threshold != 0F && circuitView.count != 0) {
            renderRecLapDialogForExistingCircuit()
        }

        countDownAnimation = CountDownAnimation(recLapCountDown, 3)
        countDownAnimation.setCountDownListener(this)

        recLapStartButton.setOnClickListener {
            recLapStartButton.invisible()
            recLapStopButton.visible()
            recLapStopButton.isEnabled = false
            setNextActionButtonViews(false)
            reconnaissanceLapViewModel.vehicleWifiStrengthLiveData.value = mutableListOf()
            recLapVehicleWifiStrengthGV.removeAllSeries()
            reconnaissanceLapViewModel.vehicleWifiStrengthGraphSeries.resetData(arrayOf())
            reconnaissanceLapViewModel.vehicleWifiStrengthGraphSeries.color = ContextCompat.getColor(context!!, R.color.colorGraphOne)
            recLapVehicleWifiStrengthGV.addSeries(reconnaissanceLapViewModel.vehicleWifiStrengthGraphSeries)
            vehicleWifiStrengthErrorTimes = 0
            recLapCountDownCL.visible()
            countDownAnimation.start()
        }

        recLapStopButton.setOnClickListener {
            recLapChrono.stop()
            recLapStopButton.invisible()
            recLapStartButton.visible()
            val wifiStrengths = reconnaissanceLapViewModel.vehicleWifiStrengthLiveData.value!!.toList()
            val sharedPref = PreferenceManager.getDefaultSharedPreferences(context)
            val circuitThresholdMarginStr = sharedPref.getString(getString(R.string.rec_lap_pref_circuit_threshold_margin_key), "5")
            val circuitThresholdMargin = circuitThresholdMarginStr.toFloatOrNull()
            if (circuitThresholdMargin != null) {
                reconnaissanceLapViewModel.setCircuitPattern(circuitView, wifiStrengths, circuitThresholdMargin)
            }
        }

        recLapNextButton.setOnClickListener {
            onNextActionButtonPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        if (inflater != null) {
            inflater.inflate(R.menu.rec_lap_nav_menu, menu)
            val next_item: MenuItem = menu!!.findItem(R.id.recLapActionNext)
            next_item.isVisible = nextActionMenuVisible
        }
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle presses on the action bar menu items
        when (item.itemId) {
            R.id.recLapActionPreferences -> {
                startActivity(Intent(activity!!, ReconnaissanceLapPreferenceActivity::class.java))
                return true
            }
            R.id.recLapActionNext -> {
                onNextActionButtonPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(context)
        val showInstructions = sharedPref.getBoolean(getString(R.string.rec_lap_show_instructions_pref_key), true)
        if (showInstructions) {
            reclapInstructionCL.visible()
            recLapInstructOKButton.setOnClickListener {
                reclapInstructionCL.invisible()
            }
        }
    }

    override fun onCountDownTick(count: Int) {
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
        recLapCountDownCL.invisible()
        recLapStopButton.isEnabled = true
        recLapChrono.base = SystemClock.elapsedRealtime()
        recLapChrono.start()
        reconnaissanceLapViewModel.measureVehicleWifiStrength((arguments?.get(PARAM_NEW_RACE) as NewRaceView))
    }

    private fun renderRecLapDialogForExistingCircuit() {
        context!!.showDialog {
            setTitle(getString(R.string.rec_lap_skip_dialog_title))
            setMessage(getString(R.string.rec_lap_skip_dialog_message))
            positiveButton(getString(R.string.action_yes)) {
                reconnaissanceLapViewModel.circuitLiveData.value = circuitView
            }
            negativeButton(getString(R.string.action_no)) {

            }
        }
    }

    private fun renderVehicleWifiStrength(wifiStrengths: MutableList<Float>?) {
        if (wifiStrengths!!.isNotEmpty()) {
            Log.i(TAG, "Last distance: " + wifiStrengths[wifiStrengths.lastIndex])
            val lastWifiStrength = wifiStrengths[wifiStrengths.lastIndex]
            recLapVehicleWifiStrengthVal.text = lastWifiStrength.toString()
            if (lastWifiStrength in Constants.RCTELEMETRY_VEHICLE_WIFI_STRENGTH_OK..0) {
                recLapVehicleWifiStrengthVal.setTextColor(ContextCompat.getColor(context!!, R.color.colorOK))
            } else if (lastWifiStrength in Constants.RCTELEMETRY_VEHICLE_WIFI_STRENGTH_WARN..-Constants.RCTELEMETRY_VEHICLE_WIFI_STRENGTH_OK) {
                recLapVehicleWifiStrengthVal.setTextColor(ContextCompat.getColor(context!!, R.color.colorWarning))
            } else {
                recLapVehicleWifiStrengthVal.setTextColor(ContextCompat.getColor(context!!, R.color.colorError))
            }
            reconnaissanceLapViewModel.vehicleWifiStrengthGraphSeries.appendData(
                    DataPoint(recLapChrono.timeElapsed.asTimeSec, lastWifiStrength.toDouble()),
                    true,
                    VEHICLEWIFISTRENGTHCHARTDATAPOINTSMAX)
            if (recLapStopButton.isVisible()) {
                vehicleWifiStrengthErrorTimes = 0
                Handler().postDelayed({
                    reconnaissanceLapViewModel.measureVehicleWifiStrength(newRaceView)
                }, Constants.RCTELEMETRY_VEHICLE_WIFI_STRENGTH_REFRESH_TIME_MS.toLong())
            }
        }
    }

    private fun handleFailure(failure: Failure?) {
        when (failure) {
            is NewRaceFailure.BadResponseFromVehicle -> handleBadResponseFromVehicle()
            is CircuitFailure.ListNotAvailable -> Snackbar.make(recLapCoordLayout, R.string.circuits_not_available_error_message, Snackbar.LENGTH_SHORT).show()
            is CircuitFailure.NotEnoughDataForPattern -> renderReconnaissanceLapErrorDialog(
                    R.string.circuit_not_enough_data_pattern_error_dialog_title,
                    R.string.circuit_not_enough_data_pattern_error_dialog_message)
            is CircuitFailure.NotFinishLineCrossingDetected -> renderReconnaissanceLapErrorDialog(
                    R.string.circuit_not_enough_data_finish_detection_dialog_title,
                    R.string.circuit_not_enough_data_pattern_error_dialog_message
            )
        }
    }

    private fun handleBadResponseFromVehicle() {
        val wifiStrengths = reconnaissanceLapViewModel.vehicleWifiStrengthLiveData.value
        if (wifiStrengths!!.isNotEmpty()) {
            if (wifiStrengths[wifiStrengths.lastIndex] > Constants.RCTELEMETRY_VEHICLE_WIFI_STRENGTH_WARN) {
                vehicleWifiStrengthErrorTimes++
            }
        } else {
            vehicleWifiStrengthErrorTimes++
        }
        if (vehicleWifiStrengthErrorTimes < Constants.RCTELEMETRY_VEHICLE_WIFI_STRENGTH_MAX_COMM_ERROR) {
            Handler().postDelayed({
                reconnaissanceLapViewModel.measureVehicleWifiStrength(newRaceView)
            }, Constants.RCTELEMETRY_VEHICLE_WIFI_STRENGTH_REFRESH_TIME_MS.toLong())
        } else {
            recLapChrono.stop()
            renderReconnaissanceLapErrorDialog(R.string.vehicle_comm_error_dialog_title, R.string.vehicle_comm_error_dialog_message)
        }

    }

    private fun renderReconnaissanceLapErrorDialog(title: Int, message: Int) {
        context!!.showDialog {
            setTitle(title)
            setMessage(message)
            positiveButton(getString(R.string.action_ok)) {
                recLapStopButton.invisible()
                recLapStartButton.visible()
            }
        }
    }

    private fun setNextActionButtonViews(state: Boolean) {
        val animNextButton: Animation = AnimationUtils.loadAnimation(activity!!, R.anim.next_button_anim)
        val orientation = activity!!.getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            nextActionMenuVisible = false
            when (state) {
                true -> {
                    recLapNextButton.visible()
                    recLapNextButton.startAnimation(animNextButton)
                }
                false -> {
                    recLapNextButton.invisible()
                    recLapNextButton.clearAnimation()
                }
            }
        } else {
            nextActionMenuVisible = state
            recLapNextButton.invisible()
            recLapNextButton.clearAnimation()
        }
        nextActionVisible = state
        activity!!.invalidateOptionsMenu()
    }

    private fun onNextActionButtonPressed() {
        if (reconnaissanceLapViewModel.circuitPatternLiveData.value != null) {
            reconnaissanceLapViewModel.storeCircuit(reconnaissanceLapViewModel.circuitPatternLiveData.value!!)
        }
    }

    private fun handleCircuitPatternUpdated(circuitView: NewRaceCircuitView?) {
        if (circuitView != null) {
            recLapCircuitThresholdVal.text = circuitView.threshold.toString()
            setNextActionButtonViews(true)
        }
    }

    private fun handleCircuitStored(circuitView: NewRaceCircuitView?) {
        if (circuitView != null) {
            navigator.runRace(activity!!, newRaceView, circuitView)
        }
    }
}
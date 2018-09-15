package es.danibeni.android.kotlin.rctelemetry.features.newrace

import android.arch.lifecycle.MutableLiveData
import android.util.Log
import es.danibeni.android.kotlin.rctelemetry.core.platform.BaseViewModel
import es.danibeni.android.kotlin.rctelemetry.data.*
import es.danibeni.android.kotlin.rctelemetry.features.races.RaceView
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class RunRaceViewModel
@Inject constructor(private val getVehicleWifiStrength: GetVehicleWifiStrength,
                    private val checkLap: CheckLapDone,
                    private val addLaps: AddLaps,
                    private val insertNewRace: InsertNewRace) : BaseViewModel() {
    private val TAG: String = RunRaceViewModel::class.java.simpleName

    var vehicleWifiStrengthLiveData: MutableLiveData<Float> = MutableLiveData()
    var actualLapLiveData: MutableLiveData<LapMeasures> = MutableLiveData()
    var lapsLiveData: MutableLiveData<MutableList<LapView>> = MutableLiveData()
    var raceIdLiveData: MutableLiveData<Long> = MutableLiveData()

    fun measureVehicleWifiStrength(newRaceView: NewRaceView) {
        val vehicle = Vehicle(address = newRaceView.vehicleAddress)
        getVehicleWifiStrength.execute({ it.either(::handleFailure, ::handleVehicleWifiStrength) }, GetVehicleWifiStrength.Params(vehicle))
    }

    private fun handleVehicleWifiStrength(wifiStrength: Float) {
        this.vehicleWifiStrengthLiveData.value = wifiStrength
    }

    fun checkLapMeasures(wifiStrength: Float, chronoTime: Long, circuitView: NewRaceCircuitView) {
        val circuit = Circuit(threshold = circuitView.threshold, count = circuitView.count)
        var lapMeasure = LapMeasures(circuit.belowThresholdNum + circuit.aboveThresholdNum)
        if (actualLapLiveData.value != null) {
            lapMeasure = actualLapLiveData.value!!
        }
        lapMeasure.shiftMeasures(wifiStrength)
        lapMeasure.time = chronoTime

        checkLap.execute({ it.either(::handleFailure, ::handleLapChecking) }, CheckLapDone.Params(circuit, lapMeasure))
    }

    private fun handleLapChecking(lapMeasure: LapMeasures) {
        if (lapMeasure.done) {
            lapMeasure.count = 0
        }
        actualLapLiveData.value = lapMeasure

    }

    fun addLap(circuitView: NewRaceCircuitView, lapTime: Long) {
        var lapNumber = 1
        var lapDiff = 0L
        val lapVelocity = circuitView.lenght * 3600 / lapTime
        Log.i(TAG, "VELOCITY: " + lapVelocity)
        var laps = this.lapsLiveData.value

        if (laps != null) {
            val lastLap = this.lapsLiveData.value!!.get(0)
            lapNumber = lastLap.lapNumber + 1
            lapDiff = lapTime - lastLap.lapTime
        } else {
            laps = mutableListOf()
        }

        val lapView = LapView(lapNumber = lapNumber, lapTime = lapTime, lapDiff = lapDiff, lapVel = lapVelocity)
        laps.add(0, lapView)
        this.lapsLiveData.value = laps
    }

    fun storeNewRace(newRaceView: NewRaceView, circuitId: Long) {
        val currentTime = Calendar.getInstance().time
        val race = Race(driver = newRaceView.driver, vehicleName = newRaceView.vehicleName, vehicleAddress = newRaceView.vehicleAddress, raceDate = currentTime, circuitId = circuitId)
        val lapsView = this.lapsLiveData.value
        var laps = listOf<Lap>()
        if (lapsView != null) {
            laps = lapsView.map { Lap(it.lapNumber, it.lapTime, it.lapDiff, it.lapVel) }
        }
        insertNewRace.execute({ it.either(::handleFailure, ::handleNewRaceStoreResponse) }, InsertNewRace.Params(race, laps))
    }

    private fun handleNewRaceStoreResponse(race: Race) {
        raceIdLiveData.value = race.id
    }
}
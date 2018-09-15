package es.danibeni.android.kotlin.rctelemetry.features.newrace

import android.arch.lifecycle.MutableLiveData
import android.util.Log
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import es.danibeni.android.kotlin.rctelemetry.core.platform.BaseViewModel
import es.danibeni.android.kotlin.rctelemetry.data.Circuit
import es.danibeni.android.kotlin.rctelemetry.data.Vehicle
import javax.inject.Inject

class ReconnaissanceLapViewModel
@Inject constructor(private val getVehicleWifiStrength: GetVehicleWifiStrength,
                    private val calcCircuitPattern: CalcCircuitPattern,
                    private val insertCircuit: InsertCircuit,
                    private val updateCircuit: UpdateCircuit) : BaseViewModel() {
    private val TAG: String = ReconnaissanceLapViewModel::class.java.simpleName

    var vehicleWifiStrengthLiveData: MutableLiveData<MutableList<Float>> = MutableLiveData()
    var vehicleWifiStrengthGraphSeries: LineGraphSeries<DataPoint> = LineGraphSeries<DataPoint>()
    var circuitPatternLiveData: MutableLiveData<NewRaceCircuitView> = MutableLiveData()
    var circuitLiveData: MutableLiveData<NewRaceCircuitView> = MutableLiveData()

    fun measureVehicleWifiStrength(newRaceView: NewRaceView) {
        val vehicle = Vehicle(name = newRaceView.vehicleName, address = newRaceView.vehicleAddress)
        getVehicleWifiStrength.execute({ it.either(::handleFailure, ::handleVehicleWifiStrength) }, GetVehicleWifiStrength.Params(vehicle))
    }

    private fun handleVehicleWifiStrength(wifiStrength: Float) {
        var wifiStrengths = this.vehicleWifiStrengthLiveData.value
        if (wifiStrengths == null) {
            wifiStrengths = mutableListOf<Float>()
        }
        wifiStrengths.add(wifiStrength)
        this.vehicleWifiStrengthLiveData.value = wifiStrengths
    }

    fun setCircuitPattern(circuitView: NewRaceCircuitView, measures: List<Float>, thresholdMargin: Float) {
        val circuit = Circuit(
                id = circuitView.id,
                name = circuitView.name,
                city = circuitView.city,
                lenght = circuitView.lenght,
                threshold = circuitView.threshold,
                belowThresholdNum = circuitView.belowThresholdNum,
                aboveThresholdNum = circuitView.aboveThresholdNum,
                count = circuitView.count)
        calcCircuitPattern.execute({ it.either(::handleFailure, ::handleCircuitPattern)}, CalcCircuitPattern.Params(circuit, measures, thresholdMargin))
    }

    private fun handleCircuitPattern(circuit: Circuit) {
        circuitPatternLiveData.value = NewRaceCircuitView(
                id = circuit.id,
                name = circuit.name,
                city = circuit.city,
                lenght = circuit.lenght!!,
                threshold = circuit.threshold,
                belowThresholdNum = circuit.belowThresholdNum,
                aboveThresholdNum = circuit.aboveThresholdNum,
                count = circuit.count)
    }

    fun storeCircuit(circuitView: NewRaceCircuitView) {
        val circuit = Circuit(
                id = circuitView.id,
                name = circuitView.name,
                city = circuitView.city,
                lenght = circuitView.lenght,
                threshold = circuitView.threshold,
                belowThresholdNum = circuitView.belowThresholdNum,
                aboveThresholdNum = circuitView.aboveThresholdNum,
                count = circuitView.count)
        if (circuit.id == 0L) {
            insertCircuit.execute({ it.either(::handleFailure, ::handleCircuitStoreResponse) }, InsertCircuit.Params(circuit))
        } else {
            updateCircuit.execute({ it.either(::handleFailure, ::handleCircuitStoreResponse) }, UpdateCircuit.Params(circuit))
        }
    }

    private fun handleCircuitStoreResponse(circuit: Circuit) {
        this.circuitLiveData.value = NewRaceCircuitView(
                id = circuit.id,
                name = circuit.name,
                city = circuit.city,
                lenght = circuit.lenght!!,
                threshold = circuit.threshold,
                belowThresholdNum = circuit.belowThresholdNum,
                aboveThresholdNum = circuit.aboveThresholdNum,
                count = circuit.count)
    }
}
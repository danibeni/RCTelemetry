package es.danibeni.android.kotlin.rctelemetry.data

import android.util.Log
import es.danibeni.android.kotlin.rctelemetry.core.constants.Constants
import es.danibeni.android.kotlin.rctelemetry.core.exception.Failure
import es.danibeni.android.kotlin.rctelemetry.core.functional.Either
import es.danibeni.android.kotlin.rctelemetry.data.local.CircuitsDao
import es.danibeni.android.kotlin.rctelemetry.features.circuits.CircuitFailure
import es.danibeni.android.kotlin.rctelemetry.features.newrace.ReconnaissanceLapViewModel


interface CircuitsRepository {
    fun circuits(): Either<Failure, List<Circuit>>
    fun circuit(circuitId: Long): Either<Failure, Circuit>
    fun addCircuitPattern(circuit: Circuit, measures: List<Float>, thresholdMargin: Float): Either<Failure, Circuit>
    fun addCircuit(circuit: Circuit): Either<Failure, Circuit>
    fun checkLap(circuit: Circuit, lapMeasure: LapMeasures): Either<Failure, LapMeasures>
    fun updateCircuit(circuit: Circuit): Either<Failure, Circuit>
    fun updateCircuitName(circuitId: Long, circuitName: String): Either<Failure, Int>
    fun deleteCircuit(circuitId: Long): Either<Failure, Int>

    class LocalDataSource
    constructor(private val circuitsDao: CircuitsDao) : CircuitsRepository {

        private val TAG: String = CircuitsRepository::class.java.simpleName
        override fun circuits(): Either<Failure, List<Circuit>> {
            val circuits = circuitsDao.circuits
            when (circuits.isEmpty())  {
                true -> return Either.Left(CircuitFailure.ListNotAvailable())
                false -> return Either.Right(circuits)
            }
        }

        override fun circuit(circuitId: Long): Either<Failure, Circuit> {
            val circuit = circuitsDao.getCircuitById(circuitId)
            return Either.Right(circuit)
        }

        override fun addCircuitPattern(circuit: Circuit, measures: List<Float>, thresholdMargin: Float): Either<Failure, Circuit> {
            return circuitLapPatternFromWifiStrength(circuit, measures, thresholdMargin)
        }

        fun circuitLapPatternFromWifiStrength(circuit: Circuit, measures: List<Float>, thresholdMargin: Float): Either<Failure, Circuit> {
            val wifiStrengthsNumberForThresholdCalc = (Constants.RCTELEMETRY_VEHICLE_WIFI_STRENGTH_THRESHOLD_PERIOD_MS / Constants.RCTELEMETRY_VEHICLE_WIFI_STRENGTH_REFRESH_TIME_MS)
            val wifiStrengthsNumberSkipForThresholdCalc = (Constants.RCTELEMETRY_VEHICLE_WIFI_STRENGTH_SKIP_PERIOD_MS/ Constants.RCTELEMETRY_VEHICLE_WIFI_STRENGTH_REFRESH_TIME_MS)
            if (measures != null) {
                if (measures.size > wifiStrengthsNumberForThresholdCalc + wifiStrengthsNumberSkipForThresholdCalc) {
                    val threshold = measures.subList(0, wifiStrengthsNumberForThresholdCalc).average().toFloat() - thresholdMargin
                    Log.i(TAG, "Threshold = " + threshold)
                    val wifiStrengthsWithoutThresholdCalc = measures.subList(wifiStrengthsNumberForThresholdCalc + wifiStrengthsNumberSkipForThresholdCalc, measures.size)
                    var aboveThresholdCounter = 0
                    val measuresForThresholdCount = circuit.belowThresholdNum + circuit.aboveThresholdNum
                    val measuresBelowThresholdNumer = circuit.belowThresholdNum
                    Log.i(TAG, wifiStrengthsWithoutThresholdCalc.toString())
                    wifiStrengthsWithoutThresholdCalc.forEachIndexed { index, _ ->
                        if ((index + measuresForThresholdCount) < wifiStrengthsWithoutThresholdCalc.size) {
                            val measuresForThreshold = wifiStrengthsWithoutThresholdCalc.subList(index, index + measuresForThresholdCount)
                            if (circutiLapThresholdDetection(measuresForThreshold, measuresBelowThresholdNumer, threshold)) {
                                aboveThresholdCounter++
                            }
                        }
                    }
                    if (aboveThresholdCounter > 0) {
                        circuit.threshold = threshold
                        circuit.count = aboveThresholdCounter
                        return Either.Right(circuit)
                    } else {
                        return Either.Left(CircuitFailure.NotFinishLineCrossingDetected())
                    }
                } else {
                    return Either.Left(CircuitFailure.NotEnoughDataForPattern())
                }
            } else {
                return Either.Left(CircuitFailure.ListNotAvailable())
            }
        }

        fun circutiLapThresholdDetection(measures: List<Float>, numBelowThreshold: Int, threshold: Float): Boolean {
            var isThreshold = false
            val measuresBelowThreshold = measures.subList(0, numBelowThreshold).filter { it <= threshold }
            val measuresAboveThreshold = measures.subList(numBelowThreshold, measures.size).filter { it > threshold }
            Log.i(TAG, "aboveThreshold = " + measuresAboveThreshold)
            Log.i(TAG, "belowThreshold = " + measuresBelowThreshold)
            if ((measuresBelowThreshold.size == numBelowThreshold)
                    and (measuresAboveThreshold.size == measures.size - numBelowThreshold)) {
                Log.i(TAG, "THRESHOLD REACHED...........................")
                isThreshold = true
            }
            return isThreshold
        }

        override fun addCircuit(circuit: Circuit): Either<Failure, Circuit> {
            circuit.id = circuitsDao.insertCircuit(circuit)
            when (circuit.id > 0) {
                true -> return Either.Right(circuit)
                false -> return Either.Left(CircuitFailure.CircuitNotInserted())
            }
        }

        override fun checkLap(circuit: Circuit, lapMeasure: LapMeasures): Either<Failure, LapMeasures> {
            val measures = lapMeasure.measures

            if (circutiLapThresholdDetection(measures, circuit.belowThresholdNum, circuit.threshold)) {
                lapMeasure.count++
            }
            lapMeasure.done = lapMeasure.count == circuit.count

            Log.i(TAG, "Actual count: " + lapMeasure.count)
            return Either.Right(lapMeasure)
        }

        override fun updateCircuit(circuit: Circuit): Either<Failure, Circuit> {
            val circuitsUpdated = circuitsDao.updateCircuit(circuit)
            when (circuitsUpdated == 1) {
                true -> return Either.Right(circuit)
                false -> return Either.Left(CircuitFailure.CircuitNotUpdated())
            }
        }

        override fun updateCircuitName(circuitId: Long, circuitName: String): Either<Failure, Int> {
            val circuitsUpdated = circuitsDao.updateCircuitName(circuitId, circuitName)
            when (circuitsUpdated == 1) {
                true -> return Either.Right(circuitsUpdated)
                false -> return Either.Left(CircuitFailure.CircuitNotUpdated())
            }
        }

        override fun deleteCircuit(circuitId: Long): Either<Failure, Int> {
            val circuitsDeleted = circuitsDao.deleteCircuitById(circuitId)
            return when (circuitsDeleted == 1) {
                true -> Either.Right(circuitsDeleted)
                false -> Either.Left(CircuitFailure.CircuitDeleteFailed())
            }
        }
    }

}
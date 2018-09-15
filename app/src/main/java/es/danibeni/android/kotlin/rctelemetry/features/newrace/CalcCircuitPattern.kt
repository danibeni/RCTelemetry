package es.danibeni.android.kotlin.rctelemetry.features.newrace

import es.danibeni.android.kotlin.rctelemetry.core.interactor.UseCase
import es.danibeni.android.kotlin.rctelemetry.data.Circuit
import es.danibeni.android.kotlin.rctelemetry.data.CircuitsRepository
import javax.inject.Inject

class CalcCircuitPattern @Inject constructor(private val circuitsRepository: CircuitsRepository) : UseCase<Circuit, CalcCircuitPattern.Params>() {

    override suspend fun run(params: Params) = circuitsRepository.addCircuitPattern(params.circuit, params.measures, params.thresholdMargin)

    data class Params(val circuit: Circuit, val measures: List<Float>, val thresholdMargin: Float)
}
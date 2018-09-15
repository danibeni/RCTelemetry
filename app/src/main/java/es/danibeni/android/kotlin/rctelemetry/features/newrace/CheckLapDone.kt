package es.danibeni.android.kotlin.rctelemetry.features.newrace

import es.danibeni.android.kotlin.rctelemetry.core.interactor.UseCase
import es.danibeni.android.kotlin.rctelemetry.data.*
import javax.inject.Inject

class CheckLapDone @Inject constructor(private val circuitsRepository: CircuitsRepository) : UseCase<LapMeasures, CheckLapDone.Params>() {

    override suspend fun run(params: Params) = circuitsRepository.checkLap(params.circuit, params.lapMeasure)

    data class Params(val circuit: Circuit, val lapMeasure: LapMeasures )
}
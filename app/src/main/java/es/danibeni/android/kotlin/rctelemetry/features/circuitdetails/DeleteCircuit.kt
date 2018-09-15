package es.danibeni.android.kotlin.rctelemetry.features.circuitdetails

import es.danibeni.android.kotlin.rctelemetry.core.interactor.UseCase
import es.danibeni.android.kotlin.rctelemetry.data.CircuitsRepository
import es.danibeni.android.kotlin.rctelemetry.data.RacesRepository
import javax.inject.Inject

class DeleteCircuit @Inject constructor(private val circuitsRepository: CircuitsRepository) : UseCase<Int, DeleteCircuit.Params>() {

    override suspend fun run(params: Params) = circuitsRepository.deleteCircuit(params.circuitId)

    data class Params(val circuitId: Long)
}
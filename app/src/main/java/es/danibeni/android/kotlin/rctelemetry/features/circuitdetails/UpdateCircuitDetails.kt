package es.danibeni.android.kotlin.rctelemetry.features.circuitdetails

import es.danibeni.android.kotlin.rctelemetry.core.interactor.UseCase
import es.danibeni.android.kotlin.rctelemetry.data.Circuit
import es.danibeni.android.kotlin.rctelemetry.data.CircuitsRepository
import javax.inject.Inject

class UpdateCircuitDetails @Inject constructor(private val circuitsRepository: CircuitsRepository) : UseCase<Circuit, UpdateCircuitDetails.Params>() {

    override suspend fun run(params: Params) = circuitsRepository.updateCircuit(params.circuit)

    data class Params(val circuit: Circuit)
}
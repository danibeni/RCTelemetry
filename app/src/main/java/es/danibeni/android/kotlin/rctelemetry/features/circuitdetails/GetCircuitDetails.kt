package es.danibeni.android.kotlin.rctelemetry.features.circuitdetails

import es.danibeni.android.kotlin.rctelemetry.core.interactor.UseCase
import es.danibeni.android.kotlin.rctelemetry.data.Circuit
import es.danibeni.android.kotlin.rctelemetry.data.CircuitsRepository
import es.danibeni.android.kotlin.rctelemetry.data.RaceAndCircuit
import es.danibeni.android.kotlin.rctelemetry.data.RacesRepository
import javax.inject.Inject

class GetCircuitDetails @Inject constructor(private val circuitsRepository: CircuitsRepository) : UseCase<Circuit, GetCircuitDetails.Params>() {

    override suspend fun run(params: Params) = circuitsRepository.circuit(params.circuitId)

    data class Params(val circuitId: Long)
}
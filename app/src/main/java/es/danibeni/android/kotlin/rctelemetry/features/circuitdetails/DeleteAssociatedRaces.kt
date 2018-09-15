package es.danibeni.android.kotlin.rctelemetry.features.circuitdetails

import es.danibeni.android.kotlin.rctelemetry.core.interactor.UseCase
import es.danibeni.android.kotlin.rctelemetry.data.RacesRepository
import javax.inject.Inject

class DeleteAssociatedRaces @Inject constructor(private val racesRepository: RacesRepository) : UseCase<Int, DeleteAssociatedRaces.Params>() {

    override suspend fun run(params: Params) = racesRepository.deleteRacesForCircuit(params.circuitId)

    data class Params(val circuitId: Long)
}
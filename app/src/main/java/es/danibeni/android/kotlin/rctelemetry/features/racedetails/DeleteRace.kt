package es.danibeni.android.kotlin.rctelemetry.features.racedetails

import es.danibeni.android.kotlin.rctelemetry.core.interactor.UseCase
import es.danibeni.android.kotlin.rctelemetry.data.RacesRepository
import javax.inject.Inject

class DeleteRace @Inject constructor(private val racesRepository: RacesRepository) : UseCase<Int, DeleteRace.Params>() {

    override suspend fun run(params: Params) = racesRepository.deleteRace(params.raceId)

    data class Params(val raceId: Long)
}
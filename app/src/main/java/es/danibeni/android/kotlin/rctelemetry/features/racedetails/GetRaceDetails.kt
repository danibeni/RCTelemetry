package es.danibeni.android.kotlin.rctelemetry.features.racedetails

import es.danibeni.android.kotlin.rctelemetry.core.interactor.UseCase
import es.danibeni.android.kotlin.rctelemetry.data.RaceAndCircuit
import es.danibeni.android.kotlin.rctelemetry.data.RacesRepository
import javax.inject.Inject

class GetRaceDetails @Inject constructor(private val racesRepository: RacesRepository) : UseCase<RaceAndCircuit, GetRaceDetails.Params>() {

    override suspend fun run(params: Params) = racesRepository.raceAndCircuit(params.raceId)

    data class Params(val raceId: Long)
}
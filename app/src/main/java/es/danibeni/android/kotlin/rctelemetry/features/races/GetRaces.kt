package es.danibeni.android.kotlin.rctelemetry.features.races

import es.danibeni.android.kotlin.rctelemetry.core.interactor.UseCase
import es.danibeni.android.kotlin.rctelemetry.data.RacesRepository
import es.danibeni.android.kotlin.rctelemetry.data.Race
import es.danibeni.android.kotlin.rctelemetry.data.RaceAndCircuit
import javax.inject.Inject

class GetRaces
@Inject constructor(private val racesRepository: RacesRepository) : UseCase<List<RaceAndCircuit>, UseCase.None>() {

    override suspend fun run(params: None) = racesRepository.racesAndCircuits()
}
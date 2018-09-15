package es.danibeni.android.kotlin.rctelemetry.features.newrace

import es.danibeni.android.kotlin.rctelemetry.core.interactor.UseCase
import es.danibeni.android.kotlin.rctelemetry.data.*
import javax.inject.Inject

class InsertNewRace @Inject constructor(private val racesRepository: RacesRepository) : UseCase<Race, InsertNewRace.Params>() {

    override suspend fun run(params: Params) = racesRepository.addRace(params.race, params.laps)

    data class Params(val race: Race, val laps: List<Lap>)
}
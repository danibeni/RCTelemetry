package es.danibeni.android.kotlin.rctelemetry.features.newrace

import es.danibeni.android.kotlin.rctelemetry.core.interactor.UseCase
import es.danibeni.android.kotlin.rctelemetry.data.*
import javax.inject.Inject

class AddLaps @Inject constructor(private val racesRepository: RacesRepository) : UseCase<Int, AddLaps.Params>() {

    override suspend fun run(params: Params) = racesRepository.addLapsToRace(params.raceId, params.laps)

    data class Params(val raceId: Long, val laps: List<Lap> )
}
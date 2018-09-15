package es.danibeni.android.kotlin.rctelemetry.features.newrace

import es.danibeni.android.kotlin.rctelemetry.core.interactor.UseCase
import es.danibeni.android.kotlin.rctelemetry.data.RacesRepository
import javax.inject.Inject

class GetDrivers @Inject constructor(private val racesRepository: RacesRepository) : UseCase<List<String>, UseCase.None>() {

    override suspend fun run(params: None) = racesRepository.drivers()
}
package es.danibeni.android.kotlin.rctelemetry.features.circuits

import es.danibeni.android.kotlin.rctelemetry.core.interactor.UseCase
import es.danibeni.android.kotlin.rctelemetry.data.Circuit
import es.danibeni.android.kotlin.rctelemetry.data.CircuitsRepository
import javax.inject.Inject

class GetCircuits @Inject constructor(private val circuitsRepository: CircuitsRepository) : UseCase<List<Circuit>, UseCase.None>() {

    override suspend fun run(params: None) = circuitsRepository.circuits()
}
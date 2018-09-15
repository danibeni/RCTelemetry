package es.danibeni.android.kotlin.rctelemetry.features.newrace

import es.danibeni.android.kotlin.rctelemetry.core.interactor.UseCase
import es.danibeni.android.kotlin.rctelemetry.data.Vehicle
import es.danibeni.android.kotlin.rctelemetry.data.VehiclesRepository
import javax.inject.Inject

class GetVehicles @Inject constructor(private val vehiclesRepository: VehiclesRepository.Network) : UseCase<List<Vehicle>, UseCase.None>() {

    override suspend fun run(params: None) = vehiclesRepository.vehicles()
}
package es.danibeni.android.kotlin.rctelemetry.features.newrace

import es.danibeni.android.kotlin.rctelemetry.core.interactor.UseCase
import es.danibeni.android.kotlin.rctelemetry.data.Vehicle
import es.danibeni.android.kotlin.rctelemetry.data.VehiclesRepository
import javax.inject.Inject

class IdentifyVehicle @Inject constructor(private val vehiclesRepository: VehiclesRepository) : UseCase<Vehicle, IdentifyVehicle.Params>() {

    override suspend fun run(params: Params) = vehiclesRepository.identifyVehicle(params.vehicle)

    data class Params(val vehicle: Vehicle)
}
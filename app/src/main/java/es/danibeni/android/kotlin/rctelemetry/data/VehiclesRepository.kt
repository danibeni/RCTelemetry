package es.danibeni.android.kotlin.rctelemetry.data

import android.util.Log
import es.danibeni.android.kotlin.rctelemetry.core.constants.Constants
import es.danibeni.android.kotlin.rctelemetry.core.exception.Failure
import es.danibeni.android.kotlin.rctelemetry.core.functional.Either
import es.danibeni.android.kotlin.rctelemetry.core.platform.NetworkHandler
import es.danibeni.android.kotlin.rctelemetry.data.remote.VehicleUDPCommunicationService
import es.danibeni.android.kotlin.rctelemetry.features.newrace.NewRaceFailure
import javax.inject.Inject

interface VehiclesRepository {

    fun vehicles(): Either<Failure, List<Vehicle>>
    fun identifyVehicle(vehicle: Vehicle): Either<Failure, Vehicle>

    class Network
    @Inject constructor(private val networkHandler: NetworkHandler) : VehiclesRepository {
        private val TAG: String = VehiclesRepository::class.java.simpleName

        override fun vehicles(): Either<Failure, List<Vehicle>> {
            if (networkHandler.isWifiEnabled and networkHandler.isConnectedToSSID(Constants.RCTELEMETRY_AP_PREFIX)) {
                val vehicles: List<Vehicle> = requestVehicles()
                return when (vehicles.isNotEmpty()) {
                    true -> Either.Right(vehicles)
                    false -> Either.Left(NewRaceFailure.NoVehiclesFound())
                }
            } else {

                return Either.Left(Failure.NetworkConnectionToSSID())
            }
        }

        override fun identifyVehicle(vehicle: Vehicle): Either<Failure, Vehicle> {
            val vehicleComm = VehicleUDPCommunicationService(vehicle.addressId, Constants.RCTELEMETRY_VEHICLE_PORT)
            val identifyResponse = vehicleComm.identify()
            return when (identifyResponse.equals(Constants.RCTELEMETRY_RESPONSE_ACK)) {
                true -> Either.Right(vehicle)
                false -> Either.Left(NewRaceFailure.VehiculeCommunicationFailure())
            }
        }

        fun requestVehicles(): List<Vehicle> {
            var vehicles = mutableListOf<Vehicle>()
            val connectedDevicesIP = networkHandler.getConnectedWifiDevicesInRange(
                    Constants.RCTELEMETRY_IP_RANGE_LOWEST, Constants.RCTELEMETRY_IP_RANGE_HIGHEST)

            for (deviceIP in connectedDevicesIP) {
                val vehicleComm = VehicleUDPCommunicationService(deviceIP, Constants.RCTELEMETRY_VEHICLE_PORT)
                val vehicleName = vehicleComm.getName()
                Log.i(TAG, "Vehicle name: " + vehicleName)
                if (vehicleName.contains(Constants.RCTELEMETRY_VEHICLE_PREFIX)) {
                    vehicles.add(Vehicle(vehicleName, addressId = deviceIP))
                }
            }
            return vehicles
        }
    }
}
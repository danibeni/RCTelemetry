package es.danibeni.android.kotlin.rctelemetry.features.newrace

import android.arch.lifecycle.MutableLiveData
import android.util.Log
import es.danibeni.android.kotlin.rctelemetry.core.exception.Failure
import es.danibeni.android.kotlin.rctelemetry.core.interactor.UseCase
import es.danibeni.android.kotlin.rctelemetry.core.platform.BaseViewModel
import es.danibeni.android.kotlin.rctelemetry.data.Vehicle
import javax.inject.Inject

class NewRaceViewModel
@Inject constructor(private val getVehicles: GetVehicles, private val identifyVehicle: IdentifyVehicle) : BaseViewModel() {
    private val TAG: String = NewRaceViewModel::class.java.simpleName

    var vehiclesLiveData: MutableLiveData<List<VehicleView>> = MutableLiveData()
    var vehicleLiveData: MutableLiveData<VehicleView> = MutableLiveData()
    var lastVehicleClicked: Int = -1


    fun loadVehicles() = getVehicles.execute({it.either(::handleFailure, ::handleVehicleList)}, UseCase.None())

    fun identifyVehicle(vehicleView: VehicleView, position: Int) {
        lastVehicleClicked = position
        val vehicle = Vehicle(vehicleView.name, addressId = vehicleView.addressId)
        identifyVehicle.execute({it.either(::handleFailure, ::handleVehicleIdentification)}, IdentifyVehicle.Params(vehicle))
    }

    fun handleVehicleList(vehicles: List<Vehicle>) {
        Log.i(TAG, "VEHICLEs LIST ITEM: " + vehicles.get(0).name)
        /*
        var racesTest : MutableList<VehicleView> = mutableListOf<VehicleView>()
        racesTest.add(VehicleView("Vehiculo1", "192.168.8.101", "AVAILABLE"))
        racesTest.add(VehicleView("Vehiculo2", "192.168.8.103", "AVAILABLE"))
        racesTest.add(VehicleView("Vehiculo3", "192.168.8.104", "AVAILABLE"))
        racesTest.add(VehicleView("Vehiculo4", "192.168.8.105", "AVAILABLE"))
        racesTest.add(VehicleView("Vehiculo6", "192.168.8.106", "AVAILABLE"))
        this.vehiclesLiveData.value = racesTest
        */
        this.vehiclesLiveData.value = vehicles.map { VehicleView(it.name, it.addressId, it.status) }

    }

    private fun handleVehicleIdentification(vehicle: Vehicle) {
        Log.i(TAG, "VEHICLE: " + vehicle.name)
        this.vehicleLiveData.value = VehicleView(vehicle.name, vehicle.addressId, vehicle.status)
    }
}
package es.danibeni.android.kotlin.rctelemetry.features.newrace

import android.arch.lifecycle.MutableLiveData
import es.danibeni.android.kotlin.rctelemetry.core.interactor.UseCase
import es.danibeni.android.kotlin.rctelemetry.core.platform.BaseViewModel
import es.danibeni.android.kotlin.rctelemetry.data.Vehicle
import javax.inject.Inject
import android.content.Context
import es.danibeni.android.kotlin.rctelemetry.R
import es.danibeni.android.kotlin.rctelemetry.data.Circuit
import es.danibeni.android.kotlin.rctelemetry.features.circuits.GetCircuits

class NewRaceViewModel
@Inject constructor(private val context: Context, private val getCircuits: GetCircuits,
                    private val getDrivers: GetDrivers,
                    private val getVehicles: GetVehicles,
                    private val identifyVehicle: IdentifyVehicle) : BaseViewModel() {
    private val TAG: String = NewRaceViewModel::class.java.simpleName

    var circuitsLiveData: MutableLiveData<List<NewRaceCircuitView>> = MutableLiveData()
    var circuitLiveData: MutableLiveData<NewRaceCircuitView> = MutableLiveData()
    var driversLiveData: MutableLiveData<List<String>> = MutableLiveData()
    var vehiclesLiveData: MutableLiveData<List<NewRaceVehicleView>> = MutableLiveData()
    var vehicleLiveData: MutableLiveData<NewRaceVehicleView> = MutableLiveData()
    var lastVehicleClicked: MutableLiveData<Int> = MutableLiveData()
    var nextActionVisible: Boolean = false

    fun loadCircuits() = getCircuits.execute({it.either(::handleFailure, ::handleCircuitList)}, UseCase.None())

    fun setCircuit(circuitView: NewRaceCircuitView) {circuitLiveData.value = circuitView}

    private fun handleCircuitList(circuits:List<Circuit>) {
        this.circuitsLiveData.value = circuits.map { NewRaceCircuitView(it.id, it.name, it.city, it.lenght!!, it.threshold, it.belowThresholdNum, it.aboveThresholdNum, it.count) }
    }

    fun loadDrivers() = getDrivers.execute({it.either(::handleFailure, ::handleDriverList)}, UseCase.None())

    private fun handleDriverList(drivers: List<String>) {
        this.driversLiveData.value = drivers
    }

    fun loadVehicles() = getVehicles.execute({it.either(::handleFailure, ::handleVehicleList)}, UseCase.None())

    private fun handleVehicleList(vehicles: List<Vehicle>) {
        this.vehiclesLiveData.value = vehicles.map { NewRaceVehicleView(it.name, it.address, context.getString(R.string.new_race_vehicle_available)) }
    }

    fun identifyVehicle(vehicleView: NewRaceVehicleView, position: Int) {
        this.lastVehicleClicked.value = position
        val vehicle = Vehicle(name = vehicleView.name, address = vehicleView.addressId)
        identifyVehicle.execute({it.either(::handleFailure, ::handleVehicleIdentification)}, IdentifyVehicle.Params(vehicle))
    }

    private fun handleVehicleIdentification(vehicle: Vehicle) {
        this.vehicleLiveData.value = NewRaceVehicleView(vehicle.name, vehicle.address, context.getString(R.string.new_race_vehicle_selected))
    }

}
package es.danibeni.android.kotlin.rctelemetry.features.circuitdetails

import android.arch.lifecycle.MutableLiveData
import es.danibeni.android.kotlin.rctelemetry.core.platform.BaseViewModel
import es.danibeni.android.kotlin.rctelemetry.data.Circuit
import es.danibeni.android.kotlin.rctelemetry.features.circuitdetails.InsertCircuitDetails
import javax.inject.Inject

class CircuitDetailsViewModel
@Inject constructor(private val getCircuit: GetCircuitDetails,
                    private val insertCircuit: InsertCircuitDetails,
                    private val deleteRaces: DeleteAssociatedRaces,
                    private val deleteCircuit: DeleteCircuit,
                    private val updateCircuit: UpdateCircuitDetails) : BaseViewModel() {
    private val TAG: String = CircuitDetailsViewModel::class.java.simpleName

    var circuitDetailsLiveData: MutableLiveData<CircuitDetailsView> = MutableLiveData()
    var closeCircuitDetailsLiveData: MutableLiveData<Boolean> = MutableLiveData()
    var circuit: Circuit = Circuit()

    fun getCircuitDetails(circuitId: Long) {
        getCircuit.execute({ it.either(::handleFailure, ::handleCircuitDetails) }, GetCircuitDetails.Params(circuitId))
    }

    private fun handleCircuitDetails(circuit: Circuit) {
        this.circuit = circuit
        circuitDetailsLiveData.value = CircuitDetailsView(name = circuit.name, city = circuit.city, lenght = circuit.lenght)
    }

    fun storeNewCircuit(circuitView: CircuitDetailsView) {
        this.circuit.name = circuitView.name
        this.circuit.city = circuitView.city
        if (circuitView.lenght != null) {
            this.circuit.lenght = circuitView.lenght
        }
        insertCircuit.execute({it.either(::handleFailure, ::handleCircuitDetailsStored)}, InsertCircuitDetails.Params(this.circuit))
    }

    private fun handleCircuitDetailsStored(circuit: Circuit) {
        this.circuit = circuit
        closeCircuitDetailsLiveData.value = true
    }

    fun deleteRaces() {
        deleteRaces.execute({it.either(::handleFailure, ::handleRacesDeleted)}, DeleteAssociatedRaces.Params(this.circuit.id))
    }

    private fun handleRacesDeleted(racesDeleted: Int) {
        deleteCircuit()
    }

    fun deleteCircuit() {
        deleteCircuit.execute({it.either(::handleFailure, ::handleCircuitDeleted)}, DeleteCircuit.Params(this.circuit.id))
    }

    private fun handleCircuitDeleted(circuitsDeleted: Int) {
        closeCircuitDetailsLiveData.value = true
    }

    fun updateCircuitDetails(circuitView: CircuitDetailsView) {
        this.circuit.name = circuitView.name
        this.circuit.city = circuitView.city
        if (circuitView.lenght != null) {
            this.circuit.lenght = circuitView.lenght
        }
        updateCircuit.execute({it.either(::handleFailure, ::handleCircuitUpdated)}, UpdateCircuitDetails.Params(circuit))
    }

    private fun handleCircuitUpdated(circuit: Circuit) {
        closeCircuitDetailsLiveData.value = true
    }

}
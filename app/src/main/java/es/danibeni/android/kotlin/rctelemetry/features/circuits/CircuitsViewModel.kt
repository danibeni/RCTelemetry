package es.danibeni.android.kotlin.rctelemetry.features.circuits

import android.arch.lifecycle.MutableLiveData
import es.danibeni.android.kotlin.rctelemetry.core.interactor.UseCase
import es.danibeni.android.kotlin.rctelemetry.core.platform.BaseViewModel
import es.danibeni.android.kotlin.rctelemetry.data.Circuit
import javax.inject.Inject

class CircuitsViewModel @Inject constructor(private val getCircuits: GetCircuits) : BaseViewModel() {

    var circuitsLiveData: MutableLiveData<List<CircuitView>> = MutableLiveData()

    fun loadCircuits() = getCircuits.execute({ it.either(::handleFailure, ::handleCircuitList) }, UseCase.None())

    private fun handleCircuitList(circuits: List<Circuit>) {
        this.circuitsLiveData.value = circuits.map {
            CircuitView(it.id, it.name, it.city, it.lenght!! )
        }
    }
}
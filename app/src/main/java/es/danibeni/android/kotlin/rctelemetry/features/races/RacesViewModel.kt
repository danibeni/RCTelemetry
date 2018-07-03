package es.danibeni.android.kotlin.rctelemetry.features.races

import android.arch.lifecycle.MutableLiveData
import es.danibeni.android.kotlin.rctelemetry.core.interactor.UseCase
import es.danibeni.android.kotlin.rctelemetry.core.platform.BaseViewModel
import es.danibeni.android.kotlin.rctelemetry.data.Race
import javax.inject.Inject

class RacesViewModel @Inject constructor(private val getRaces: GetRaces) : BaseViewModel() {

    var racesLiveData: MutableLiveData<List<RaceView>> = MutableLiveData()

    fun loadRaces() = getRaces.execute({ it.either(::handleFailure, ::handleRaceList) }, UseCase.None())

    private fun handleRaceList(races: List<Race>) {
        this.racesLiveData.value = races.map { RaceView(it.id, it.circuit) }
    }
}
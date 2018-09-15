package es.danibeni.android.kotlin.rctelemetry.features.races

import android.arch.lifecycle.MutableLiveData
import es.danibeni.android.kotlin.rctelemetry.core.extension.empty
import es.danibeni.android.kotlin.rctelemetry.core.extension.minutesFormat
import es.danibeni.android.kotlin.rctelemetry.core.extension.simpleDateAndTime
import es.danibeni.android.kotlin.rctelemetry.core.interactor.UseCase
import es.danibeni.android.kotlin.rctelemetry.core.platform.BaseViewModel
import es.danibeni.android.kotlin.rctelemetry.data.Lap
import es.danibeni.android.kotlin.rctelemetry.data.Race
import es.danibeni.android.kotlin.rctelemetry.data.RaceAndCircuit
import javax.inject.Inject

class RacesViewModel @Inject constructor(private val getRaces: GetRaces) : BaseViewModel() {

    var racesLiveData: MutableLiveData<List<RaceView>> = MutableLiveData()

    fun loadRaces() = getRaces.execute({ it.either(::handleFailure, ::handleRacesList) }, UseCase.None())

    private fun handleRacesList(races: List<RaceAndCircuit>) {

        this.racesLiveData.value = races.map {
            var raceTime = 0L
            if (it.lapList != null) {
                if (it.lapList!!.isNotEmpty()) {
                    raceTime = it.lapList!!.map { it.lapTime }.sum()
                }
            }
            RaceView(it.id, it.circuitName, it.driver, String.minutesFormat(raceTime), String.simpleDateAndTime(it.raceDate))
        }
    }
}
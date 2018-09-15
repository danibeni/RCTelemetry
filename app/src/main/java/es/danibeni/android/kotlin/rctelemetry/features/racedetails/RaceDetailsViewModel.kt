package es.danibeni.android.kotlin.rctelemetry.features.racedetails

import android.arch.lifecycle.MutableLiveData
import es.danibeni.android.kotlin.rctelemetry.core.extension.minutesFormat
import es.danibeni.android.kotlin.rctelemetry.core.platform.BaseViewModel
import es.danibeni.android.kotlin.rctelemetry.data.Circuit
import es.danibeni.android.kotlin.rctelemetry.data.Race
import es.danibeni.android.kotlin.rctelemetry.data.RaceAndCircuit
import es.danibeni.android.kotlin.rctelemetry.features.newrace.LapView
import javax.inject.Inject

class RaceDetailsViewModel
@Inject constructor(private val getRace: GetRaceDetails,
                    private val deleteRace: DeleteRace) : BaseViewModel() {
    private val TAG: String = RaceDetailsViewModel::class.java.simpleName

    var raceDetailsLiveData: MutableLiveData<RaceDetailsView> = MutableLiveData()
    var bestLapLiveData: MutableLiveData<LapView> = MutableLiveData()
    var closeRaceDetailsLiveData: MutableLiveData<Boolean> = MutableLiveData()

    fun getRaceDetails(raceId: Long) {
        getRace.execute({ it.either(::handleFailure, ::handleRaceDetails) }, GetRaceDetails.Params(raceId))
    }

    private fun handleRaceDetails(race: RaceAndCircuit) {
        if (race.lapList != null) {
            var vehicle = race.vehicleName
            if (race.vehicleAlias.isNotEmpty()) {
               vehicle = race.vehicleAlias
            }
            val lapsView = race.lapList!!.map { LapView(it.lapNumber, it.lapTime, it.lapDiff, it.lapVelocity) }
            val raceTime = lapsView.map { it.lapTime }.sum()
            val bestLap = lapsView.sortedBy { it.lapTime }.first()
            bestLapLiveData.value = bestLap
            raceDetailsLiveData.value = RaceDetailsView(circuitName = race.circuitName, driver = race.driver, vehicle = vehicle, raceTime = String.minutesFormat(raceTime), laps = lapsView)
        }
    }

    fun deleteRace(raceId: Long) {
        deleteRace.execute({it.either(::handleFailure, ::handleRaceDeleted)}, DeleteRace.Params(raceId))
    }

    private fun handleRaceDeleted(racesDeleted: Int) {
        closeRaceDetailsLiveData.value = true
    }

    fun closeRaceDetails() {
        closeRaceDetailsLiveData.value = true
    }

}
package es.danibeni.android.kotlin.rctelemetry.features.racedetails

import es.danibeni.android.kotlin.rctelemetry.features.newrace.LapView

data class RaceDetailsView (
        val circuitName: String = "",
        val driver: String = "",
        val vehicle: String = "",
        val raceTime: String = "",
        val laps:List<LapView> = listOf()
)
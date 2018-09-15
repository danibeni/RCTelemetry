package es.danibeni.android.kotlin.rctelemetry.features.circuitdetails

import es.danibeni.android.kotlin.rctelemetry.features.newrace.LapView

data class CircuitDetailsView (
        val name: String = "",
        val city: String = "",
        val lenght: Float? = 0.0F
)
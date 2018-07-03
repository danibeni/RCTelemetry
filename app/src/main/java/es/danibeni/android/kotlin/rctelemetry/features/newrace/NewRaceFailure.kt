package es.danibeni.android.kotlin.rctelemetry.features.newrace

import es.danibeni.android.kotlin.rctelemetry.core.exception.Failure.FeatureFailure

class NewRaceFailure {
    class VehiculeCommunicationFailure() : FeatureFailure()
    class NoVehiclesFound : FeatureFailure()
}
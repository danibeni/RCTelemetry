/**
 * Copyright (C) 2018 Fernando Cejas Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package es.danibeni.android.kotlin.rctelemetry.features.circuits

import es.danibeni.android.kotlin.rctelemetry.core.exception.Failure.FeatureFailure

class CircuitFailure {
    class ListNotAvailable: FeatureFailure()
    class NotEnoughDataForPattern: FeatureFailure()
    class NotFinishLineCrossingDetected: FeatureFailure()
    class CircuitNotInserted: FeatureFailure()
    class CircuitNotFound: FeatureFailure()
    class CircuitNotUpdated: FeatureFailure()
    class CircuitDeleteFailed: FeatureFailure()
}


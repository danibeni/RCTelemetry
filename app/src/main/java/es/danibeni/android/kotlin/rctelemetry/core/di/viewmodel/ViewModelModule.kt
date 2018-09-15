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
package es.danibeni.android.kotlin.rctelemetry.core.di.viewmodel

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import es.danibeni.android.kotlin.rctelemetry.features.circuitdetails.CircuitDetailsViewModel
import es.danibeni.android.kotlin.rctelemetry.features.circuits.CircuitsViewModel
import es.danibeni.android.kotlin.rctelemetry.features.newrace.NewRaceViewModel
import es.danibeni.android.kotlin.rctelemetry.features.newrace.ReconnaissanceLapViewModel
import es.danibeni.android.kotlin.rctelemetry.features.newrace.RunRaceViewModel
import es.danibeni.android.kotlin.rctelemetry.features.racedetails.RaceDetailsViewModel
import es.danibeni.android.kotlin.rctelemetry.features.races.RacesViewModel

@Module
abstract class ViewModelModule {
    @Binds
    internal abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(RacesViewModel::class)
    abstract fun bindsRacesViewModel(racesViewModel: RacesViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(CircuitsViewModel::class)
    abstract fun bindsCircuitsViewModel(circuitsViewModel: CircuitsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(NewRaceViewModel::class)
    abstract fun bindsNewRacesViewModel(newRacesViewModel: NewRaceViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ReconnaissanceLapViewModel::class)
    abstract fun bindsReconnaissanceLapViewModel(reconnaissanceLapViewModel: ReconnaissanceLapViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(RunRaceViewModel::class)
    abstract fun bindsRunRaceViewModel(runRaceViewModel: RunRaceViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(RaceDetailsViewModel::class)
    abstract fun bindsRaceDetailsViewModel(raceDetailsModelView: RaceDetailsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(CircuitDetailsViewModel::class)
    abstract fun bindsCircuitDetailsViewModel(circuitDetailsModelView: CircuitDetailsViewModel): ViewModel


}
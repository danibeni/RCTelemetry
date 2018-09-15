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
package es.danibeni.android.kotlin.rctelemetry.core.di

import es.danibeni.android.kotlin.rctelemetry.AndroidApplication
import es.danibeni.android.kotlin.rctelemetry.core.di.viewmodel.ViewModelModule
import es.danibeni.android.kotlin.rctelemetry.core.navigation.RouteActivity
import dagger.Component
import es.danibeni.android.kotlin.rctelemetry.core.platform.BaseActivity
import es.danibeni.android.kotlin.rctelemetry.features.circuits.CircuitsFragment
import es.danibeni.android.kotlin.rctelemetry.features.newrace.NewRaceFragment
import es.danibeni.android.kotlin.rctelemetry.features.newrace.ReconnaissanceLapFragment
import es.danibeni.android.kotlin.rctelemetry.features.newrace.RunRaceFragment
import es.danibeni.android.kotlin.rctelemetry.features.circuitdetails.CircuitDetailsFragment
import es.danibeni.android.kotlin.rctelemetry.features.racedetails.RaceDetailsFragment
import es.danibeni.android.kotlin.rctelemetry.features.races.RacesFragment
import javax.inject.Singleton

@Singleton
@Component(modules = [ApplicationModule::class, ViewModelModule::class])
interface ApplicationComponent {
    fun inject(application: AndroidApplication)
    fun inject(routeActivity: RouteActivity)
    fun inject(baseActivity: BaseActivity)
    fun inject(racesFragment: RacesFragment)
    fun inject(circuitsFragment: CircuitsFragment)
    fun inject(newRaceFragment: NewRaceFragment)
    fun inject(reconnaissancLapFragment: ReconnaissanceLapFragment)
    fun inject(runRaceFragment: RunRaceFragment)
    fun inject(raceDetailsFragment: RaceDetailsFragment)
    fun inject(circuitDetailsFragment: CircuitDetailsFragment)
}

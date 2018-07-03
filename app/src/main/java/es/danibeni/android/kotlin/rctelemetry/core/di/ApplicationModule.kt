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

import android.content.Context
import es.danibeni.android.kotlin.rctelemetry.AndroidApplication
import dagger.Module
import dagger.Provides
import es.danibeni.android.kotlin.rctelemetry.core.constants.Constants
import es.danibeni.android.kotlin.rctelemetry.data.local.RCTelemetryDatabase
import es.danibeni.android.kotlin.rctelemetry.data.RacesRepository
import es.danibeni.android.kotlin.rctelemetry.data.VehiclesRepository
import javax.inject.Singleton

@Module
class ApplicationModule(private val application: AndroidApplication) {

    @Provides @Singleton fun provideApplicationContext(): Context = application

    @Provides @Singleton fun provideRacesRepository(context: Context): RacesRepository {
        val database = RCTelemetryDatabase.getInstance(context)
        return RacesRepository.LocalDataSource(database.racesDao())
    }

    @Provides @Singleton fun provideVehiclesRepository(dataSource: VehiclesRepository.Network): VehiclesRepository = dataSource
}

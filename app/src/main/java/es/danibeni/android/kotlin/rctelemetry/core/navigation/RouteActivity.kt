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
package es.danibeni.android.kotlin.rctelemetry.core.navigation

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import es.danibeni.android.kotlin.rctelemetry.AndroidApplication
import es.danibeni.android.kotlin.rctelemetry.R
import es.danibeni.android.kotlin.rctelemetry.core.di.ApplicationComponent
import javax.inject.Inject

class RouteActivity : AppCompatActivity() {

    private val appComponent: ApplicationComponent by lazy(mode = LazyThreadSafetyMode.NONE) {
        (application as AndroidApplication).appComponent
    }

    @Inject internal lateinit var navigator: Navigator

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        appComponent.inject(this)
        navigator.showMain(this)
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
    }
}

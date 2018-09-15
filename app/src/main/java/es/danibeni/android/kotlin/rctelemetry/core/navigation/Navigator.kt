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

import android.content.Context
import android.content.Intent
import android.net.wifi.WifiManager
import android.support.v4.app.FragmentActivity
import android.view.View
import es.danibeni.android.kotlin.rctelemetry.features.races.RacesActivity
import javax.inject.Inject
import javax.inject.Singleton
import android.content.pm.PackageManager
import es.danibeni.android.kotlin.rctelemetry.core.constants.Constants
import es.danibeni.android.kotlin.rctelemetry.features.circuits.CircuitsActivity
import es.danibeni.android.kotlin.rctelemetry.features.newrace.*
import es.danibeni.android.kotlin.rctelemetry.features.circuitdetails.CircuitDetailsActivity
import es.danibeni.android.kotlin.rctelemetry.features.racedetails.RaceDetailsActivity

@Singleton
class Navigator
@Inject constructor() {

    fun showMain(context: Context) {
        showRaces(context)
    }

    private fun showRaces(context: Context) = context.startActivity(RacesActivity.callingIntent(context))

    fun showCircuits(activity: FragmentActivity) = activity.startActivity(CircuitsActivity.callingIntent(activity))

    fun newRace(activity: FragmentActivity) = activity.startActivity(NewRaceActivity.callingIntent(activity))

    fun pickUpWifi(activity: FragmentActivity) {
        val intent = Intent(WifiManager.ACTION_PICK_WIFI_NETWORK)
        val packageManager = activity.packageManager
        val activities = packageManager.queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY)
        val isIntentSafe = activities.size > 0
        if (isIntentSafe) {
            activity.startActivityForResult(Intent(WifiManager.ACTION_PICK_WIFI_NETWORK), Constants.CHANGE_WIFI_NETWORK_CONNECTION)
        }
    }

    fun reconnaissanceLap(activity: FragmentActivity, newRace: NewRaceView, circuit: NewRaceCircuitView) {
        val intent = ReconnaissanceLapActivity.callingIntent(activity, newRace, circuit)
        activity.startActivity(intent)
    }

    fun runRace(activity: FragmentActivity, newRace: NewRaceView, circuit: NewRaceCircuitView) {
        val intent = RunRaceActivity.callingIntent(activity, newRace, circuit)
        activity.startActivity(intent)
    }

    fun raceDetails(activity: FragmentActivity, raceId: Long) {
        val intent = RaceDetailsActivity.callingIntent(activity, raceId)
        activity.startActivity(intent)
    }

    fun circuitDetails(activity: FragmentActivity, circuitId: Long) {
        val intent = CircuitDetailsActivity.callingIntent(activity, circuitId)
        activity.startActivity(intent)
    }

/*
    fun showMovieDetails(activity: FragmentActivity, movie: MovieView, navigationExtras: Extras) {
        val intent = MovieDetailsActivity.callingIntent(activity, movie)
        val sharedView = navigationExtras.transitionSharedElement as ImageView
        val activityOptions = ActivityOptionsCompat
                .makeSceneTransitionAnimation(activity, sharedView, sharedView.transitionName)
        activity.startActivity(intent, activityOptions.toBundle())
    }
*/
    class Extras(val transitionSharedElement: View)
}



package es.danibeni.android.kotlin.rctelemetry.features.racedetails

import android.content.Context
import android.content.Intent
import es.danibeni.android.kotlin.rctelemetry.core.platform.BaseActivity
import es.danibeni.android.kotlin.rctelemetry.features.newrace.RunRaceFragment

class RaceDetailsActivity : BaseActivity() {
    companion object {
        private const val INTENT_EXTRA_PARAM_RACE_ID = "es.danibeni.android.kotlin.rctelemetry.features.INTENT_PARAM_RACE_ID"
        fun callingIntent(context: Context, raceId: Long): Intent {
            val intent = Intent(context, RaceDetailsActivity::class.java)
            intent.putExtra(INTENT_EXTRA_PARAM_RACE_ID, raceId)
            return intent
        }
    }

    override fun fragment() = RaceDetailsFragment.forRaceDetails(intent.getLongExtra(INTENT_EXTRA_PARAM_RACE_ID, 0))
}

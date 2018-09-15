package es.danibeni.android.kotlin.rctelemetry.features.newrace

import android.content.Context
import android.content.Intent
import es.danibeni.android.kotlin.rctelemetry.core.platform.BaseActivity

class ReconnaissanceLapActivity : BaseActivity() {
    companion object {
        private const val INTENT_EXTRA_PARAM_NEW_RACE = "es.danibeni.android.kotlin.rctelemetry.features.newrace.INTENT_PARAM_NEW_RACE"
        private const val INTENT_EXTRA_PARAM_CIRCUIT = "es.danibeni.android.kotlin.rctelemetry.features.newrace.INTENT_PARAM_CIRCUIT"
        fun callingIntent(context: Context, newRace: NewRaceView, circuit: NewRaceCircuitView): Intent {
            val intent = Intent(context, ReconnaissanceLapActivity::class.java)
            intent.putExtra(INTENT_EXTRA_PARAM_NEW_RACE, newRace)
            intent.putExtra(INTENT_EXTRA_PARAM_CIRCUIT, circuit)
            return intent
        }
    }

    override fun fragment() = ReconnaissanceLapFragment.forReconnaissanceLap(intent.getParcelableExtra(INTENT_EXTRA_PARAM_NEW_RACE), intent.getParcelableExtra(INTENT_EXTRA_PARAM_CIRCUIT))
}
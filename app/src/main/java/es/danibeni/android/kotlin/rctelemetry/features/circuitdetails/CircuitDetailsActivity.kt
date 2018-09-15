package es.danibeni.android.kotlin.rctelemetry.features.circuitdetails

import android.content.Context
import android.content.Intent
import es.danibeni.android.kotlin.rctelemetry.core.platform.BaseActivity

class CircuitDetailsActivity : BaseActivity() {
    companion object {
        private const val INTENT_EXTRA_PARAM_CIRCUIT_ID = "es.danibeni.android.kotlin.rctelemetry.features.INTENT_PARAM_CIRCUIT_ID"
        fun callingIntent(context: Context, circuitId: Long): Intent {
            val intent = Intent(context, CircuitDetailsActivity::class.java)
            intent.putExtra(INTENT_EXTRA_PARAM_CIRCUIT_ID, circuitId)
            return intent
        }
    }

    override fun fragment() = CircuitDetailsFragment.forCircuitDetails(intent.getLongExtra(INTENT_EXTRA_PARAM_CIRCUIT_ID, 0))
}

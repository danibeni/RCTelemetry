package es.danibeni.android.kotlin.rctelemetry.features.newrace

import android.content.Context
import android.content.Intent
import es.danibeni.android.kotlin.rctelemetry.core.platform.BaseActivity

class ReconnaissanceLapActivity : BaseActivity() {
    companion object {
        private const val INTENT_EXTRA_PARAM_VEHICLE = "es.danibeni.android.kotlin.rctelemetry.features.newrace.INTENT_PARAM_VEHICLE"
        fun callingIntent(context: Context, vehicle: VehicleView): Intent {
            val intent = Intent(context, ReconnaissanceLapActivity::class.java)
            intent.putExtra(INTENT_EXTRA_PARAM_VEHICLE, vehicle)
            return intent
        }
    }
    override fun fragment() = ReconnaissanceLapFragment.forVehicle(intent.getParcelableExtra(INTENT_EXTRA_PARAM_VEHICLE))
}
package es.danibeni.android.kotlin.rctelemetry.features.circuits

import android.content.Context
import android.content.Intent
import es.danibeni.android.kotlin.rctelemetry.core.platform.BaseActivity

class CircuitsActivity : BaseActivity() {


    companion object {
        fun callingIntent(context: Context) = Intent(context, CircuitsActivity::class.java)
    }

    override fun fragment() = CircuitsFragment()
}
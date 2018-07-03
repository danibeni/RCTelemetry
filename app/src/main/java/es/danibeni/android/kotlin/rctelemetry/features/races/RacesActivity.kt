package es.danibeni.android.kotlin.rctelemetry.features.races

import android.content.Context
import android.content.Intent
import es.danibeni.android.kotlin.rctelemetry.core.platform.BaseActivity

class RacesActivity : BaseActivity() {


    companion object {
        fun callingIntent(context: Context) = Intent(context, RacesActivity::class.java)
    }

    override fun fragment() = RacesFragment()
}
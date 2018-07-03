package es.danibeni.android.kotlin.rctelemetry.features.newrace

import android.content.Context
import android.content.Intent
import android.util.Log
import es.danibeni.android.kotlin.rctelemetry.core.constants.Constants
import es.danibeni.android.kotlin.rctelemetry.core.platform.BaseActivity

class NewRaceActivity : BaseActivity() {

    companion object {
        fun callingIntent(context: Context) = Intent(context, NewRaceActivity::class.java)
    }
    override fun fragment() = NewRaceFragment()

}
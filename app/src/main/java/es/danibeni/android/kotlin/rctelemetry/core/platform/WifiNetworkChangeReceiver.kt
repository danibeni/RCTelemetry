package es.danibeni.android.kotlin.rctelemetry.core.platform

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class WifiNetworkChangeReceiver : BroadcastReceiver() {
    private val TAG: String = WifiNetworkChangeReceiver::class.java.simpleName

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.i(TAG, "NETWORK CHANGED")
    }

}
package es.danibeni.android.kotlin.rctelemetry.features.newrace

import android.os.Bundle
import android.support.v7.preference.PreferenceFragmentCompat
import es.danibeni.android.kotlin.rctelemetry.R

class ReconnaisanceLapPreferenceFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.reclap_preferences)
    }

}
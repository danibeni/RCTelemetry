package es.danibeni.android.kotlin.rctelemetry.features.newrace

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.preference.Preference
import android.support.v7.preference.PreferenceFragmentCompat
import android.support.v7.preference.SwitchPreferenceCompat
import es.danibeni.android.kotlin.rctelemetry.R
import es.danibeni.android.kotlin.rctelemetry.core.platform.BaseActivity
import kotlinx.android.synthetic.main.toolbar.toolbar

class ReconnaissanceLapPreferenceActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.base_activity)
        setSupportActionBar(toolbar)

        // android.R.id.content is probably for old style activity
        supportFragmentManager.beginTransaction()
                // .replace(android.R.id.content, SettingsFragment())
                .replace(R.id.fragmentContainer, SettingsFragment())
                .commit()
    }

    class SettingsFragment : PreferenceFragmentCompat() {

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.reclap_preferences)
        }

    }
}
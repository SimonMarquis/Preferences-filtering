package fr.smarquis.preferences

import android.os.Build.MANUFACTURER
import android.os.Build.MODEL
import android.os.Build.PRODUCT
import android.os.Build.VERSION.RELEASE
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.text.style.ImageSpan
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.core.text.buildSpannedString
import androidx.core.text.inSpans
import androidx.core.view.isVisible
import androidx.preference.MultiSelectListPreference
import androidx.preference.Preference
import androidx.preference.Preference.SummaryProvider
import androidx.preference.PreferenceFragmentCompat
import androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
import fr.smarquis.preferences.BuildConfig.APPLICATION_ID
import fr.smarquis.preferences.BuildConfig.VERSION_CODE
import fr.smarquis.preferences.BuildConfig.VERSION_NAME
import androidx.preference.R as PreferenceR

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        /* Configure the [emptyView] */
        val emptyView = view.findViewById<TextView>(android.R.id.empty).apply {
            text = buildSpannedString {
                inSpans(ImageSpan(context, R.mipmap.ic_launcher)) { append("Empty…") }
            }
        }
        /* Update the [emptyView]'s visibility based on the adapter's item count */
        with(listView.adapter!!) {
            registerAdapterDataObserver(object : AdapterDataObserver() {
                override fun onChanged() {
                    emptyView.isVisible = itemCount == 0
                }
            })
        }
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
        /* Application */
        findPreference<Preference>("pref_app_applicationId")!!.summary = APPLICATION_ID
        findPreference<Preference>("pref_app_versionName")!!.summary = VERSION_NAME
        findPreference<Preference>("pref_app_versionCode")!!.summary = VERSION_CODE.toString()
        /* Device */
        val displayMetrics = requireContext().resources.displayMetrics
        findPreference<Preference>("pref_device_make")!!.summary = MANUFACTURER
        findPreference<Preference>("pref_device_model")!!.summary = MODEL
        findPreference<Preference>("pref_device_product")!!.summary = PRODUCT
        findPreference<Preference>("pref_device_release")!!.summary = "$RELEASE (API $SDK_INT)"
        findPreference<Preference>("pref_device_resolution")!!.summary = "${displayMetrics.widthPixels}x${displayMetrics.heightPixels}"
        findPreference<Preference>("pref_device_density")!!.summary = "${displayMetrics.densityDpi} dpi"
        /* AndroidX */
        findPreference<MultiSelectListPreference>("pref_androidx_MultiSelectListPreference")!!.summaryProvider = SummaryProvider<MultiSelectListPreference> {
            if (it.values.isEmpty()) it.context.getString(PreferenceR.string.not_set)
            else it.values.joinToString { value -> it.entries[it.findIndexOfValue(value)] }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_settings, menu)
        /* Attach the filtering mechanism */
        (menu.findItem(R.id.menu_item_search).actionView as SearchView).attachFiltering(preferenceScreen)
    }

}

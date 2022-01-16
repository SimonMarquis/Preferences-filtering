package fr.smarquis.preferences

import androidx.annotation.VisibleForTesting
import androidx.appcompat.widget.SearchView
import androidx.preference.EditTextPreference
import androidx.preference.ListPreference
import androidx.preference.MultiSelectListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceGroup
import androidx.preference.PreferenceScreen
import androidx.preference.SeekBarPreference
import androidx.preference.TwoStatePreference
import androidx.preference.children

object Filtering {

    operator fun invoke(preference: Preference, string: String?) = preference.filter(string)

    @VisibleForTesting
    fun Preference.filter(string: String?): Boolean = when {
        this is PreferenceGroup -> children.map { it.filter(string) }.count { it } > 0
        string.isNullOrBlank() -> true
        string in title -> true
        string in summary -> true
        this is EditTextPreference -> string in text
        this is SeekBarPreference -> string in value.toString()
        this is TwoStatePreference -> string in if (isChecked) summaryOn else summaryOff
        this is ListPreference -> string in value
        this is MultiSelectListPreference -> values.any { string in it }
        else -> false
    }.also { isVisible = it }

    private operator fun CharSequence?.contains(filter: String?): Boolean = this?.contains(filter.orEmpty(), ignoreCase = true) ?: false

}

fun SearchView.attachFiltering(screen: PreferenceScreen) = setOnQueryTextListener(object : SearchView.OnQueryTextListener {
    override fun onQueryTextSubmit(query: String) = Filtering(screen, query)
    override fun onQueryTextChange(newText: String) = Filtering(screen, newText)
})

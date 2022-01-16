package fr.smarquis.preferences

import androidx.preference.EditTextPreference
import androidx.preference.ListPreference
import androidx.preference.MultiSelectListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceGroup
import androidx.preference.SeekBarPreference
import androidx.preference.TwoStatePreference
import com.google.testing.junit.testparameterinjector.TestParameter
import com.google.testing.junit.testparameterinjector.TestParameterInjector
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@RunWith(TestParameterInjector::class)
class FilteringTest {

    @Test
    fun `Filtering with null string`() {
        /* Given */
        val preference = mockk<Preference>(relaxed = true)
        val string = null
        /* When */
        val filtered = Filtering(preference, string)
        /* Then */
        assertTrue(filtered)
        verify { preference.isVisible = true }
    }

    @Test
    fun `Filtering with blank string`() {
        /* Given */
        val preference = mockk<Preference>(relaxed = true)
        val string = "  "
        /* When */
        val filtered = Filtering(preference, string)
        /* Then */
        assertTrue(filtered)
        verify { preference.isVisible = true }
    }

    @Test
    fun `Filtering with string not found`() {
        /* Given */
        val preference = mockk<Preference>(relaxed = true)
        /* When */
        val filtered = Filtering(preference, "Hello, World!")
        /* Then */
        assertFalse(filtered)
        verify { preference.isVisible = false }
    }

    @Test
    fun `Filtering on title`(@TestParameter match: Boolean) {
        /* Given */
        val preference = mockk<Preference>(relaxed = true) {
            every { title } returns "Hello, World!".takeIf { match }
        }
        /* When */
        val filtered = Filtering(preference, "World")
        /* Then */
        assertEquals(expected = match, actual = filtered)
        verify { preference.isVisible = match }
    }

    @Test
    fun `Filtering on summary`(@TestParameter match: Boolean) {
        /* Given */
        val preference = mockk<Preference>(relaxed = true) {
            every { summary } returns "Hello, World!".takeIf { match }
        }
        /* When */
        val filtered = Filtering(preference, "World")
        /* Then */
        assertEquals(expected = match, actual = filtered)
        verify { preference.isVisible = match }
    }

    @Test
    fun `Filtering on EditTextPreference`(@TestParameter match: Boolean) {
        /* Given */
        val preference = mockk<EditTextPreference>(relaxed = true) {
            every { text } returns "Hello, World!".takeIf { match }
        }
        /* When */
        val filtered = Filtering(preference, "World")
        /* Then */
        assertEquals(expected = match, actual = filtered)
        verify { preference.isVisible = match }
    }

    @Test
    fun `Filtering on SeekBarPreference`(@TestParameter match: Boolean) {
        /* Given */
        val preference = mockk<SeekBarPreference>(relaxed = true) {
            every { value } returns if (match) 1 else 0
        }
        /* When */
        val filtered = Filtering(preference, "1")
        /* Then */
        assertEquals(expected = match, actual = filtered)
        verify { preference.isVisible = match }
    }

    @Test
    fun `Filtering on TwoStatePreference`(
        @TestParameter checked: Boolean,
        @TestParameter match: Boolean,
    ) {
        /* Given */
        val preference = mockk<TwoStatePreference>(relaxed = true) {
            every { isChecked } returns checked
            every { if (checked) summaryOn else summaryOff } returns "Hello, World!".takeIf { match }
        }
        /* When */
        val filtered = Filtering(preference, "World")
        /* Then */
        assertEquals(expected = match, actual = filtered)
        verify { preference.isVisible = match }
    }

    @Test
    fun `Filtering on ListPreference`(@TestParameter match: Boolean) {
        /* Given */
        val preference = mockk<ListPreference>(relaxed = true) {
            every { value } returns "Hello, World!".takeIf { match }
        }
        /* When */
        val filtered = Filtering(preference, "World")
        /* Then */
        assertEquals(expected = match, actual = filtered)
        verify { preference.isVisible = match }
    }

    @Test
    fun `Filtering on MultiSelectListPreference`(@TestParameter match: Boolean) {
        /* Given */
        val preference = mockk<MultiSelectListPreference>(relaxed = true) {
            every { values } returns setOf("Hello", "World".takeIf { match }, "!")
        }
        /* When */
        val filtered = Filtering(preference, "World")
        /* Then */
        assertEquals(expected = match, actual = filtered)
        verify { preference.isVisible = match }
    }

    @Test
    fun `Filtering on PreferenceGroup`(@TestParameter match: Boolean) {
        /* Given */
        val child = mockk<Preference>(relaxed = true) {
            every { title } returns "Hello, World!".takeIf { match }
        }
        val group = mockk<PreferenceGroup>(relaxed = true) {
            every { preferenceCount } returns 1
            every { getPreference(0) } returns child
        }
        /* When */
        val filtered = Filtering(group, "World")
        /* Then */
        assertEquals(expected = match, actual = filtered)
        verify {
            child.isVisible = match
            group.isVisible = match
        }
    }

    @Test
    fun `Filtering on nested PreferenceGroups`(@TestParameter match: Boolean) {
        /* Given */
        val nestedChild = mockk<Preference>(relaxed = true) {
            every { title } returns "Hello, World!".takeIf { match }
        }
        val nestedGroup = mockk<PreferenceGroup>(relaxed = true) {
            every { preferenceCount } returns 1
            every { getPreference(0) } returns nestedChild
        }
        val group = mockk<PreferenceGroup>(relaxed = true) {
            every { preferenceCount } returns 1
            every { getPreference(0) } returns nestedGroup
        }
        /* When */
        val filtered = Filtering(group, "World")
        /* Then */
        assertEquals(expected = match, actual = filtered)
        verify {
            nestedChild.isVisible = match
            nestedGroup.isVisible = match
            group.isVisible = match
        }
    }

    @Test
    fun `Filtering on PreferenceGroup with multiple children`() {
        /* Given */
        val first = mockk<Preference>(relaxed = true) {
            every { title } returns "Hello, first!"
        }
        val second = mockk<Preference>(relaxed = true) {
            every { title } returns "Hello, second!"
        }
        val group = mockk<PreferenceGroup>(relaxed = true) {
            every { preferenceCount } returns 2
            every { getPreference(0) } returns first
            every { getPreference(1) } returns second
        }
        /* Then all match */
        assertTrue(Filtering(group, "Hello"))
        verify {
            first.isVisible = true
            second.isVisible = true
            group.isVisible = true
        }
        clearMocks(first, second, group, answers = false)

        /* Then single match */
        assertTrue(Filtering(group, "first"))
        verify {
            first.isVisible = true
            second.isVisible = false
            group.isVisible = true
        }
        clearMocks(first, second, group, answers = false)

        /* Then none match */
        assertFalse(Filtering(group, "third"))
        verify {
            first.isVisible = false
            second.isVisible = false
            group.isVisible = false
        }
        clearMocks(first, second, group, answers = false)
    }

}

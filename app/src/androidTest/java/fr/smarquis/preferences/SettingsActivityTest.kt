package fr.smarquis.preferences

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.Visibility.VISIBLE
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isRoot
import androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import fr.smarquis.preferences.BuildConfig.APPLICATION_ID
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.not
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import androidx.appcompat.R as AppcompatR


@RunWith(AndroidJUnit4::class)
class SettingsActivityTest {

    @get:Rule
    var rule = activityScenarioRule<SettingsActivity>()

    @Test
    fun filtering() {
        onView(isRoot()).check(matches(isDisplayed()))
        /* Check ID entry is displayed */
        onView(allOf(withText(APPLICATION_ID))).check(matches(isDisplayed()))
        /* Open the Search */
        onView(withId(R.id.menu_item_search)).perform(click())
        /* Filter "Version" */
        onView(withId(AppcompatR.id.search_src_text)).perform(typeText("Version"), closeSoftKeyboard())
        /* Check ID entry is no longer displayed */
        onView(withText(APPLICATION_ID)).check(doesNotExist())
        /* Check we still have filtered entries */
        onView(withText("Version code")).check(matches(isDisplayed()))
        onView(withText("Version name")).check(matches(isDisplayed()))
        /* Clear filter */
        onView(withId(AppcompatR.id.search_close_btn)).perform(click(), closeSoftKeyboard())
        /* Check ID entry is back */
        onView(allOf(withText(APPLICATION_ID), withEffectiveVisibility(VISIBLE))).check(matches(isDisplayed()))
    }

    @Test
    fun emptyView() {
        onView(isRoot()).check(matches(isDisplayed()))
        /* Check empty view is not displayed */
        onView(withId(android.R.id.empty)).check(matches(not(isDisplayed())))
        /* Open the Search */
        onView(withId(R.id.menu_item_search)).perform(click())
        /* Filter for a unknown key "xyz" */
        onView(withId(AppcompatR.id.search_src_text)).perform(typeText("xyz"), closeSoftKeyboard())
        /* Check empty view is displayed */
        onView(withId(android.R.id.empty)).check(matches(isDisplayed()))
    }

}

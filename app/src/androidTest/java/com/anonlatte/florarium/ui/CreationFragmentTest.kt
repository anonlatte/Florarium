package com.anonlatte.florarium.ui

import android.content.Context
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.action.ViewActions.swipeUp
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.anonlatte.florarium.R
import com.anonlatte.florarium.ui.creation.CreationFragment
import kotlinx.android.synthetic.main.fragment_plant_creation.*
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CreationFragmentTest {

    private val context: Context = InstrumentationRegistry.getInstrumentation().targetContext
    private lateinit var fragment: FragmentScenario<CreationFragment>

    @Before
    fun launchFragment() {
        fragment = launchFragmentInContainer<CreationFragment>(themeResId = R.style.AppTheme)
    }

    @Test
    fun testEmptyPlantNameValidation() {
        onView(withId(R.id.addPlantButton)).perform(scrollTo(), click())
        onView(withId(R.id.titleInputLayout)).check(
            matches(hasDescendant(withText(R.string.error_empty_plant_name)))
        )
    }

    @Test
    fun testLongPlantNameValidation() {
        var maxFieldLength = 0
        fragment.onFragment {
            maxFieldLength = it.titleInputLayout.counterMaxLength
        }
        val invalidLength = maxFieldLength + 1
        onView(withId(R.id.titleEditText)).perform(
            typeText(getRandomString(invalidLength)),
            closeSoftKeyboard()
        )
        onView(withId(R.id.addPlantButton)).perform(scrollTo(), click())
        onView(withId(R.id.titleInputLayout)).check(
            matches(
                hasDescendant(
                    withText(
                        context.getString(
                            R.string.error_long_plant_name,
                            invalidLength
                        )
                    )
                )
            )
        )
    }

    @Test
    fun testValidPlantNameValidation() {
        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())
        navController.setGraph(R.navigation.main)
        fragment.onFragment {
            Navigation.setViewNavController(it.requireView(), navController)
        }
        onView(withId(R.id.titleEditText)).perform(
            typeText(getRandomString()),
            closeSoftKeyboard()
        )
        onView(withId(R.id.homeScrollView)).perform(swipeUp())
        onView(withId(R.id.addPlantButton)).perform(scrollTo(), click())
        assertThat(
            navController.currentDestination?.id,
            equalTo(R.id.homeFragment)
        )
    }

    private fun getRandomString(length: Int = 8): String {
        return (1..length).map { ('a'..'z').random() }.joinToString("")
    }
}

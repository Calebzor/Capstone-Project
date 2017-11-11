package hu.tvarga.cheaplist.ui;

import android.support.annotation.NonNull;
import android.support.test.espresso.NoMatchingViewException;
import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.azimolabs.conditionwatcher.ConditionWatcher;
import com.azimolabs.conditionwatcher.Instruction;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import hu.tvarga.cheaplist.R;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class EmailLogin {

	@Rule
	public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(
			MainActivity.class);

	@Ignore
	@Test
	public void emailLogin() throws Exception {

		ConditionWatcher.waitForCondition(waitForViewByIdCondition(R.id.email_button));

		ViewInteraction supportVectorDrawablesButton = onView(
				allOf(withId(R.id.email_button), withText("Sign in with email"), childAtPosition(
						allOf(withId(R.id.btn_holder), childAtPosition(withId(R.id.container), 0)),
						0)));
		supportVectorDrawablesButton.perform(scrollTo(), click());

		ConditionWatcher.waitForCondition(waitForViewByIdCondition(R.id.email));

		ViewInteraction textInputEditText = onView(allOf(withId(R.id.email),
				childAtPosition(childAtPosition(withId(R.id.email_layout), 0), 0), isDisplayed()));
		textInputEditText.perform(click());

		ViewInteraction textInputEditText2 = onView(allOf(withId(R.id.email),
				childAtPosition(childAtPosition(withId(R.id.email_layout), 0), 0), isDisplayed()));
		textInputEditText2.perform(replaceText("user@example.com"), closeSoftKeyboard());

		ViewInteraction textInputEditText5 = onView(
				allOf(withId(R.id.email), withText("user@example.com"),
						childAtPosition(childAtPosition(withId(R.id.email_layout), 0), 0),
						isDisplayed()));
		textInputEditText5.perform(closeSoftKeyboard());

		ViewInteraction appCompatButton = onView(allOf(withId(R.id.button_next), withText("Next"),
				childAtPosition(childAtPosition(withId(R.id.fragment_register_email), 0), 1),
				isDisplayed()));
		appCompatButton.perform(click());

		ConditionWatcher.waitForCondition(waitForViewByIdCondition(R.id.password));

		ViewInteraction textInputEditText6 = onView(allOf(withId(R.id.password),
				childAtPosition(childAtPosition(withId(R.id.password_layout), 0), 0),
				isDisplayed()));
		textInputEditText6.perform(replaceText("secretPassword"), closeSoftKeyboard());

		ViewInteraction appCompatButton2 = onView(
				allOf(withId(R.id.button_done), withText("Sign in"), childAtPosition(
						childAtPosition(withClassName(is("android.widget.LinearLayout")), 3), 1)));
		appCompatButton2.perform(scrollTo(), click());

	}

	@NonNull
	private Instruction waitForViewByIdCondition(final int id) {
		return new Instruction() {
			@Override
			public String getDescription() {
				return "Waiting for view";
			}

			@Override
			public boolean checkCondition() {
				try {
					onView(withId(id)).check(matches(isDisplayed()));
					return true;
				}
				catch (NoMatchingViewException e) {
					return false;
				}
			}
		};
	}

	private static Matcher<View> childAtPosition(final Matcher<View> parentMatcher,
			final int position) {

		return new TypeSafeMatcher<View>() {
			@Override
			public void describeTo(Description description) {
				description.appendText("Child at position " + position + " in parent ");
				parentMatcher.describeTo(description);
			}

			@Override
			public boolean matchesSafely(View view) {
				ViewParent parent = view.getParent();
				return parent instanceof ViewGroup && parentMatcher.matches(parent) && view.equals(
						((ViewGroup) parent).getChildAt(position));
			}
		};
	}
}

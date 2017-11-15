package hu.tvarga.cheaplist.ui;

import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import com.azimolabs.conditionwatcher.ConditionWatcher;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import hu.tvarga.cheaplist.R;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static hu.tvarga.cheaplist.ConditionWatchers.waitForViewByIdCondition;
import static hu.tvarga.cheaplist.EspressoHelpers.childAtPosition;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertTrue;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class EmailLogin {

	@Rule
	public ActivityTestRule<StartActivity> mActivityTestRule = new ActivityTestRule<>(
			StartActivity.class);

	@Test
	public void emailLogin() throws Exception {
		ensureDummyUserIsLoggedIn();
	}

	public static void ensureDummyUserIsLoggedIn() throws Exception {
		FirebaseAuth instance = FirebaseAuth.getInstance();
		FirebaseUser currentUser = instance.getCurrentUser();
		if (currentUser != null) {
			String email = currentUser.getEmail();
			if ("user@example.com".equals(email)) {
				assertTrue("Dummy assert, test does not need to run", true);
				return;
			}
		}

		ConditionWatcher.waitForCondition(waitForViewByIdCondition(R.id.email_button));

		ViewInteraction supportVectorDrawablesButton = onView(
				allOf(withId(R.id.email_button), withText("Sign in with email")));
		supportVectorDrawablesButton.perform(scrollTo(), click());

		ConditionWatcher.waitForCondition(waitForViewByIdCondition(R.id.email));

		ViewInteraction textInputEditText = onView(allOf(withId(R.id.email),
				childAtPosition(childAtPosition(withId(R.id.email_layout), 0), 0), isDisplayed()));
		textInputEditText.perform(click());

		ViewInteraction textInputEditText2 = onView(allOf(withId(R.id.email), childAtPosition(childAtPosition(withId(R.id.email_layout), 0), 0), isDisplayed()));
		textInputEditText2.perform(replaceText("user@example.com"), closeSoftKeyboard());

		ViewInteraction textInputEditText5 = onView(
				allOf(withId(R.id.email), withText("user@example.com"), childAtPosition(childAtPosition(withId(R.id.email_layout), 0), 0),
						isDisplayed()));
		textInputEditText5.perform(closeSoftKeyboard());

		ViewInteraction appCompatButton = onView(allOf(withId(R.id.button_next), withText("Next"),
				childAtPosition(childAtPosition(withId(R.id.fragment_register_email), 0), 1),
				isDisplayed()));
		appCompatButton.perform(click());

		ConditionWatcher.waitForCondition(waitForViewByIdCondition(R.id.password));

		ViewInteraction textInputEditText6 = onView(allOf(withId(R.id.password), childAtPosition(childAtPosition(withId(R.id.password_layout), 0), 0),
				isDisplayed()));
		textInputEditText6.perform(replaceText("secretPassword"), closeSoftKeyboard());

		ViewInteraction appCompatButton2 = onView(
				allOf(withId(R.id.button_done), withText("Sign in"), childAtPosition(childAtPosition(withClassName(is("android.widget.LinearLayout")), 3), 1)));
		appCompatButton2.perform(scrollTo(), click());
	}

}

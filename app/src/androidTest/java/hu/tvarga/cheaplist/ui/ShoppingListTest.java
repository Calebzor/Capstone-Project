package hu.tvarga.cheaplist.ui;

import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;

import com.azimolabs.conditionwatcher.ConditionWatcher;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import hu.tvarga.cheaplist.R;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static hu.tvarga.cheaplist.ConditionWatchers.waitForViewByContentDescriptionCondition;
import static hu.tvarga.cheaplist.ConditionWatchers.waitForViewByIdCondition;
import static hu.tvarga.cheaplist.ConditionWatchers.waitForViewByViewMatcherCondition;
import static hu.tvarga.cheaplist.EspressoHelpers.childAtPosition;
import static hu.tvarga.cheaplist.EspressoHelpers.clickNavigationButton;
import static hu.tvarga.cheaplist.ui.EmailLogin.ensureDummyUserIsLoggedIn;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class ShoppingListTest {

	@Rule
	public ActivityTestRule<StartActivity> mActivityTestRule = new ActivityTestRule<>(
			StartActivity.class);

	@Before
	public void setUp() throws Exception {
		ensureDummyUserIsLoggedIn();
	}

	@Test
	public void shoppingListTest() throws Exception {

		ConditionWatcher.waitForCondition(waitForViewByIdCondition(R.id.itemContainer, true));

		final Matcher<View> viewMatcher = allOf(withId(R.id.itemContainer),
				childAtPosition(childAtPosition(withId(R.id.itemsListStart), 0), 1));

		ConditionWatcher.waitForCondition(waitForViewByViewMatcherCondition(viewMatcher));

		ViewInteraction merchantCategoryItemInStartList = onView(viewMatcher);
		merchantCategoryItemInStartList.perform(click());

		ConditionWatcher.waitForCondition(waitForViewByIdCondition(R.id.detailFab));

		ConditionWatcher.waitForCondition(
				waitForViewByContentDescriptionCondition(R.string.add_to_list_action));

		ViewInteraction floatingActionButton = onView(withId(R.id.detailFab));
		floatingActionButton.perform(click());

		clickNavigationButton();

		clickNavigationButton();

		ViewInteraction navigationMenuItemView = onView(allOf(childAtPosition(
				allOf(withId(R.id.design_navigation_view),
						childAtPosition(withId(R.id.navigationView), 0)), 2), isDisplayed()));
		navigationMenuItemView.perform(click());

		ViewInteraction shoppingListItem = onView(allOf(withId(R.id.itemContainer), childAtPosition(
				allOf(withId(R.id.view_foreground),
						childAtPosition(withClassName(is("android.widget.FrameLayout")), 1)), 1),
				isDisplayed()));
		shoppingListItem.perform(click());

		floatingActionButton.perform(click());

		clickNavigationButton();

		ConditionWatcher.waitForCondition(waitForViewByIdCondition(R.id.emptyText));
	}

}

package hu.tvarga.cheaplist.ui;

import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.contrib.DrawerActions;
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
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withChild;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static hu.tvarga.cheaplist.ConditionWatchers.waitForViewByIdCondition;
import static hu.tvarga.cheaplist.ConditionWatchers.waitForViewByTextCondition;
import static hu.tvarga.cheaplist.ConditionWatchers.waitForViewByViewMatcherCondition;
import static hu.tvarga.cheaplist.EspressoHelpers.childAtPosition;
import static hu.tvarga.cheaplist.ui.EmailLogin.ensureDummyUserIsLoggedIn;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class CompareSearchTest {

	@Rule
	public ActivityTestRule<StartActivity> mActivityTestRule = new ActivityTestRule<>(
			StartActivity.class);

	@Before
	public void setUp() throws Exception {
		ensureDummyUserIsLoggedIn();
	}

	@Test
	public void compareSearch() throws Exception {
		ConditionWatcher.waitForCondition(waitForViewByIdCondition(R.id.itemContainer, true));

		Matcher<View> startListItem = allOf(withId(R.id.itemContainer),
				childAtPosition(childAtPosition(withId(R.id.itemsListStart), 0), 1));

		ConditionWatcher.waitForCondition(waitForViewByViewMatcherCondition(startListItem));

		ConditionWatcher.waitForCondition(waitForViewByIdCondition(R.id.searchMenuItem, true));

		ViewInteraction searchMenuItem = onView(withId(R.id.searchMenuItem));
		searchMenuItem.perform(click());

		ViewInteraction searchInputField = onView(allOf(withId(R.id.search_src_text),
				childAtPosition(allOf(withId(R.id.search_plate),
						childAtPosition(withId(R.id.search_edit_frame), 1)), 0), isDisplayed()));
		searchInputField.perform(replaceText("something that will never be found"),
				closeSoftKeyboard());

		searchInputField.perform(closeSoftKeyboard());

		onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());

		ConditionWatcher.waitForCondition(
				waitForViewByTextCondition(R.string.shopping_list_menu_item));

		ViewInteraction shoppingListMenuItem = onView(withText(R.string.shopping_list_menu_item));
		shoppingListMenuItem.perform(click());

		Matcher<View> titleMatcher = allOf(withId(R.id.toolbar),
				withChild(withText(R.string.shopping_list_menu_item)));
		ConditionWatcher.waitForCondition(waitForViewByViewMatcherCondition(titleMatcher));

		pressBack();

		ConditionWatcher.waitForCondition(waitForViewByIdCondition(R.id.searchMenuItem, true));

		Matcher<View> emptyListText = allOf(withId(R.id.itemsListStart),
				withText(R.string.no_data_available));
		ConditionWatcher.waitForCondition(waitForViewByViewMatcherCondition(emptyListText));

		ViewInteraction appCompatImageView = onView(withId(R.id.search_close_btn));
		appCompatImageView.perform(click());

		ConditionWatcher.waitForCondition(waitForViewByViewMatcherCondition(startListItem));
	}

}

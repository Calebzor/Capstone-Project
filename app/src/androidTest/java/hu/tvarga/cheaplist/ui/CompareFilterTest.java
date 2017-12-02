package hu.tvarga.cheaplist.ui;

import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;
import android.widget.TextView;

import com.azimolabs.conditionwatcher.ConditionWatcher;
import com.azimolabs.conditionwatcher.Instruction;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import hu.tvarga.cheaplist.R;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static hu.tvarga.cheaplist.ConditionWatchers.waitForViewByIdCondition;
import static hu.tvarga.cheaplist.ConditionWatchers.waitForViewByViewMatcherCondition;
import static hu.tvarga.cheaplist.EspressoHelpers.childAtPosition;
import static hu.tvarga.cheaplist.ui.EmailLogin.ensureDummyUserIsLoggedIn;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class CompareFilterTest {

	private static final int FIRST_CATEGORY = 0;
	private static final int SECOND_CATEGORY = 2;

	@Rule
	public ActivityTestRule<StartActivity> mActivityTestRule = new ActivityTestRule<>(
			StartActivity.class);

	@Before
	public void setUp() throws Exception {
		ensureDummyUserIsLoggedIn();
	}

	@Test
	public void compareFilter() throws Exception {
		ConditionWatcher.waitForCondition(waitForViewByIdCondition(R.id.itemContainer, true));

		final Matcher<View> startListItem = allOf(withId(R.id.itemContainer),
				childAtPosition(childAtPosition(withId(R.id.itemsListStart), 0), 1));
		final Matcher<View> startListItem2 = allOf(withId(R.id.itemContainer),
				childAtPosition(childAtPosition(withId(R.id.itemsListStart), 1), 1));

		ConditionWatcher.waitForCondition(waitForViewByViewMatcherCondition(startListItem));

		ConditionWatcher.waitForCondition(
				waitForViewByIdCondition(R.id.compareFilterMenuItem, true));

		ViewInteraction compareFilterMenuItem = onView(withId(R.id.compareFilterMenuItem));
		compareFilterMenuItem.perform(click());

		Matcher<View> listMatcher = withId(R.id.categoriesFilterList);

		ConditionWatcher.waitForCondition(waitForViewByViewMatcherCondition(listMatcher));

		// don't select 0 as that categories items are probably visible on screen
		onView(withId(R.id.categoriesFilterList)).perform(
				RecyclerViewActions.actionOnItemAtPosition(SECOND_CATEGORY, click()));

		pressBack();
		ConditionWatcher.waitForCondition(waitForViewByViewMatcherCondition(startListItem));

		// save first list elements name
		final String[] firstListItemName = new String[2];
		onView(startListItem).check(matches(new BaseMatcher<View>() {
			@Override
			public void describeTo(Description description) {
				// not needed
			}

			@Override
			public boolean matches(Object item) {
				firstListItemName[0] = (String) ((TextView) ((View) item).findViewById(R.id.name))
						.getText();
				return true;
			}
		}));
		onView(startListItem2).check(matches(new BaseMatcher<View>() {
			@Override
			public void describeTo(Description description) {
				// not needed
			}

			@Override
			public boolean matches(Object item) {
				firstListItemName[1] = (String) ((TextView) ((View) item).findViewById(R.id.name))
						.getText();
				return true;
			}
		}));

		compareFilterMenuItem.perform(click());

		ConditionWatcher.waitForCondition(waitForViewByViewMatcherCondition(listMatcher));

		onView(withId(R.id.categoriesFilterList)).perform(
				RecyclerViewActions.actionOnItemAtPosition(FIRST_CATEGORY, click()));
		onView(withId(R.id.categoriesFilterList)).perform(
				RecyclerViewActions.actionOnItemAtPosition(SECOND_CATEGORY, click()));

		pressBack();
		ConditionWatcher.waitForCondition(waitForViewByViewMatcherCondition(startListItem));

		// save first list elements name
		final String[] firstListItemNameAfterToggle = new String[2];

		ConditionWatcher.waitForCondition(new Instruction() {
			@Override
			public String getDescription() {
				return "Wait for view string to not match";
			}

			@Override
			public boolean checkCondition() {
				onView(startListItem).check(matches(new BaseMatcher<View>() {
					@Override
					public void describeTo(Description description) {
						// not needed
					}

					@Override
					public boolean matches(Object item) {
						firstListItemNameAfterToggle[0] =
								(String) ((TextView) ((View) item).findViewById(R.id.name))
										.getText();
						return true;
					}
				}));
				onView(startListItem2).check(matches(new BaseMatcher<View>() {
					@Override
					public void describeTo(Description description) {
						// not needed
					}

					@Override
					public boolean matches(Object item) {
						firstListItemNameAfterToggle[1] =
								(String) ((TextView) ((View) item).findViewById(R.id.name))
										.getText();
						return true;
					}
				}));
				return !firstListItemNameAfterToggle[0].equals(firstListItemName[0]) ||
						!firstListItemNameAfterToggle[1].equals(firstListItemName[1]);
			}
		});

		compareFilterMenuItem.perform(click());

		ConditionWatcher.waitForCondition(waitForViewByViewMatcherCondition(listMatcher));

		onView(withId(R.id.categoriesFilterList)).perform(
				RecyclerViewActions.actionOnItemAtPosition(FIRST_CATEGORY, click()));

		pressBack();
		ConditionWatcher.waitForCondition(waitForViewByViewMatcherCondition(startListItem));
	}

}

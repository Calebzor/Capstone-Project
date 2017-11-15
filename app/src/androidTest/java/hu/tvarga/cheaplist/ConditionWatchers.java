package hu.tvarga.cheaplist;

import android.support.annotation.NonNull;
import android.support.test.espresso.AmbiguousViewMatcherException;
import android.support.test.espresso.NoMatchingViewException;
import android.view.View;

import com.azimolabs.conditionwatcher.Instruction;

import org.hamcrest.Matcher;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

public class ConditionWatchers {

	@NonNull
	public static Instruction waitForViewByIdCondition(final int id) {
		return waitForViewByIdCondition(id, false);
	}

	@NonNull
	public static Instruction waitForViewByIdCondition(final int id,
			final boolean allowAmbiguousViewMatcherException) {
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
				catch (AmbiguousViewMatcherException e) {
					return allowAmbiguousViewMatcherException;
				}
			}
		};
	}

	@NonNull
	public static Instruction waitForViewByViewMatcherCondition(final Matcher<View> viewMatcher) {
		return new Instruction() {
			@Override
			public String getDescription() {
				return "Waiting for view";
			}

			@Override
			public boolean checkCondition() {
				try {
					viewMatcher.matches(isDisplayed());
					return true;
				}
				catch (NoMatchingViewException e) {
					return false;
				}
			}
		};
	}

	@NonNull
	public static Instruction waitForViewByContentDescriptionCondition(
			final String contentDescription) {
		return new Instruction() {
			@Override
			public String getDescription() {
				return "Waiting for view";
			}

			@Override
			public boolean checkCondition() {
				try {
					onView(withContentDescription(contentDescription)).check(
							matches(isDisplayed()));
					return true;
				}
				catch (NoMatchingViewException e) {
					return false;
				}
			}
		};
	}

	@NonNull
	public static Instruction waitForViewByContentDescriptionCondition(
			final int contentDescription) {
		return new Instruction() {
			@Override
			public String getDescription() {
				return "Waiting for view";
			}

			@Override
			public boolean checkCondition() {
				try {
					onView(withContentDescription(contentDescription)).check(
							matches(isDisplayed()));
					return true;
				}
				catch (NoMatchingViewException e) {
					return false;
				}
			}
		};
	}
}

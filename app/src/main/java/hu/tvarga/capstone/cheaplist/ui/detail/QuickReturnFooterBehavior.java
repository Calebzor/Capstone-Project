package hu.tvarga.capstone.cheaplist.ui.detail;

import android.animation.Animator;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.view.animation.Interpolator;

/**
 * Credits: https://github.com/bherbst/QuickReturnFooterSample
 */
public class QuickReturnFooterBehavior extends CoordinatorLayout.Behavior<View> {

	private static final Interpolator INTERPOLATOR = new FastOutSlowInInterpolator();
	private static final int DURATION = 200;

	private int ySinceDirectionChange;
	private boolean isShowing;
	private boolean isHiding;

	public QuickReturnFooterBehavior(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public boolean onStartNestedScroll(@NonNull CoordinatorLayout coordinatorLayout,
			@NonNull View child, @NonNull View directTargetChild, @NonNull View target, int axes,
			int type) {
		return (axes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0;
	}

	@Override
	public void onNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull View child,
			@NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed,
			int dyUnconsumed, int type) {
		if (dyUnconsumed > 0 && ySinceDirectionChange < 0 ||
				dyUnconsumed < 0 && ySinceDirectionChange > 0) {
			// We detected a direction change- cancel existing animations and reset our cumulative delta Y
			child.animate().cancel();
			ySinceDirectionChange = 0;
		}

		ySinceDirectionChange += dyUnconsumed;

		if (ySinceDirectionChange > child.getHeight() + getBottomMargin(child) &&
				child.getVisibility() == View.VISIBLE && !isHiding) {
			hide(child);
		}
		else if (ySinceDirectionChange < 0 && child.getVisibility() == View.INVISIBLE &&
				!isShowing) {
			show(child);
		}
	}

	private int getBottomMargin(View view) {
		CoordinatorLayout.LayoutParams layoutParams =
				(CoordinatorLayout.LayoutParams) view.getLayoutParams();
		return layoutParams.bottomMargin;
	}

	/**
	 * Hide the quick return view.
	 * <p>
	 * Animates hiding the view, with the view sliding down and out of the screen.
	 * After the view has disappeared, its visibility will change to GONE.
	 *
	 * @param view The quick return view
	 */
	private void hide(final View view) {
		isHiding = true;
		ViewPropertyAnimator animator = view.animate().translationY(
				view.getHeight() + getBottomMargin(view)).setInterpolator(INTERPOLATOR).setDuration(
				DURATION);

		animator.setListener(new Animator.AnimatorListener() {
			@Override
			public void onAnimationStart(Animator animator) {
				// no op
			}

			@Override
			public void onAnimationEnd(Animator animator) {
				// Prevent drawing the View after it is gone
				isHiding = false;
				view.setVisibility(View.INVISIBLE);
			}

			@Override
			public void onAnimationCancel(Animator animator) {
				// Canceling a hide should show the view
				isHiding = false;
				if (!isShowing) {
					show(view);
				}
			}

			@Override
			public void onAnimationRepeat(Animator animator) {
				// no op
			}
		});

		animator.start();
	}

	/**
	 * Show the quick return view.
	 * <p>
	 * Animates showing the view, with the view sliding up from the bottom of the screen.
	 * After the view has reappeared, its visibility will change to VISIBLE.
	 *
	 * @param view The quick return view
	 */
	private void show(final View view) {
		isShowing = true;
		ViewPropertyAnimator animator = view.animate().translationY(0).setInterpolator(INTERPOLATOR)
				.setDuration(DURATION);

		animator.setListener(new Animator.AnimatorListener() {
			@Override
			public void onAnimationStart(Animator animator) {
				view.setVisibility(View.VISIBLE);
			}

			@Override
			public void onAnimationEnd(Animator animator) {
				isShowing = false;
			}

			@Override
			public void onAnimationCancel(Animator animator) {
				// Canceling a show should hide the view
				isShowing = false;
				if (!isHiding) {
					hide(view);
				}
			}

			@Override
			public void onAnimationRepeat(Animator animator) {
				// no op
			}
		});

		animator.start();
	}
}
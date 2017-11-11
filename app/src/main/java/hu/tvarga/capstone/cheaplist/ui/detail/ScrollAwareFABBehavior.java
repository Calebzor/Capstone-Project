package hu.tvarga.capstone.cheaplist.ui.detail;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;
import android.view.View;

public class ScrollAwareFABBehavior extends CoordinatorLayout.Behavior<FloatingActionButton> {

	public ScrollAwareFABBehavior(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public boolean layoutDependsOn(CoordinatorLayout parent, FloatingActionButton fab,
			View dependency) {
		return dependency instanceof NestedScrollView;
	}

	@Override
	public boolean onDependentViewChanged(CoordinatorLayout parent, FloatingActionButton fab,
			View dependency) {
		if (dependency instanceof NestedScrollView) {
			float y = dependency.getY() + dependency.getScrollY();
			fab.setTranslationY(y);
		}
		return true;
	}

}
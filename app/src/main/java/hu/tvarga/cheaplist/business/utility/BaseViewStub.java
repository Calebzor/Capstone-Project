package hu.tvarga.cheaplist.business.utility;

import timber.log.Timber;

public abstract class BaseViewStub {

	protected void log(String methodName) {
		Timber.d(methodName);
	}
}

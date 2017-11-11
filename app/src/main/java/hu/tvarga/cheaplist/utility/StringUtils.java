package hu.tvarga.cheaplist.utility;

import timber.log.Timber;

public class StringUtils {

	private StringUtils() {
		// hide constructor
	}

	public static String setTextToPlaceholder(String textWithPlaceholder, Object... textToInsert) {
		String result;
		try {
			result = String.format(textWithPlaceholder, (Object[]) textToInsert);
		}
		catch (Exception ex) {
			result = textWithPlaceholder;
			Timber.e(ex);
		}
		return result;
	}

	public static boolean isEmpty(String filter) {
		return filter == null || filter.isEmpty();
	}
}

package hu.tvarga.capstone.cheaplist.dao.utility;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public final class GsonHelper {

	private static Gson gson;

	private GsonHelper() {
		// To prevent instantiation
	}

	/**
	 * Use this method instead of creating many Gson instances with "new Gson()".
	 *
	 * @return Gson instance
	 */
	public static Gson getGson() {
		if (gson == null) {
			gson = new GsonBuilder().create();
		}
		return gson;

	}
}

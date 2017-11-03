package hu.tvarga.capstone.cheaplist.business;

import java.util.HashMap;
import java.util.Map;

import hu.tvarga.capstone.cheaplist.R;

public class NutritionNameHelper {

	private NutritionNameHelper() {
		// hiding constructor
	}

	private static final Map<String, Integer> nutritionNameMap = new HashMap<>();

	static {
		nutritionNameMap.put("fat", R.string.fat);
		nutritionNameMap.put("saturatedFat", R.string.saturatedFat);
		nutritionNameMap.put("carbs", R.string.carbs);
		nutritionNameMap.put("sugar", R.string.sugar);
		nutritionNameMap.put("protein", R.string.protein);
		nutritionNameMap.put("salt", R.string.salt);
	}

	public static int getNutritionLocalizedName(String fieldName) {
		if (nutritionNameMap.containsKey(fieldName)) {
			return nutritionNameMap.get(fieldName);
		}
		return 0;
	}
}

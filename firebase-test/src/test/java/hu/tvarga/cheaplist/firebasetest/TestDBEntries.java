package hu.tvarga.cheaplist.firebasetest;

import hu.tvarga.cheaplist.dao.Item;
import hu.tvarga.cheaplist.dao.ItemCategory;
import hu.tvarga.cheaplist.dao.ManufacturerInformation;

public class TestDBEntries {

	public static Item getSoproni() {
		Item item = new Item();
		item.category = ItemCategory.ALCOHOL;
		item.name = "Soproni világos sör 4,5% 6 x 0,5 l";
		item.price = 1269d;
		item.pricePerUnit = 423d;
		item.unit = "l";
		item.description = "Jellemzők\n" + "100% magyar árpából\n" + "Termék információk\n" +
				"A Soproni Magyarország egyik legrégebbi, nagy múltra visszatekintő söre. A természetes összetevőknek és a szakértelemnek köszönhetően évek óta megbízható minőséget képvisel a lager kategóriájú sörök között. Kellemes, lágy ízének köszönhetően Magyarország legnépszerűbb söre, amely 100% magyar árpából készül.\n" +
				"Világos sör";
		//		NutritionInformation nutritionInformation = new NutritionInformation();
		//		nutritionInformation.energy = 32.8;
		//		nutritionInformation.fat = 0d;
		//		nutritionInformation.saturatedFat = 0d;
		//		nutritionInformation.carbs = 1.6;
		//		nutritionInformation.sugar = 0.6;
		//		nutritionInformation.protein = 0.4;
		//		nutritionInformation.salt = 0d;
		//		item.nutritionInformation = nutritionInformation;

		ManufacturerInformation manufacturerInformation = new ManufacturerInformation();
		manufacturerInformation.address =
				"Heineken Hungária Sörgyárak Zrt.\n" + "9400 Sopron\n" + "Vándor S. u. 1.";
		manufacturerInformation.contact =
				"Heineken Hungária Sörgyárak Zrt.\n" + "9400 Sopron\n" + "Vándor S. u. 1.\n" +
						"Vevőszolgálat: 06/99/516-200\n" + "Látogass el a www.soproni.hu oldalra";
		item.manufacturerInformation = manufacturerInformation;
		return item;
	}

	public static Item getBudweiser() {
		Item item = new Item();
		item.category = ItemCategory.ALCOHOL;
		item.name = "Budweiser Budvar Original cseh prémium világos sör 5% 0,5 l";
		item.price = 299d;
		item.pricePerUnit = 598d;
		item.unit = "l";
		item.description = "Cseh világos sör";

		ManufacturerInformation manufacturerInformation = new ManufacturerInformation();
		manufacturerInformation.address =
				"Karoliny Světlé 4. \n" + "7021 České Budějovice\n" + "Csehország";
		manufacturerInformation.contact =
				"Carlsberg Hungary Kft.\n" + "2040 Budaörs\n" + "Neumann J. u. 3.";
		item.manufacturerInformation = manufacturerInformation;
		return item;
	}

	public static Item getCarlsberg() {
		Item item = new Item();
		item.category = ItemCategory.ALCOHOL;
		item.name = "Carlsberg minőségi világos sör 5% 500 ml";
		item.price = 229d;
		item.pricePerUnit = 458d;
		item.unit = "l";
		item.description = "Minőségi világos sör";
		//		NutritionInformation nutritionInformation = new NutritionInformation();
		//		nutritionInformation.energy = 40d;
		//		item.nutritionInformation = nutritionInformation;

		ManufacturerInformation manufacturerInformation = new ManufacturerInformation();
		manufacturerInformation.address =
				"Carlsberg Hungary Kft.\n" + "2040 Budaörs\n" + "Neumann J. u. 3.";
		manufacturerInformation.contact =
				"Carlsberg Hungary Kft.\n" + "2040 Budaörs\n" + "Neumann J. u. 3.\n" +
						"Tel.: 06-23-888-500";
		item.manufacturerInformation = manufacturerInformation;
		return item;
	}
}

package hu.tvarga.capstone.cheaplist.dao;

import com.google.firebase.database.ServerValue;

public class Item extends DataObject {

	public String name;

	public Double price;
	public Double pricePerUnit;
	public String unit;
	public String currency = "HUF";

	public ItemCategory category;

	public String description;
	public String imageURL;

	public NutritionInformation nutritionInformation;

	public String manufacturerID;
	public ManufacturerInformation manufacturerInformation;

	public Object lastModified = ServerValue.TIMESTAMP;

}

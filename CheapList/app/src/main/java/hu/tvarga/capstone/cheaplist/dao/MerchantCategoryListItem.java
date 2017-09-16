package hu.tvarga.capstone.cheaplist.dao;

public class MerchantCategoryListItem extends Item {

	protected MerchantCategoryListItem() {
	}

	public MerchantCategoryListItem(String id, String name, Double price, Double pricePerUnit,
			String unit, String currency) {
		this.id = id;

		this.name = name;
		this.price = price;
		this.pricePerUnit = pricePerUnit;
		this.unit = unit;
		this.currency = currency;
	}

	public MerchantCategoryListItem(Item item) {
		id = item.id;

		name = item.name;
		price = item.price;
		pricePerUnit = item.pricePerUnit;
		unit = item.unit;
		currency = item.currency;
	}
}

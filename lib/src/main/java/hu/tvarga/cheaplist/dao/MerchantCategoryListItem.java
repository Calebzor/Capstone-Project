package hu.tvarga.cheaplist.dao;

public class MerchantCategoryListItem extends Item {

	protected MerchantCategoryListItem() {
	}

	public MerchantCategoryListItem(Item item) {
		id = item.id;

		name = item.name;
		price = item.price;
		pricePerUnit = item.pricePerUnit;
		unit = item.unit;
		currency = item.currency;

		imageURL = item.imageURL;
		thumbnail = item.thumbnail;
	}
}

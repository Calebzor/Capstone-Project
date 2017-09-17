package hu.tvarga.capstone.cheaplist.dao;

public class ShoppingListItem extends MerchantCategoryListItem {

	public boolean checked;
	public Merchant merchant;

	protected ShoppingListItem() {
	}

	public ShoppingListItem(String id, String name, Double price, Double pricePerUnit, String unit,
			String currency, boolean checked, Merchant merchant) {
		super(id, name, price, pricePerUnit, unit, currency);
		this.checked = checked;
		this.merchant = merchant;
	}

	public ShoppingListItem(Item item, Merchant merchant) {
		super(item);
		this.merchant = merchant;
	}
}

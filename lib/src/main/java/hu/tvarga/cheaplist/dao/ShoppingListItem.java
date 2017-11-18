package hu.tvarga.cheaplist.dao;

public class ShoppingListItem extends MerchantCategoryListItem {

	public boolean checked;
	public Merchant merchant;

	protected ShoppingListItem() {
	}

	public ShoppingListItem(Item item, Merchant merchant) {
		super(item);
		this.merchant = merchant;
	}
}

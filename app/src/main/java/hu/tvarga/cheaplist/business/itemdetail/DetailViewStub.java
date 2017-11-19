package hu.tvarga.cheaplist.business.itemdetail;

import hu.tvarga.cheaplist.business.utility.BaseViewStub;
import hu.tvarga.cheaplist.dao.Item;

public class DetailViewStub extends BaseViewStub implements DetailContract.View {

	@Override
	public void updateUI(Item item) {
		log("updateUI");
	}

	@Override
	public void showFabAsRemove() {
		log("showFabAsRemove");
	}

	@Override
	public void showFabAsAdd() {
		log("showFabAsAdd");
	}
}

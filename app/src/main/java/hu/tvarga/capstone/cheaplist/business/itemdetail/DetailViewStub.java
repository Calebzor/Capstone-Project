package hu.tvarga.capstone.cheaplist.business.itemdetail;

import hu.tvarga.capstone.cheaplist.dao.Item;
import timber.log.Timber;

public class DetailViewStub implements DetailContract.View {

	@Override
	public void updateUI(Item item) {
		Timber.d("updateUI");
	}

	@Override
	public void showFabAsRemove() {
		Timber.d("showFabAsRemove");
	}

	@Override
	public void showFabAsAdd() {
		Timber.d("showFabAsAdd");
	}
}

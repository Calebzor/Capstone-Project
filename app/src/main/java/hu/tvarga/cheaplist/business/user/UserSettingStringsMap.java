package hu.tvarga.cheaplist.business.user;

import hu.tvarga.cheaplist.R;

public enum UserSettingStringsMap {
	IMAGE_DOWNLOADING_DISABLED(R.string.dont_download_images_from_the_internet,
			R.string.dont_download_images_from_the_internet_description);

	private final int name;
	private final int description;

	UserSettingStringsMap(int name, int description) {
		this.name = name;
		this.description = description;
	}

	public int getName() {
		return name;
	}

	public int getDescription() {
		return description;
	}

	@Override
	public String toString() {
		return "UserSettingStringsMap{" + "name=" + name + ", description=" + description + "} " +
				super.toString();
	}
}

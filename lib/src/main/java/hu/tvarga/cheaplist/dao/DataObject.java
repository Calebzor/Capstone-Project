package hu.tvarga.cheaplist.dao;

import com.google.gson.Gson;

import java.io.Serializable;

import hu.tvarga.cheaplist.utility.GsonHelper;

public class DataObject implements Serializable {

	private static final Gson GSON = GsonHelper.getGson();

	public String id;

	@Override
	public String toString() {
		return GSON.toJson(this);
	}
}

package hu.tvarga.capstone.cheaplist.dao;

import com.google.gson.Gson;

import java.io.Serializable;

import static hu.tvarga.capstone.cheaplist.utility.GsonHelper.getGson;

public class DataObject implements Serializable {

	private static final Gson GSON = getGson();

	public String id;

	@Override
	public String toString() {
		return GSON.toJson(this);
	}
}

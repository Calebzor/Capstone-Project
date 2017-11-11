package hu.tvarga.cheaplist.firebasetest;

import com.google.firebase.database.DatabaseError;

import hu.tvarga.cheaplist.dao.DataObject;

public interface DatabaseObjectCallback<T extends DataObject> {

	void onDataChange(T data);
	void onCancelled(DatabaseError databaseError);
}

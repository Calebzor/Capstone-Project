package hu.tvarga.capstone.cheaplist.firebasetest;

import com.google.firebase.database.DatabaseError;

import hu.tvarga.capstone.cheaplist.dao.DataObject;

public interface DatabaseObjectCallback<T extends DataObject> {

	void onDataChange(T data);
	void onCancelled(DatabaseError databaseError);
}

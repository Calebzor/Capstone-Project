package hu.tvarga.cheaplist.di;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import dagger.Module;
import dagger.Provides;

@Module
public class FirebaseModule {

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    public FirebaseModule() {
        this.firebaseAuth = FirebaseAuth.getInstance();
        this.firebaseFirestore = FirebaseFirestore.getInstance();
    }

    @Provides
    FirebaseAuth provideFirebaseAuth() {
        return firebaseAuth;
    }

    @Provides
    FirebaseFirestore provideFirebaseFirestore() {
        return firebaseFirestore;
    }
}

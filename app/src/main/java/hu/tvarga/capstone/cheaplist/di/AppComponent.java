package hu.tvarga.capstone.cheaplist.di;

import android.content.Context;

import dagger.Component;
import dagger.android.AndroidInjectionModule;
import hu.tvarga.capstone.cheaplist.CheapListApp;
import hu.tvarga.capstone.cheaplist.di.scopes.ApplicationScope;

@ApplicationScope
@Component(modules = {AppModule.class, AndroidInjectionModule.class})
public interface AppComponent {

	void inject(CheapListApp app);

	Context getContext();

}
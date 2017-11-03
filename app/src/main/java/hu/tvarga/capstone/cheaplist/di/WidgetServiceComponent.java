package hu.tvarga.capstone.cheaplist.di;

import dagger.Component;
import hu.tvarga.capstone.cheaplist.di.androidinjectors.ServiceModule;
import hu.tvarga.capstone.cheaplist.di.scopes.ApplicationScope;
import hu.tvarga.capstone.cheaplist.widget.WidgetService;

@Component(modules = ServiceModule.class)
@ApplicationScope
public interface WidgetServiceComponent {

	void inject(WidgetService widgetService);
}

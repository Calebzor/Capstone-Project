package hu.tvarga.cheaplist.di;

import dagger.Component;
import hu.tvarga.cheaplist.di.androidinjectors.ServiceModule;
import hu.tvarga.cheaplist.di.scopes.ApplicationScope;
import hu.tvarga.cheaplist.widget.WidgetService;

@Component(modules = ServiceModule.class)
@ApplicationScope
public interface WidgetServiceComponent {

	void inject(WidgetService widgetService);
}

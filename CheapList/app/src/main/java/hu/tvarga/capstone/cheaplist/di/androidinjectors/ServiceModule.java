package hu.tvarga.capstone.cheaplist.di.androidinjectors;

import android.content.Context;

import dagger.Module;
import dagger.Provides;
import hu.tvarga.capstone.cheaplist.widget.WidgetService;

@Module
public class ServiceModule {

	private WidgetService widgetService;

	public ServiceModule(WidgetService widgetService) {
		this.widgetService = widgetService;
	}

	@Provides
	WidgetService provideWidgetService() {
		return widgetService;
	}

	@Provides
	Context provideContext() {
		return widgetService.getApplicationContext();
	}

}

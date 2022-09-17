package modules;

import com.google.inject.AbstractModule;
import core.databinders.WiFreeFormattersProvider;
import play.data.format.Formatters;

public class FormattersModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(Formatters.class).toProvider(WiFreeFormattersProvider.class);
    }

}

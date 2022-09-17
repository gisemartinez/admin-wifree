package core.databinders;

import models.Admin;
import models.FieldConfig;
import models.Portal;
import play.data.format.Formatters;
import play.i18n.MessagesApi;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

@Singleton
public class WiFreeFormattersProvider implements Provider<Formatters> {

    private final MessagesApi messagesApi;

    @Inject
    public WiFreeFormattersProvider(MessagesApi messagesApi) {
        this.messagesApi = messagesApi;
    }

    @Override
    public Formatters get() {
        Formatters formatters = new Formatters(messagesApi);

        formatters.register(Portal.class, new PortalDataBinder());
        formatters.register(Admin.class, new AdminDataBinder());
        formatters.register(FieldConfig.class, new FieldConfigDataBinder());

        return formatters;
    }

}

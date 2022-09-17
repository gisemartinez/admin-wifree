package core.databinders;

import daos.PortalDAO;
import models.Portal;
import play.data.format.Formatters;

import java.util.Locale;

public class PortalDataBinder extends Formatters.SimpleFormatter<Portal> implements WiFreeDataBinder<Portal> {

    @Override
    public Portal parse(String text, Locale locale) {
        return parseEntity(text, locale, new PortalDAO());
    }

    @Override
    public String print(Portal portal, Locale locale) {
        return printEntity(portal, locale);
    }

}

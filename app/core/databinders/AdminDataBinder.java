package core.databinders;

import daos.AdminDAO;
import models.Admin;
import play.data.format.Formatters;

import java.text.ParseException;
import java.util.Locale;

public class AdminDataBinder extends Formatters.SimpleFormatter<Admin> implements WiFreeDataBinder<Admin> {

    @Override
    public Admin parse(String text, Locale locale) throws ParseException {
        return parseEntity(text, locale, new AdminDAO());
    }

    @Override
    public String print(Admin admin, Locale locale) {
        return printEntity(admin, locale);
    }

}

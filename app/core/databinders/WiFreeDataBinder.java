package core.databinders;

import daos.GenericDAO;
import models.BaseModel;
import utils.StringHelper;

import java.util.Locale;

public interface WiFreeDataBinder<T extends BaseModel> {

	default <DAO extends GenericDAO<T>> T parseEntity(String text, Locale locale, DAO dao) {
		if (StringHelper.isNotEmpty(text)) {
			final long id = Long.parseLong(text);
			return dao.getOrCreate(id);
		} else {
			return null;
		}
	}
	
	default String printEntity(T entity, Locale locale) {
		return entity.getId().toString();
	}

}

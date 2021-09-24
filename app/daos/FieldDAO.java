package daos;

import models.Field;
import models.Portal;

public class FieldDAO extends GenericDAO<Field> {
    public FieldDAO() {
        super(Field.class);
    }

    public Portal findPortal(Long fieldId) {
        Field field = get(fieldId);
        return field.getSurvey().getPortal();
    }

}

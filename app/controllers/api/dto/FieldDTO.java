package controllers.api.dto;

import models.Field;

public class FieldDTO extends WiFreeDTO {

    public String id;
    public String type;
    public FieldConfigDTO config;

    public FieldDTO() {}

    public FieldDTO(String id, String type, FieldConfigDTO config) {
        this.id = id;
        this.type = type;
        this.config = config;
    }

    public static FieldDTO fromDomain(Field field) {
        FieldConfigDTO config = FieldConfigDTO.fromDomain(field.getConfig());
        return new FieldDTO(field.getId().toString(), field.getType(), config);
    }

}

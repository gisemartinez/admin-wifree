package models;

import io.ebean.annotation.DbJsonB;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class Field extends BaseModel {

    @ManyToOne(optional = false)
    private Survey survey;

    private String type;

    @DbJsonB
    private FieldConfig config;

    public Field(Long id, String type, FieldConfig config) {
        this.id = id;
        this.type = type;
        this.config = config;
    }

    public Field copy() {
        return new Field(id, type, config.copy());
    }

    @Override
    public String toLogString() {
        return toLogString("id=" + id, "type=" + type, "config=\n\t" + config.toLogString());
    }

    public Survey getSurvey() {
        return survey;
    }

    public void setSurvey(Survey survey) {
        this.survey = survey;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public FieldConfig getConfig() {
        return config;
    }

    public void setConfig(FieldConfig config) {
        this.config = config;
    }
}

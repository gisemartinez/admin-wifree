package models;

import io.ebean.annotation.NotNull;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Table(
        uniqueConstraints = @UniqueConstraint(columnNames = {"field_id", "user_id"})
)
@Entity
public class FieldAnswer extends BaseModel {

    @ManyToOne(optional = false)
    private Field field;

    @ManyToOne(optional = false)
    private NetworkUser user;

    @NotNull
    private String answer;

    public FieldAnswer(Field field, NetworkUser user, String answer) {
        this.field = field;
        this.user = user;
        this.answer = answer;
    }

    @Override
    public String toLogString() {
        return "field: " + field.getConfig().key + ", user: " + user.id + ", answer: " + answer;
    }

    public Field getField() {
        return field;
    }

    public void setField(Field field) {
        this.field = field;
    }

    public NetworkUser getUser() {
        return user;
    }

    public void setUser(NetworkUser user) {
        this.user = user;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}

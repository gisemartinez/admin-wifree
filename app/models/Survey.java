package models;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.util.List;
import java.util.stream.Collectors;

@Entity
public class Survey extends BaseModel {

    @ManyToOne(optional = false)
    private Portal portal;

    private String title;

    private boolean enabled;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "survey")
    private List<Field> fields;

    public Survey(Long id, Portal portal, String title, List<Field> fields, boolean enabled) {
        this.id = id;
        this.portal = portal;
        this.title = title;
        this.fields = fields;
        this.enabled = enabled;
    }

    public Survey copy() {
        return new Survey(id, portal, title, fields.stream().map(Field::copy).collect(Collectors.toList()), enabled);
    }

    @Override
    public String toLogString() {
        return toLogString("id=" + id,
                "portalId=" + portal.getId(),
                "title=" + title,
                "enabled=" + enabled,
                "fields=\n\t" + fields.stream().map(Field::toLogString).collect(Collectors.joining("\n\t")));
    }

    public List<Field> getFields() {
        return fields;
    }

    public void setFields(List<Field> fields) {
        this.fields = fields;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Portal getPortal() {
        return portal;
    }

    public void setPortal(Portal portal) {
        this.portal = portal;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}


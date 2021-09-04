package models;

import io.ebean.Model;
import io.ebean.annotation.WhenCreated;
import io.ebean.annotation.WhenModified;
import utils.StringHelper;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Created by jesu on 08/06/17.
 */
@MappedSuperclass
public abstract class BaseModel extends Model implements Serializable, WiFreeEntity {

	@Id
	protected Long id;

	@WhenCreated
	protected Timestamp whenCreated;

	@WhenModified
	protected Timestamp whenModified;

	public boolean isNew() {
		return id == null || id == 0L;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	protected String toLogString(String... attributes) {
		return StringHelper.toLogString(attributes);
	}

	public abstract String toLogString();

	public Timestamp getWhenCreated() {
		return whenCreated;
	}

	public void setWhenCreated(Timestamp whenCreated) {
		this.whenCreated = whenCreated;
	}

	public Timestamp getWhenModified() {
		return whenModified;
	}

	public void setWhenModified(Timestamp whenModified) {
		this.whenModified = whenModified;
	}

}

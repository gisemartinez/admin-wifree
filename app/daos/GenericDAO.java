package daos;

import io.ebean.Ebean;
import io.ebean.EbeanServer;
import io.ebean.Expression;
import models.BaseModel;
import play.Logger;

import javax.annotation.Nullable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import static java.util.Arrays.asList;

/**
 * Created by jesu on 12/06/17.
 */
public abstract class GenericDAO<T extends BaseModel> {

	protected EbeanServer server;
	protected final Class<T> ENTITY_TYPE;
	protected final Logger.ALogger logger = Logger.of(this.getClass());


	public GenericDAO(Class<T> cls) {
		ENTITY_TYPE = cls;
	}


	public void save(T object) {
		Ebean.save(object);
		logger.debug("Object saved={}", object);
	}

	public void saveAll(T... objects) {
		saveAll(asList(objects));
	}

	public void saveAll(List<T> objects) {
		Ebean.saveAll(objects);
		logger.debug("Objects saved={}", objects);
	}

	public boolean delete(T object) {
		boolean deleted = Ebean.delete(object);
		if (deleted) logger.debug("Object deleted={}", object);
		return deleted;
	}

	public T get(Long id) {
		logger.debug("Returning object type {} with id {}", ENTITY_TYPE, id);
		return Ebean.find(ENTITY_TYPE, id);
	}

	public List<T> getAll() {
		logger.debug("Returning all objects type {}", ENTITY_TYPE);
		return Ebean.find(ENTITY_TYPE).findList();
	}

	public List<T> listWhere(Expression expression) {
		logger.debug("Returning all objects type {} matching {}", ENTITY_TYPE, expression);
		return Ebean.find(ENTITY_TYPE).where(expression).findList();
	}

	@Nullable
	public T find(Expression expression) {
		logger.debug("Returning first object of type {} matching {}", ENTITY_TYPE, expression);
		List<T> ts = listWhere(expression);
		return ts.size() == 0 ? null : ts.get(0);
	}
	
	public T getOrCreate(Long id) {
		logger.debug("Returning object of type {} with id {} or creating it if not found", ENTITY_TYPE, id);
		T entity = get(id);
		try {
			return entity == null ?  createEntity(id) : entity;
		} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
			// throw new Exception(e); // TODO: mejorar y loguear error
			return null;
		}
	}
	
	public T getOrCreate(T entity) {
		return getOrCreate(entity.getId());
	}
	
	protected T createEntity(Long id) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
		final Constructor<T> idConstructor = ENTITY_TYPE.getConstructor(Long.class);
		return idConstructor.newInstance(id);
	}

	public EbeanServer getServer() {
		return server;
	}

	public Class<T> getENTITY_TYPE() {
		return ENTITY_TYPE;
	}

}

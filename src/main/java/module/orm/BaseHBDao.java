package module.orm;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Resource;
import javax.persistence.Id;

import module.util.Assert;
import module.util.PageUtil;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

/**
 * @author Junyan Base Hibernate DAO Class
 */
public abstract class BaseHBDao<M extends Serializable, PK extends Serializable> {

	private String pkName = "id";

	private Class<M> entityClass;

	private final String listAllHql;

	private final String countAllHql;

	@SuppressWarnings("unchecked")
	public BaseHBDao() {
		entityClass = (Class<M>) ((ParameterizedType) getClass().getGenericSuperclass())
				.getActualTypeArguments()[0];
		Field[] fields = entityClass.getDeclaredFields();
		for (Field f : fields) {
			if (f.isAnnotationPresent(Id.class)) {
				pkName = f.getName();
			}
		}
		Assert.notNull(pkName);
		listAllHql = "FROM " + entityClass.getSimpleName() + " ORDER BY " + pkName;
		countAllHql = "SELECT COUNT(*) FROM " + entityClass.getSimpleName();
	}

	public BaseHBDao(Class<M> c, String idName) {
		entityClass = c;
		pkName = idName;
		Assert.notNull(pkName);
		listAllHql = "FROM " + entityClass.getSimpleName() + " ORDER BY " + pkName;
		countAllHql = "SELECT COUNT(*) FROM " + entityClass.getSimpleName();
	}

	@Resource
	private SessionFactory sessionFactory;

	/** 获取当前线程的session */
	public Session getCurrentSession() {
		// 事务必须是开启的（REQUIRED），否则获取不到
		return sessionFactory.getCurrentSession();
	}

	@SuppressWarnings("unchecked")
	public PK save(M model) {
		try {
			return (PK) getCurrentSession().save(model);
		} catch (RuntimeException e) {
			throw e;
		}
	}

	public void saveOrUpdate(M model) {
		try {
			getCurrentSession().saveOrUpdate(model);
		} catch (RuntimeException e) {
			throw e;
		}
	}

	public void update(M model) {
		try {
			getCurrentSession().update(model);
		} catch (RuntimeException e) {
			throw e;
		}
	}

	public void merge(M model) {
		try {
			getCurrentSession().merge(model);
		} catch (RuntimeException e) {
			throw e;
		}
	}

	public void delete(PK id) {
		try {
			getCurrentSession().delete(get(id));
		} catch (RuntimeException e) {
			throw e;
		}
	}

	public void deleteObject(M model) {
		try {
			getCurrentSession().delete(model);
		} catch (RuntimeException e) {
			throw e;
		}
	}

	@SuppressWarnings("unchecked")
	public M get(PK id) {
		try {
			return (M) getCurrentSession().get(entityClass, id);
		} catch (RuntimeException e) {
			throw e;
		}
	}

	public int countAll() {
		try {
			Long total = aggregate(countAllHql);
			return total.intValue();
		} catch (RuntimeException e) {
			throw e;
		}
	}

	public List<M> listAll() {
		try {
			return list(listAllHql, -1, -1);
		} catch (RuntimeException e) {
			throw e;
		}
	}

	public List<M> listAll(int pageNumber, int pageSize) {
		try {
			return list(listAllHql, pageNumber, pageSize);
		} catch (RuntimeException e) {
			throw e;
		}
	}

	public boolean exits(PK id) {
		try {
			return get(id) != null;
		} catch (RuntimeException e) {
			throw e;
		}
	}

	public void flush() {
		try {
			getCurrentSession().flush();
		} catch (RuntimeException e) {
			throw e;
		}
	}

	public void clear() {
		try {
			getCurrentSession().clear();
		} catch (RuntimeException e) {
			throw e;
		}
	}

	protected int executeUpdate(String hql, final Object... paramList) {
		Query query = getCurrentSession().createQuery(hql);
		if (paramList != null) {
			setParameters(query, paramList);
		}
		return query.executeUpdate();
	}

	@SuppressWarnings("unchecked")
	protected <T> List<T> list(final String hql, final int pageNumber, final int pageSize,
			final Object... paramList) {
		Query query = getCurrentSession().createQuery(hql);
		if (paramList != null) {
			setParameters(query, paramList);
		}
		if (pageSize > -1) {
			query.setMaxResults(pageSize);
		}
		if (pageSize > 0) {
			query.setFirstResult(PageUtil.getPageStart(pageNumber, pageSize));
		} else {
			query.setFirstResult(0);
		}
		return (List<T>) query.list();
	}

	@SuppressWarnings("unchecked")
	protected <T> T unique(final String hql, final Object... paramList) {
		Query query = getCurrentSession().createQuery(hql);
		if (paramList != null) {
			setParameters(query, paramList);
		}
		return (T) query.uniqueResult();
	}

	@SuppressWarnings("unchecked")
	protected <T> T aggregate(final String hql, final Map<String, Collection<?>> map,
			final Object... paramList) {
		Query query = getCurrentSession().createQuery(hql);
		if (paramList != null) {
			setParameters(query, paramList);
			if (map != null) {
				for (Entry<String, Collection<?>> entry : map.entrySet()) {
					query.setParameterList(entry.getKey(), entry.getValue());
				}
			}
		}
		return (T) query.uniqueResult();
	}

	@SuppressWarnings("unchecked")
	protected <T> T aggregate(final String hql, final Object... paramList) {
		Query query = getCurrentSession().createQuery(hql);
		if (paramList != null) {
			setParameters(query, paramList);
		}
		return (T) query.uniqueResult();
	}

	/**
	 * 为Query自动设置参数列表
	 */
	protected void setParameters(Query query, Object[] paramList) {
		if (paramList != null) {
			for (int i = 0; i < paramList.length; i++) {
				if (paramList[i] instanceof Date) {
					query.setTimestamp(i, (Date) paramList[i]);
				} else {
					query.setParameter(i, paramList[i]);
				}
			}
		}
	}

}
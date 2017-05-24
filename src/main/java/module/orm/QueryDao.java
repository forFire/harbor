package module.orm;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;

public class QueryDao<T, PK extends Serializable> extends CriteriaDao<T, Serializable> {

	@SuppressWarnings("unchecked")
	public <X> X getHql(final String hql, final Object... values) {
		return (X) createQuery(hql, values).uniqueResult();
	}

	@SuppressWarnings("unchecked")
	public <X> List<X> find(final String hql, final Object... values) {
		return createQuery(hql, values).list();
	}

	public int batchExecuteMaster(final String hql, final Object... values) {
		return createQueryMaster(hql, values).executeUpdate();
	}

	// --------------------------------------------------------------------

	/**
	 * 根据查询HQL与参数列表创建Query对象.
	 */
	protected Query createQuery(final String queryString, final Object... values) {
		Query query = getSession().createQuery(queryString);
		if (values != null) {
			for (int i = 0; i < values.length; i++) {
				query.setParameter(i, values[i]);
			}
		}
		return query;
	}

	protected Query createQueryMaster(final String queryString, final Object... values) {
		Query query = getSessionMaster().createQuery(queryString);
		if (values != null) {
			for (int i = 0; i < values.length; i++) {
				query.setParameter(i, values[i]);
			}
		}
		return query;
	}

	/**
	 * 根据查询HQL与参数列表创建Query对象.Map
	 */
	protected Query createQuery(final String queryString, final Map<String, Object> values) {
		Query query = getSession().createQuery(queryString);
		if (values != null) {
			// 格式 from Account where name=:name
			query.setProperties(values);
		}
		return query;
	}

}
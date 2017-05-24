package module.orm;

import java.io.Serializable;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

public class CriteriaDao<T, PK extends Serializable> extends Dao<T, Serializable> {

	@SuppressWarnings("unchecked")
	public T getBy(final String propertyName, final Object value) {
		Criterion criterion = Restrictions.eq(propertyName, value);
		return (T) createCriteria(criterion).uniqueResult();
	}

	public List<T> findBy(final String propertyName, final Object value) {
		Criterion criterion = Restrictions.eq(propertyName, value);
		return find(criterion);
	}

	public List<T> findByIds(List<PK> ids) {
		return find(Restrictions.in(getIdName(), ids));
	}

	@SuppressWarnings("unchecked")
	public List<T> findAllOrder(String orderBy, boolean isAsc) {
		Criteria criteria = createCriteria();
		if (isAsc) {
			criteria.addOrder(Order.asc(orderBy));
		} else {
			criteria.addOrder(Order.desc(orderBy));
		}
		return criteria.list();
	}

	// -----------------------------待整理---------------------------------------

	@SuppressWarnings("unchecked")
	public List<T> findAll() {
		Criteria criteria = createCriteria();
		return criteria.list();
	}

	public int countAll() {
		Criteria criteria = createCriteria();
		int totalCount = (Integer) criteria.setProjection(Projections.rowCount()).uniqueResult();
		return totalCount;
	}

	// --------------------------------------------------------------------

	@SuppressWarnings("unchecked")
	protected List<T> find(final Criterion... criterions) {
		return createCriteria(criterions).list();
	}

	protected Criteria createCriteria(final Criterion... criterions) {
		Criteria criteria = getSession().createCriteria(entityClass);
		for (Criterion c : criterions) {
			criteria.add(c);
		}
		return criteria;
	}

}
package module.orm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.Projections;
import org.hibernate.internal.CriteriaImpl;
import org.hibernate.transform.ResultTransformer;
import org.springframework.util.Assert;

/**
 * @author fyq
 */
@SuppressWarnings("unchecked")
public class BaseDao<T, PK extends Serializable> extends QueryDao<T, PK> {

	/**
	 * 查询所有 无排序 由criteria实现
	 */
	public Page<T> findAll(final Page<T> page) {
		return findPage(page);
	}

	/**
	 * query 实现
	 */
	public Page<T> findPage(final Page<T> page, final String hql, final Object... values) {
		Query query = createQuery(hql, values);
		if (page.isAutoCount()) {
			page.setTotalCount(totalCount(hql, values));
		}
		setByPage(query, page);
		List<T> result = query.list();
		page.setResult(result);
		return page;
	}

	/**
	 * criteria 实现
	 */
	public Page<T> findPage(final Page<T> page, final Criterion... criterions) {
		Criteria criteria = createCriteria(criterions);
		if (page.isAutoCount()) {
			page.setTotalCount(totalCount(criteria));
		}
		setByPage(criteria, page);
		List<T> result = criteria.list();
		page.setResult(result);
		return page;
	}

	// --------------------------------------------------------------------

	/**
	 * 设置条件by page query 实现
	 */
	protected Query setByPage(final Query query, final Page<T> page) {
		if (page.getUse2Page().equals("Y")) {
			query.setFirstResult(page.getFirst());
			query.setMaxResults(page.getPageSize());
		}
		return query;
	}

	/**
	 * 设置条件by page criteria 实现
	 */
	protected Criteria setByPage(final Criteria criteria, final Page<T> page) {
		if (page.getUse2Page().equals("Y")) {
			criteria.setFirstResult(page.getFirst());
			criteria.setMaxResults(page.getPageSize());
		}

		if (page.isOrderBySetted()) {
			String[] orderByArray = StringUtils.split(page.getOrderBy(), ',');
			String[] orderArray = StringUtils.split(page.getOrder(), ',');
			Assert.isTrue(orderByArray.length == orderArray.length, "分页多重个数不相等");
			for (int i = 0; i < orderByArray.length; i++) {
				if (Page.ASC.equals(orderArray[i])) {
					criteria.addOrder(Order.asc(orderByArray[i]));
				} else {
					criteria.addOrder(Order.desc(orderByArray[i]));
				}
			}
		} else {
			criteria.addOrder(Order.desc(getIdName()));
		}

		return criteria;
	}

	/**
	 * 获取总数 query 实现
	 */
	protected long totalCount(final String hql, final Object... values) {
		String countHql = HqlUtils.getCountHql(hql);
		return getHql(countHql, values);
	}

	/**
	 * 获取总数 criteria 实现
	 */
	@SuppressWarnings("rawtypes")
	protected long totalCount(final Criteria criteria) {
		CriteriaImpl impl = (CriteriaImpl) criteria;
		// 先把Projection、ResultTransformer、OrderBy取出来,清空三者后再执行Count操作
		Projection projection = impl.getProjection();
		ResultTransformer transformer = impl.getResultTransformer();
		List<CriteriaImpl.OrderEntry> orderEntries = null;
		orderEntries = (List) ReflectionUtils.getFieldValue(impl, "orderEntries");
		ReflectionUtils.setFieldValue(impl, "orderEntries", new ArrayList());
		// 执行Count查询
		long totalCount = (Long) criteria.setProjection(Projections.rowCount()).uniqueResult();
		// 将之前的Projection,ResultTransformer和OrderBy条件重新设回去
		criteria.setProjection(projection);
		if (projection == null) {
			criteria.setResultTransformer(CriteriaSpecification.ROOT_ENTITY);
		}
		if (transformer != null) {
			criteria.setResultTransformer(transformer);
		}
		ReflectionUtils.setFieldValue(impl, "orderEntries", orderEntries);
		return totalCount;
	}

}

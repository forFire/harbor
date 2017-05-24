package module.orm;

import module.util.Assert;

import org.apache.commons.lang3.StringUtils;

/**
 * @author fyq
 */
public class HqlUtils {

	public static String getCountHql(String hql) {
		// select子句与order by子句会影响count查询,进行简单的排除.
		String acountHql = "from " + StringUtils.substringAfter(hql, "from");
		acountHql = StringUtils.substringBefore(acountHql, "order by");
		acountHql = acountHql.replaceAll("left.*?where", "where");
		return "select count(*) " + acountHql;
	}

	public static void main(String[] args) {
		String hql = "select aa from Account where a=a order by a";
		System.out.println(getCountHql(hql));
		hql = "select s from Ship s left join fetch s.shipStatus where s.id in (select us.shipId from UserShip us where us.userId=? ) ";
		System.out.println(getCountHql(hql));
	}

	public static <T> void setOrderByPage(StringBuffer sb, Page<T> page) {
		if (page.isOrderBySetted()) {
			// sb.append("order by name desc,id asc ");
			sb.append("order by ");
			String[] orderByArray = StringUtils.split(page.getOrderBy(), ',');
			String[] orderArray = StringUtils.split(page.getOrder(), ',');
			Assert.isTrue(orderByArray.length == orderArray.length,
					"OrderBy length not equal Order");
			for (int i = 0; i < orderByArray.length; i++) {
				if (i > 0)
					sb.append(",");
				sb.append(orderByArray[i] + " " + orderArray[i]);
			}
		}
	}
}

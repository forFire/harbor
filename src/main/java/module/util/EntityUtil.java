package module.util;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class EntityUtil {

	@SuppressWarnings("all")
	public static <T> String[] nullField(Class<T> type, T entity) {
		try {
			StringBuffer sb = new StringBuffer();
			List<String> strList = new ArrayList<String>();
			Method[] methods = type.getMethods();
			for (Method method : methods) {
				String methodName = method.getName();
				if (methodName.startsWith("get") && null == method.invoke(entity, null)) {
					sb.append(methodName.substring(3, 4).toLowerCase());
					sb.append(methodName.substring(4));
					strList.add(sb.toString());
					sb.delete(0, sb.length());
				}
			}
			return strList.toArray(new String[strList.size()]);
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}
	}

}

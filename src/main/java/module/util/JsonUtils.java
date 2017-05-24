package module.util;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.JsonParser.Feature;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.JavaType;

/**
 * @author fyq
 */
public class JsonUtils {

	private static ObjectMapper objectMapper = new ObjectMapper();

	static {
		// 解析器支持解析单引号
		objectMapper.configure(Feature.ALLOW_SINGLE_QUOTES, true);
		// 解析器支持解析结束符
		objectMapper.configure(Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
		// 忽略JSON字符串多余字段
		objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}

	public static String obj2Str(Object obj) {
		try {
			return objectMapper.writeValueAsString(obj);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static <T> T str2Obj(String str, Class<T> clazz) {
		try {
			return objectMapper.readValue(str, clazz);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> str2Map(String str) {
		try {
			return objectMapper.readValue(str, Map.class);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	// -----------------------------------------------------------------

	/**
	 * 将objectl转JSON字符串,如:{"a":"aa","b":"bb"}
	 * 
	 * @date 2013-3-20,上午11:26:54
	 * @author fyq
	 */
	public static String getJson(Object obj)  {
		try {
			return objectMapper.writeValueAsString(obj);
		} catch (Exception e) {
			return null;
		}
	}

	public static <T> T json2Obj(String str, Class<T> clazz) {
		try {
			return objectMapper.readValue(str, clazz);
		} catch (Exception e) {
			return null;
		}
	}


	/**
	 * 直接取str中JSON值
	 * 
	 * @date 2013-3-20,上午11:27:24
	 * @author fyq
	 */
	public static String getByKey(String key, String str) {
		return "" + getMap(str).get(key);
	}

	/**
	 * str为简单的Map格式,如:{"type":3,"pwd":"a","name":"a"}
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getMap(String str) {
		try {
			return objectMapper.readValue(str, Map.class);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * str为复杂Map格式,由自己确定,如:{"protocol":[{"type":3,"pwd":"a","name":"a"}]}
	 * 
	 * Map<String, List<Map<String, Object>>>
	 */
	@SuppressWarnings("unchecked")
	public static <T> Map<String, T> getMapList(String str) throws Exception {
		try {
			return objectMapper.readValue(str, Map.class);
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * str为List格式
	 * 
	 * 如:[{"type":3,"pwd":"a","name":"a"},{"type":3,"pwd":"b","name":"b"}]
	 */
	@SuppressWarnings("unchecked")
	public static List<LinkedHashMap<String, Object>> getList(String str) throws Exception {
		try {
			return objectMapper.readValue(str, List.class);
		} catch (Exception e) {
			throw e;
		}
	}
	
	public static <T> List<T> str2List(String str, Class<T> clazz) throws Exception {
		try {
			JavaType javaType = objectMapper.getTypeFactory().constructParametricType(List.class, clazz);
			return objectMapper.readValue(str, javaType);
		} catch (Exception e) {
			throw e;
		}
	}

	public static void main(String[] args) throws Exception {
		String tmp = "{\"protocol\":[{\"type\":3,\"pwd\":\"35922BE20930A9E04AAC8B91EB55F90C\",\"cmd\":1,\"name\":\"testName\"}]}";
		Map<String, List<Map<String, Object>>> protocolMap = JsonUtils.getMapList(tmp);
		List<Map<String, Object>> list = protocolMap.get("protocol");
		for (Map<String, Object> map : list) {
			System.out.println(map.get("pwd"));
		}

		Map<String, Object> aa = new HashMap<String, Object>();
		aa.put("a", "aa");
		aa.put("b", "bb");
		System.out.println(getJson(aa));
	}
}

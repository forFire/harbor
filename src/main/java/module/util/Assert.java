package module.util;

public abstract class Assert extends org.springframework.util.Assert {

	public static void equals(Object a, Object b, String message) {
		if (null != a && !a.equals(b))
			throw new IllegalArgumentException(message);
	}

}
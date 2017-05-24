package module.cache.aspect;

/**
 * @author fyq
 */
public class CachePack {
	private String key;
	private Object value;
	private long timeOut;// 过期时间

	public CachePack() {
		super();
	}

	public CachePack(String key, Object value, long timeOut) {
		this.key = key;
		this.value = value;
		this.timeOut = timeOut;
	}

	public String getKey() {
		return key;
	}

	public long getTimeOut() {
		return timeOut;
	}

	public Object getValue() {
		return value;
	}

	public void setKey(String string) {
		key = string;
	}

	public void setTimeOut(long l) {
		timeOut = l;
	}

	public void setValue(Object object) {
		value = object;
	}

}

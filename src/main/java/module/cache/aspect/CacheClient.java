package module.cache.aspect;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author fyq
 */
public class CacheClient extends TimerTask {

	private static final CacheClient cacheClient = new CacheClient(10);
	static {
		Timer timer = new Timer();
		// 1000*60*5min=300000
		timer.schedule(getInstance(), 1000, 300000);
	}

	public static CacheClient getInstance() {
		return cacheClient;
	}

	private CacheClient(final int maxSize) {
		cacheMap = new ConcurrentHashMap<String, CachePack>(maxSize);
		clear();
	}

	private Map<String, CachePack> cacheMap;

	// ************************************

	/**
	 * 访问计数器
	 */
	private long visitedCount;

	/**
	 * 命中计数器
	 */
	private long hitCount;

	public synchronized void clear() {
		cacheMap.clear();
		visitedCount = 0;
		hitCount = 0;
	}

	public long getHitCount() {
		return hitCount;
	}

	public long getVisitedCount() {
		return visitedCount;
	}

	public synchronized void delete(String key) {
		cacheMap.remove(key);
	}

	// ************************************

	public synchronized Object get(String key) {
		if (key == null) {
			return null;
		}
		visitedCount++;
		if (cacheMap.containsKey(key)) {
			CachePack cache = (CachePack) cacheMap.get(key);
			if (!cacheExpired(cache)) {
				hitCount++;
				return cache.getValue();
			} else
				cacheMap.remove(key);
		}
		return null;
	}

	/**
	 * unit second
	 */
	public synchronized void set(String key, int expiration, Object obj) {
		if ((key == null) || (obj == null)) {
			return;
		}
		long tmp = expiration * 1000 + System.currentTimeMillis();
		cacheMap.put(key, new CachePack(key, obj, tmp));
	}

	// ************************************

	@Override
	public void run() {
		for (Map.Entry<String, CachePack> entry : cacheMap.entrySet()) {
			long timeOut = entry.getValue().getTimeOut();
			String key = entry.getKey();
			if (timeOut < System.currentTimeMillis()) {
				cacheMap.remove(key);
			}
		}
	}

	/**
	 * 是否过期
	 */
	private static boolean cacheExpired(CachePack cache) {
		if (null == cache) {
			return false;
		}
		long out = cache.getTimeOut();
		long now = System.currentTimeMillis();
		if (out > now || out <= 0) {
			return false;
		} else {
			return true;
		}
	}

	// ************************************

	public synchronized void add(String key, int expiration, Object value) {
		set(key, expiration, value);
	}
}

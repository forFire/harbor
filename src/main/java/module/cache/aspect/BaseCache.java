package module.cache.aspect;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import module.cache.operate.Delete;
import module.cache.operate.GetAndSet;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.reflect.MethodSignature;

/**
 * 拦截缓存
 * 
 * @author fyq
 * @date 2013-2-1 下午05:30:05
 */
public abstract class BaseCache<T> {
	// @Autowired
	// private MemcachedClient cacheClient;
	private CacheClient cacheClient = CacheClient.getInstance();

	private Class<T> entityClass;

	public abstract void cachedPointcut();

	@SuppressWarnings("all")
	public BaseCache() {
		this.entityClass = null;
		Class c = getClass();
		Type t = c.getGenericSuperclass();
		if (t instanceof ParameterizedType) {
			Type[] p = ((ParameterizedType) t).getActualTypeArguments();
			this.entityClass = (Class<T>) p[0];
		}
	}

	@SuppressWarnings("all")
	@Around("cachedPointcut()")
	public Object doAround(ProceedingJoinPoint pjp) throws Throwable {
		Method method = ((MethodSignature) pjp.getSignature()).getMethod();
		if (method.isAnnotationPresent(GetAndSet.class)) {
			Object arg = pjp.getArgs()[0];// 无参时报错 统一检测
			GetAndSet annotate = method.getAnnotation(GetAndSet.class);
			String key = annotate.prefix() + arg;
			return getAndSet(pjp, annotate, key);
		} else if (method.isAnnotationPresent(Delete.class)) {
			Object arg = pjp.getArgs()[0];
			Delete annotate = method.getAnnotation(Delete.class);
			String key = annotate.prefix() + arg;
			// 更新的时候传的是实体
			if (annotate.entity()) {
				String methodName = annotate.pk();
				Method methodGet = entityClass.getMethod(methodName, null);
				String idValue = (String) methodGet.invoke(arg, null);
				key = annotate.prefix() + idValue;
			}
			cacheClient.delete(key);
			return pjp.proceed();
		} else {
			return pjp.proceed();
		}
	}

	private Object getAndSet(ProceedingJoinPoint pjp, GetAndSet annotate, String key)
			throws Throwable {
		Object result = cacheClient.get(key);
		if (result == null) {
			result = pjp.proceed();
			if (result != null) {
				int expiration = annotate.expiration();
				cacheClient.set(key, expiration, result);
			}
		}
		return result;
	}
}

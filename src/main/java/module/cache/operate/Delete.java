package module.cache.operate;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface Delete {
	String prefix();

	boolean entity() default false;// 默认方法参数不是传实体

	String pk() default "getId";// 如果传实体,默认取主键为getId
}

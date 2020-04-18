package be.nabu.libs.types.api.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Field {
	public String name() default "";
	public String namespace() default "";
	public boolean primary() default false;
	public boolean generated() default false;
	public long min() default 0l;
	public long max() default 0l;
	public long length() default 0l;
	public int minLength() default 0;
	public int maxLength() default 0;
	// the "normal" default is 1, but in java everything is optional by default
	public int minOccurs() default 0;
	public int maxOccurs() default 1;
	public String pattern() default "";
}

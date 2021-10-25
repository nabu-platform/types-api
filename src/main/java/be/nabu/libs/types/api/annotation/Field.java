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
	// the group this field belongs to, this can be used to create panes for example
	public String group() default "";
	public boolean primary() default false;
	public boolean generated() default false;
	// can be used to indicate whether or not this field is environment specific
	public boolean environmentSpecific() default false;
	public long min() default 0l;
	public long max() default 0l;
	public long length() default 0l;
	public int minLength() default 0;
	public int maxLength() default 0;
	// the "normal" default is 1, but in java everything is optional by default
	public int minOccurs() default 0;
	public int maxOccurs() default 1;
	public String pattern() default "";
	public String show() default "";
	public String hide() default "";
	public String comment() default "";
	public String defaultValue() default "";
	public String foreignKey() default "";
}

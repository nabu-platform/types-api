package be.nabu.libs.types.api.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ComplexTypeDescriptor {
	public String collectionName() default "";
	public String name() default "";
	public String namespace() default "";
	public String[] propOrder() default {};
}

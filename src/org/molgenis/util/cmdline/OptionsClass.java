// 

package org.molgenis.util.cmdline;

// jdk
import java.lang.annotation.*;




/**
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface OptionsClass
{
	String name() default "";
	String version() default "0.0.0";
	String description() default "";
};

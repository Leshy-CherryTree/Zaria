/****************************************/
/* ScriptObject.java					*/
/* Created on: 29-May-2014				*/
/* Copyright Cherry Tree Studio 2014	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.scripting.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used for classes that can be loaded in script using the load() function.
 * 
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ScriptObject
{
	public String value();
	public String details() default "";
}

/****************************************/
/* ScriptFunction.java					*/
/* Created on: 01-Jun-2014				*/
/* Copyright Cherry Tree Studio 2014		*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.scripting.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation defining methods that will be accessible to scripts without
 * referencing the parent object. The methods have to be defined as static.
 * 
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ScriptFunction
{
	public String category();
	public String description();
	public String details() default "";
	public String returnValue() default "";
	public String[] parameters();
}

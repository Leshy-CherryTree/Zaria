/****************************************/
/* ExposeToScript.java					*/
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
 * Used for class methods that can be called through script.
 * 
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ScriptMethod
{
	public String description();
	public String details() default "";
	public String returnValue() default "";
	public String[] parameters();
}
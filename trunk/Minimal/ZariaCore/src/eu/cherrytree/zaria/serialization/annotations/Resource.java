/****************************************/
/* Resource.java						*/
/* Created on: 11-May-2013				*/
/* Copyright Cherry Tree Studio 2013	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.serialization.annotations;

import eu.cherrytree.zaria.serialization.ResourceType;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Resource
{
	public ResourceType value();
}

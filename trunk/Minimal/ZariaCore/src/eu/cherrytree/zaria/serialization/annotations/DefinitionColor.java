/****************************************/
/* Color.java							*/
/* Created on: 16-Mar-2013				*/
/* Copyright Cherry Tree Studio 2013	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.serialization.annotations;

import eu.cherrytree.zaria.serialization.ColorName;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface DefinitionColor
{
	public ColorName value();
}

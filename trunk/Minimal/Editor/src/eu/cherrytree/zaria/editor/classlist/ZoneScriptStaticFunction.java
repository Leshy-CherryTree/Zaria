/****************************************/
/* ZoneScriptFunction.java				*/
/* Created on: 06-Jul-2014				*/
/* Copyright Cherry Tree Studio 2014		*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.editor.classlist;

import eu.cherrytree.zaria.scripting.annotations.ScriptFunction;
import java.lang.reflect.Method;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public class ZoneScriptStaticFunction extends ZoneScriptFunction
{
	//--------------------------------------------------------------------------
					
	public ZoneScriptStaticFunction(Method method, ScriptFunction annotation)
	{
		super(method, annotation.category(), annotation.description(), annotation.details(), annotation.returnValue(), annotation.parameters());
	}
	
	//--------------------------------------------------------------------------
}

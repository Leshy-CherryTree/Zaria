/****************************************/
/* ZoneScriptMethod.java					*/
/* Created on: 16-04-2016				*/
/* Copyright Cherry Tree Studio 2016	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.editor.classlist;

import eu.cherrytree.zaria.scripting.annotations.ScriptMethod;
import java.lang.reflect.Method;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public class ZoneScriptMethod extends ZoneScriptFunction
{	
	//--------------------------------------------------------------------------
	
	private Class type;
	
	//--------------------------------------------------------------------------
					
	public ZoneScriptMethod(Method method, Class type, ScriptMethod annotation)
	{
		super(method, type.getSimpleName(), annotation.description(), annotation.details(), annotation.returnValue(), annotation.parameters());
		
		this.type = type;
	}
	
	//--------------------------------------------------------------------------

	public Class getType()
	{
		return type;
	}
	
	//--------------------------------------------------------------------------
}

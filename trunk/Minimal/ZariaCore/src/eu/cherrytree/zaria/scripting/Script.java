/****************************************/
/* ScriptObject.java						*/
/* Created on: 29-May-2014				*/
/* Copyright Cherry Tree Studio 2014		*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.scripting;

import eu.cherrytree.zaria.base.ApplicationRuntimeError;
import eu.cherrytree.zaria.debug.DebugManager;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.EcmaError;
import org.mozilla.javascript.RhinoException;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public class Script
{
	//--------------------------------------------------------------------------
	
	private Context context;
	private Scriptable scope;
	
	private org.mozilla.javascript.Script script;
	
	//--------------------------------------------------------------------------

	Script(Context context, Scriptable scope, String source, String name) throws RhinoException
	{
		this.context = context;
		this.scope = scope;
		
		Context compile_context = Context.getCurrentContext();
		boolean local_compile_context = false;
						
		try
		{			
			if (compile_context == null)
			{
				compile_context = Context.enter();
				local_compile_context = true;
			}
		
			script = compile_context.compileString(source, name, 0, null);
		}
		catch (RhinoException ex)
		{
			ApplicationRuntimeError error = new ApplicationRuntimeError("Compilation of script " + ex.sourceName() + " failed.", ex);
			
			error.addInfo("Details", ex.details());
			error.addInfo("Line", Integer.toString(ex.lineNumber()));
			error.addInfo("Collumn", Integer.toString(ex.columnNumber()));
			error.addInfo("Source", ex.lineSource());
			
			throw error;
		}
		finally
		{
			if (local_compile_context)
				Context.exit();
		}		
	}
	
	//--------------------------------------------------------------------------
	
	public void addObject(Object object, String name)
	{
		assert object != null;
		assert name != null && !name.isEmpty();
		
		ScriptableObject.putProperty(scope, name, Context.javaToJS(object, scope));
	}
	
	//--------------------------------------------------------------------------
	
	public <T> T run() throws EcmaError
	{
		try
		{
			Object ret = script.exec(context, scope);
									
			if (ScriptEngine.ProtectedNativeJavaObject.class.isAssignableFrom(ret.getClass()))			
				return (T) ((ScriptEngine.ProtectedNativeJavaObject) ret).getJavaObject();
			else
				return (T) ret;
		}
		catch(ClassCastException ex)
		{			
			if (DebugManager.isActive())
				DebugManager.alert("Script error!", "Running of script failed.\nReturn value type does not match.\n" +  ex.getMessage());
		}
		catch (RhinoException ex)
		{
			if (DebugManager.isActive())
			{	
				DebugManager.trace(ex);
				
				DebugManager.alert("Script error!", 
						"Running of script " + ex.sourceName() + " failed.\n" + 
						ex.details() +
						"[" + ex.lineNumber() + ";" + ex.columnNumber() + "]: " + ex.lineSource()	);
			}
		}
		
		return null;
	}
	
	//--------------------------------------------------------------------------
}

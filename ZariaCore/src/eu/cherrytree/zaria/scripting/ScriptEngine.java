/****************************************/
/* ScriptEngine.java						*/
/* Created on: 29-May-2014				*/
/* Copyright Cherry Tree Studio 2014	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.scripting;

import eu.cherrytree.zaria.base.ApplicationRuntimeError;
import eu.cherrytree.zaria.console.Console;
import eu.cherrytree.zaria.debug.DebugManager;
import eu.cherrytree.zaria.scripting.annotations.ScriptField;
import eu.cherrytree.zaria.scripting.annotations.ScriptFunction;
import eu.cherrytree.zaria.scripting.annotations.ScriptMethod;
import eu.cherrytree.zaria.scripting.preprocessing.ScriptPreprocessor;
import eu.cherrytree.zaria.scripting.preprocessing.errors.ScriptPreprocessorError;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.Reader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.mozilla.javascript.ClassShutter;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.NativeJavaObject;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.WrapFactory;

/**
 * Wrapper around the Rhino JavaScript engine, that allows execution only of select methods and global functions.
 * 
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public class ScriptEngine
{
	//--------------------------------------------------------------------------
	
	private static ArrayList<String> classes;
	private static HashMap<String, Class> objects;
	private static HashMap<Class, ArrayList<String>> members;
	private static HashMap<Class, String[]> functions;
	
	private static HashMap<String, HashSet<Script>> scripts;
	
	//--------------------------------------------------------------------------
	
	private static class Preprocessor extends ScriptPreprocessor
	{
		private String fixPath(String path)
		{
			if (!path.endsWith(".zonescript"))
				path += ".zonescript";

			path = path.replace('.', '/');
			path = path.replace("/zonescript", ".zonescript");

			if (!path.startsWith("/"))
				path = "/" + path;
			
			return path;
		}
		
		@Override
		protected String getSource(String path)
		{
			try
			{
				path = fixPath(path);

				InputStream inpuStream = ScriptEngine.class.getResourceAsStream(path);

				if (inpuStream == null)	
				{
					DebugManager.trace("Couldn't load script " + path, DebugManager.TraceLevel.ERROR);
					return "";
				}

				char[] buf = new char[2048];

				Reader r = new InputStreamReader(inpuStream, "UTF-8");
				StringBuilder s = new StringBuilder();

				while (true)
				{
					int n = r.read(buf);

					if (n < 0)
						break;
					s.append(buf, 0, n);
				}
				
				return s.toString();
			}
			catch(IOException ex)
			{
				DebugManager.trace(ex);
				return "";
			}
		}

		@Override
		protected boolean scriptExist(String path)
		{
			return ScriptEngine.class.getResourceAsStream(fixPath(path)) != null;
		}
		
		public String getPreprocessedScript(String path)
		{
			return preProcess(getSource(path));
		}
	}
	
	//--------------------------------------------------------------------------
	
	public static final class ScriptEngineInitializationException extends Exception
	{
		public ScriptEngineInitializationException(Throwable cause)
		{
			super("Couldn't initialize the Rhino Script Engine.", cause);
		}		
	}
	
	//--------------------------------------------------------------------------

	private static class CustomClassShutter implements ClassShutter
	{		
		@Override
		public boolean visibleToScripts(String string)
		{			
			return classes.contains(string);
		}
	}
	
	//--------------------------------------------------------------------------
	
	private static class ProtectedContextFactory extends ContextFactory
	{
		private static final ProtectedWrapFactory wrapper = new ProtectedWrapFactory();

		@Override
		protected Context makeContext()
		{
			Context c = super.makeContext();
			c.setWrapFactory(wrapper);

			return c;
		}
	}
	
	//--------------------------------------------------------------------------

	private static class ProtectedWrapFactory extends WrapFactory
	{				
		@Override
		public Scriptable wrapAsJavaObject(Context cx, Scriptable scope, Object javaObject, Class<?> staticType)
		{
			return new ProtectedNativeJavaObject(scope, javaObject, staticType);
		}
	}
	
	//--------------------------------------------------------------------------

	static class ProtectedNativeJavaObject extends NativeJavaObject
	{
		private ArrayList<String> protectedMembers;

		public ProtectedNativeJavaObject(Scriptable scope, Object javaObject, Class staticType)
		{
			super(scope, javaObject, staticType);

			Class cls = javaObject != null ? javaObject.getClass() : staticType;

			protectedMembers = ScriptEngine.members.get(cls);
		}

		@Override
		public boolean has(String name, Scriptable start)
		{
			if (protectedMembers.contains(name))
				return super.has(name, start);
			else
				return false;
		}

		@Override
		public Object get(String name, Scriptable start)
		{
			if (protectedMembers.contains(name))
				return super.get(name, start);
			else
				return NOT_FOUND;
		}		

		public Object getJavaObject()
		{
			return javaObject;
		}				
	}
	
	//--------------------------------------------------------------------------
	
	private static Context context;
	private static Preprocessor preprocessor = new Preprocessor();
	
	//--------------------------------------------------------------------------
	
	@SuppressWarnings("unchecked")
	public static void init() throws ScriptEngineInitializationException
	{			
		try
		{
			try (ObjectInputStream stream = new ObjectInputStream(ScriptEngine.class.getResourceAsStream("/scriptdata/classes.scrdat")))
			{
				classes = (ArrayList<String>) stream.readObject();
			}
			
			try (ObjectInputStream stream = new ObjectInputStream(ScriptEngine.class.getResourceAsStream("/scriptdata/objects.scrdat")))
			{
				objects = (HashMap<String, Class>) stream.readObject();
			}
			
			try (ObjectInputStream stream = new ObjectInputStream(ScriptEngine.class.getResourceAsStream("/scriptdata/members.scrdat")))
			{
				members = (HashMap<Class, ArrayList<String>>) stream.readObject();
			}	
			
			try (ObjectInputStream stream = new ObjectInputStream(ScriptEngine.class.getResourceAsStream("/scriptdata/functions.scrdat")))
			{
				functions = (HashMap<Class, String[]>) stream.readObject();
			}		
		}
		catch (NullPointerException | IOException | ClassNotFoundException ex)
		{
			throw new ScriptEngineInitializationException(ex);
		}
            		
		classes.add("java.lang.Object");
		classes.add("java.lang.String");
		
		ContextFactory.initGlobal(new ProtectedContextFactory());	
		
		context = Context.enter();		
		context.setClassShutter(new CustomClassShutter());		
		
		if (DebugManager.isActive())
			scripts = new HashMap<>();
	}
	
	//--------------------------------------------------------------------------
	
	public static void addScriptClass(Class cls)
	{
		assert classes != null;
		assert !classes.contains(cls.getName());
		assert !members.containsKey(cls);
		assert !functions.containsKey(cls);
		
		ArrayList<String> memeber_array = new ArrayList<>();
		ArrayList<String> function_array = new ArrayList<>();
		
		for (Method method : cls.getDeclaredMethods())
		{
			if (Modifier.isStatic(method.getModifiers()) && method.getAnnotation(ScriptFunction.class) != null)
			{
				memeber_array.add(method.getName());
				function_array.add(method.getName());
				break;
			}
			else if (method.getAnnotation(ScriptMethod.class) != null)
			{
				memeber_array.add(method.getName());
				break;
			}
		}

		for (Field field : cls.getDeclaredFields())
		{
			if (field.getAnnotation(ScriptField.class) != null)
			{
				memeber_array.add(field.getName());
				break;
			}
		}
		
		assert !memeber_array.isEmpty();
		
		classes.add(cls.getName());
		members.put(cls, memeber_array);
		
		if (!function_array.isEmpty())
		{
			String[] funcs = new String[function_array.size()];
			function_array.toArray(funcs);
			
			functions.put(cls, funcs);
		}
	}
	
	//--------------------------------------------------------------------------
	
	public static void addLoadableObjectClass(Class cls)
	{
		assert objects != null;
		assert !objects.containsKey(cls.getSimpleName());
		assert !objects.containsValue(cls);
		
		addScriptClass(cls);
		
		objects.put(cls.getSimpleName(), cls);
	}
	
	//--------------------------------------------------------------------------
	
	@ScriptFunction
	(
		category = "General",
		description = "Creates object of given type.",
		returnValue = "Created object.",
		parameters = {"name", "Name of the type of the object."}
	)
	public static Object create(String name)
	{			
		try
		{
			if (objects.containsKey(name))
				return objects.get(name).newInstance();
		}
		catch (InstantiationException | IllegalAccessException ex)
		{
			throw new ApplicationRuntimeError(ex);
		}

		return null;
	}
	
	//--------------------------------------------------------------------------
	
	public static void destroy()
	{
		if (DebugManager.isActive())
		{
			if (!scripts.isEmpty())
			{
				DebugManager.trace("There are not cleaned up scripts", DebugManager.TraceLevel.ERROR);
				
				for (HashSet<Script> set : scripts.values())
				{
					if (!set.isEmpty())
						DebugManager.trace("Name: " + set.iterator().next().getName() + " Amount: " + set.size(), DebugManager.TraceLevel.ERROR);
				}
			}
		}
		
		Context.exit();
	}
	
	//--------------------------------------------------------------------------
	
	public static <T> T getObject(Object object)
	{
		T ret = null;
		
		if (object instanceof ScriptEngine.ProtectedNativeJavaObject)
		{
			try
			{
				ret = (T) ((ScriptEngine.ProtectedNativeJavaObject) object).getJavaObject();
			}
			catch(ClassCastException ex)
			{
				DebugManager.alert("Script alert", "Invalid object type!\n" + ex.getMessage());
				return null;
			}
		}
		
		return ret;
	}
	
	//--------------------------------------------------------------------------
	
	public static Script createScript(String source, String name)
	{
		ScriptableObject scope = context.initStandardObjects();				
		
		for (Map.Entry<Class, String[]> entry : functions.entrySet())
			scope.defineFunctionProperties(entry.getValue(), entry.getKey(), ScriptableObject.DONTENUM);
		
		return new Script(context, scope, source, name);
	}
	
	//--------------------------------------------------------------------------	
	
	public static Script loadScript(String path, String name)
	{
		Console.printOut("Loading script " + path);
		
		String source = "";
		
		try
		{
			source = preprocessor.getPreprocessedScript(path);
		}
		catch (ScriptPreprocessorError error)
		{
			DebugManager.trace(error);
			DebugManager.alert("Script preprocessing error", DebugManager.getThrowableText("", error));
		}
		
		if (source.isEmpty())
		{
			DebugManager.trace("Couldn't create script " + path +  ", preprocessor returned empty string.", DebugManager.TraceLevel.ERROR);
			return null;
		}
		
		Script script = createScript(source, name);
		
		if (DebugManager.isActive())
		{
			if (!scripts.containsKey(path))
				scripts.put(path, new HashSet<Script>());
		
			scripts.get(path).add(script);
		}
		
		return script;
	}
	
	//--------------------------------------------------------------------------
	
	static void onScriptDestroyed(Script script)
	{
		if (DebugManager.isActive())
		{
			for (Map.Entry<String, HashSet<Script>> entry : scripts.entrySet())
			{
				HashSet<Script> set = entry.getValue();
				
				if (set.contains(script))
				{
					set.remove(script);
					
					if (set.isEmpty())
						scripts.remove(entry.getKey());
					
					break;
				}
			}
		}
	}
	
	//--------------------------------------------------------------------------
	
	public static void updateScripts(String path, String source)
	{
		if (scripts.containsKey(path))
		{
			for (Script script : scripts.get(path))
			{
				try
				{
					source = preprocessor.preProcess(source);
				}
				catch (ScriptPreprocessorError error)
				{
					DebugManager.trace(error);
					DebugManager.alert("Script preprocessing error", DebugManager.getThrowableText("", error));
				}
				
				if (!source.isEmpty())
				{
					script.reinit(source);
				}
				else
				{
					DebugManager.trace("Couldn't update script " + path +  ", preprocessor returned empty string.", DebugManager.TraceLevel.ERROR);
				}
			}
		}
		else
		{
			DebugManager.trace("Trying to update scripts " + path + ", but no created ones found.");
		}
	}
	
	//--------------------------------------------------------------------------
}

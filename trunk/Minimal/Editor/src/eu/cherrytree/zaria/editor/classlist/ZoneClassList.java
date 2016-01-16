/****************************************/
/* ZoneClassList.java					*/
/* Created on: 19-Apr-2014				*/
/* Copyright Cherry Tree Studio 2014	*/
/* Released under EUPL v1.1				*/
/****************************************/
    
package eu.cherrytree.zaria.editor.classlist;

import com.fasterxml.jackson.core.JsonProcessingException;

import eu.cherrytree.zaria.editor.EditorApplication;
import eu.cherrytree.zaria.editor.debug.DebugConsole;
import eu.cherrytree.zaria.editor.serialization.Serializer;
import eu.cherrytree.zaria.scripting.annotations.ScriptFunction;
import eu.cherrytree.zaria.serialization.ZariaObjectDefinition;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;

import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public class ZoneClassList
{
	//--------------------------------------------------------------------------
	
	private static String [] excludes = {"eu.cherrytree.zaria.serialization.ZariaObjectDefinitionLibrary"};
	
	//--------------------------------------------------------------------------
	
	private ArrayList<ZoneClass> classes = new ArrayList<>();
	private ArrayList<ZoneScriptFunction> scriptFunctions = new ArrayList<>();
	
	//--------------------------------------------------------------------------
	
	public ArrayList<ZoneClass> getClasses()
	{
		return classes;
	}
	
	//--------------------------------------------------------------------------
	
	public ArrayList<ZoneScriptFunction> getScriptFunctions()
	{
		return scriptFunctions;
	}
	
	//--------------------------------------------------------------------------
	
	public ArrayList<ZoneClass> getClasses(Class<? extends ZariaObjectDefinition> cls)
	{
		ArrayList<ZoneClass> ret = new ArrayList<>();
					
		for(ZoneClass zcls : classes)
		{
			if(cls.isAssignableFrom(zcls.getObjectClass()))
				ret.add(zcls);
		}
			
		return ret;
	}
	
	//--------------------------------------------------------------------------
	
	public ZoneClassList()
	{
		try
		{
			load();
		}
		catch (IOException | ClassNotFoundException ex)
		{
			DebugConsole.logger.log(Level.SEVERE, null, ex);
		}
	}
				
	//--------------------------------------------------------------------------
	
	public final void load() throws IOException, ClassNotFoundException
	{		
		classes.clear();
		
		loadBaseClasses();
		loadFromJars();	
	}
	
	//--------------------------------------------------------------------------
			
	private void loadBaseClasses()
	{		
		Reflections reflections = new Reflections("eu.cherrytree.zaria");
		Set<Class<? extends ZariaObjectDefinition>> subTypes = reflections.getSubTypesOf(ZariaObjectDefinition.class);
		
		for(Class<? extends ZariaObjectDefinition> c : subTypes)
			addClass(c);
		
		reflections = new Reflections(new ConfigurationBuilder().setUrls(ClasspathHelper.forPackage("eu.cherrytree.zaria")).setScanners(new MethodAnnotationsScanner()));
		
		Set<Method> functions = reflections.getMethodsAnnotatedWith(ScriptFunction.class);
						
		for(Method method : functions)
			scriptFunctions.add(new ZoneScriptFunction(method, method.getAnnotation(ScriptFunction.class)));
	}
	
	//--------------------------------------------------------------------------
	
	private void getClassNames(String jarPath, ArrayList<String> names) throws IOException
	{
		JarFile jarFile = new JarFile(jarPath);
		Enumeration<JarEntry> e = jarFile.entries();
		
		while(e.hasMoreElements())
		{
			JarEntry je = (JarEntry) e.nextElement();
			 
			if(je.isDirectory() || !je.getName().endsWith(".class"))
				continue;
			
			String className = je.getName().substring(0, je.getName().length() - 6);
			names.add(className.replace('/', '.'));
		}
	}
	
	//--------------------------------------------------------------------------
	
	private void loadFromJars() throws MalformedURLException, IOException
	{
		ArrayList<String> classNames = new ArrayList<>();
		
		String [] jar_paths = Serializer.getJarPaths();
	
		for(int i = 0 ; i < jar_paths.length ; i++)
			getClassNames(jar_paths[i], classNames);
		
		ClassLoader jarLoader = Serializer.getJarLoader();
		
		for(String className : classNames)
		{
			try
			{
				Class<?> c = jarLoader.loadClass(className);
												
				if(ZariaObjectDefinition.class.isAssignableFrom(c))
					addClass((Class<? extends ZariaObjectDefinition>) c);
				
				findScriptFunctions(c);
			}
			catch(ClassNotFoundException | NoClassDefFoundError ex)
			{
				DebugConsole.logger.log(Level.SEVERE, null, ex);
			}			
		}		
	}
	
	//--------------------------------------------------------------------------
	
	private void addClass(Class<? extends ZariaObjectDefinition> cls)
	{
		// Check if this class is not in the excludes list.
		for(String excl : excludes)
		{
			if(cls.getCanonicalName().equals(excl))
				return;
		}
		
		// Check if the class can be created.
		int modifiers = cls.getModifiers();
		
		if(Modifier.isAbstract(modifiers) || Modifier.isInterface(modifiers))
			return;
		
		// Load class data.
		try
		{
			classes.add(new ZoneClass(cls));
		}
		catch(ZoneClass.InvalidClassHierarchyException | InstantiationException | IllegalAccessException | JsonProcessingException | NoClassDefFoundError ex)
		{
			EditorApplication.getDebugConsole().addLine("Couldn't load class: " + cls.getCanonicalName());
			DebugConsole.logger.log(Level.SEVERE, null, ex);
		}
	}
	
	//--------------------------------------------------------------------------
	
	private void findScriptFunctions(Class cls)
	{
		for(Method method : cls.getDeclaredMethods())
		{
			int modifiers = method.getModifiers();
			
			if(Modifier.isStatic(modifiers))
			{
				ScriptFunction annotation = method.getAnnotation(ScriptFunction.class);
				
				if(annotation != null)
					scriptFunctions.add(new ZoneScriptFunction(method, annotation));
			}
		}
	}
	
	//--------------------------------------------------------------------------
}

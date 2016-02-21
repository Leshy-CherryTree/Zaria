/****************************************/
/* ScriptClassesAnalyzer.java 			*/
/* Created on: 29-May-2014				*/
/* Copyright Cherry Tree Studio 2014	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.scripting.analyzer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;


/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public class ScriptClassesAnalyzer
{		
	//--------------------------------------------------------------------------
		
	private String srcDir;
	private String tgtDir;
	private String[] jars;
	private String[] classpath;
	
	public static void main (String[] arg)
	{
		if (arg.length != 4)
		{
			System.err.println("Wrong amount of attributes!");
			System.err.println("[srcDir] [tgtDir] [jars] [classpath]");
			return;
		}
		
		ScriptClassesAnalyzer analyzer = new ScriptClassesAnalyzer();
		
		analyzer.setSrcDir(arg[0]);
		analyzer.setTgtDir(arg[1]);
		analyzer.setJars(arg[2]);
		analyzer.setClasspath(arg[3]);
		
		analyzer.execute();
	}

	//--------------------------------------------------------------------------
	
	public void setTgtDir(String tgtDir)
	{
		this.tgtDir = tgtDir;
	}
				
	//--------------------------------------------------------------------------

	public void setSrcDir(String dir)
	{
		this.srcDir = dir;
	}
	
	//--------------------------------------------------------------------------
	
	public void setJars(String jar)
	{
		if (jar.isEmpty())
			this.jars = new String[0];
		else
			this.jars = getPaths(jar);
	}
			
	//--------------------------------------------------------------------------
	
	public void setClasspath(String classpath)
	{
		this.classpath = getPaths(classpath);
	}
	
	//--------------------------------------------------------------------------
	
	private String[] getPaths(String pathString)
	{
		if (System.getProperty("os.name").startsWith("Windows"))
		{
			String[] paths = pathString.split(":");
			ArrayList<String> pathList = new ArrayList<>();
			ArrayList<String> finalPathList = new ArrayList<>();
			
			for (String path : paths)
			{
				String[] split_again = path.split(";");
				
				pathList.addAll(Arrays.asList(split_again));
			}
			
			String disk = "";
			
			for (String path : pathList)
			{
				if (path.length() == 1)
				{
					if (!disk.isEmpty())
						throw new RuntimeException("Can't parse paths!");
										
					disk = path;
				}
				else if (path.startsWith("/") || path.startsWith("\\"))
				{
					if (disk.isEmpty())
						throw new RuntimeException("Can't parse paths!");
					
					finalPathList.add(disk + ":" + path);
					disk = "";
				}
				else
					finalPathList.add(path);
			}
			
			String [] ret = new String[finalPathList.size()];
			finalPathList.toArray(ret);
		
			return ret;
		}
		else
			return pathString.split(":");
	}
	
	//--------------------------------------------------------------------------

	private void getClassesFromDirectory(File file, ArrayList<URL> urls, ArrayList<String> names, String packageName) throws MalformedURLException
	{
		File[] files = file.listFiles();

		if (files  == null)
			return;
		
		for (File f : files)
		{
			if (f.isDirectory())
			{
				getClassesFromDirectory(f, urls, names, packageName + f.getName() + ".");
			}
			else if (f.getName().endsWith(".class") && !f.getName().contains("$"))
			{				
				names.add(packageName + f.getName().substring(0, f.getName().length() - 6));
				urls.add(f.toURI().toURL());
			}
		}
	}
			
	//--------------------------------------------------------------------------			
	
	private void getClassesFromJar(String jarPath, ArrayList<String> names) throws IOException
	{
		JarFile jarFile = new JarFile(jarPath);
		Enumeration<JarEntry> e = jarFile.entries();
		
		while(e.hasMoreElements())
		{
			JarEntry je = (JarEntry) e.nextElement();
			 
			if (je.isDirectory() || !je.getName().endsWith(".class") || je.getName().contains("$"))
				continue;
			
			String className = je.getName().substring(0, je.getName().length() - 6);
			names.add(className.replace('/', '.'));
		}
	}
	
	//--------------------------------------------------------------------------
	
	private void verifyObject(Class cls, String className, ArrayList<String> classes, HashMap<String, Class> objects)
	{
		for (Annotation annotation : cls.getDeclaredAnnotations())
		{
			if (annotation.annotationType().getCanonicalName().equals("eu.cherrytree.zaria.scripting.annotations.ScriptObject"))
			{
				if (cls.getSimpleName().equals(className))
					System.out.println("Found ScriptObject: " + className);
				else
					System.out.println("Found ScriptObject: " + className + " derived from " + cls.getSimpleName());
				
				objects.put(cls.getSimpleName(), cls);
				classes.add(cls.getName());
				
				return;
			}
		}
		
		if (cls.getSuperclass() != null)
			verifyObject(cls.getSuperclass(), className, classes, objects);
	}
	
	//--------------------------------------------------------------------------
	
	private void verifyFunctionsAndMethods(Class cls, String className, ArrayList<String> membersArray, ArrayList<String> functionArray)
	{
		for (Method method : cls.getDeclaredMethods())
		{	
			for (Annotation annotation : method.getDeclaredAnnotations())
			{
				if (Modifier.isStatic(method.getModifiers()) && annotation.annotationType().getCanonicalName().equals("eu.cherrytree.zaria.scripting.annotations.ScriptFunction"))
				{
					System.out.println("Found ScriptFunction: " + method.getName() + "() in class " + className);	
					
					membersArray.add(method.getName());
					functionArray.add(method.getName());
				}
				else if (annotation.annotationType().getCanonicalName().equals("eu.cherrytree.zaria.scripting.annotations.ScriptMethod"))
				{
					if (cls.getSimpleName().equals(className))
						System.out.println("Found ScriptMethod: " + method.getName() + "() in class " + className);
					else
						System.out.println("Found ScriptMethod: " + method.getName() + "() in class " + className + " derived from " + cls.getSimpleName());
					
					membersArray.add(method.getName());										
				}
			}
		}
		
		if (cls.getSuperclass() != null)
			verifyFunctionsAndMethods(cls.getSuperclass(), className, membersArray, functionArray);
	}
	
	//--------------------------------------------------------------------------
	
	private void verifyFields(Class cls, String className, ArrayList<String> membersArray)
	{
		for (Field field : cls.getDeclaredFields())
		{
			for (Annotation annotation : field.getDeclaredAnnotations())
			{
				if (annotation.annotationType().getCanonicalName().equals("eu.cherrytree.zaria.scripting.annotations.ScriptField"))
				{
					if (cls.getSimpleName().equals(className))
						System.out.println("Found ScriptField: " + field.getName() + " in class " + className);
					else
						System.out.println("Found ScriptField: " + field.getName() + " in class " + className + " derived from " + cls.getSimpleName());
					
					membersArray.add(field.getName());
				}
			}
		}
		
		if (cls.getSuperclass() != null)
			verifyFields(cls.getSuperclass(), className, membersArray);
	}
	
	//--------------------------------------------------------------------------
	
	private void verifyClass(Class cls, ArrayList<String> classes, HashMap<String, Class> objects, HashMap<Class, ArrayList<String>> members, HashMap<Class, String[]> functions)
	{					
		if (cls.isInterface() || Modifier.isAbstract(cls.getModifiers()))
			return;
		
		ArrayList<String> members_array = new ArrayList<>();
		ArrayList<String> function_array = new ArrayList<>();
		
		verifyObject(cls, cls.getSimpleName(), classes, objects);						
		verifyFunctionsAndMethods(cls, cls.getSimpleName(), members_array, function_array);
		verifyFields(cls, cls.getSimpleName(), members_array);
		
		if (!function_array.isEmpty())
		{
			String[] funcs = new String[function_array.size()];
			function_array.toArray(funcs);
			functions.put(cls, funcs);
			
			if (!classes.contains(cls.getName()))
				classes.add(cls.getName());
		}
		
		if (!members_array.isEmpty())
		{
			members.put(cls, members_array);
			
			if (!classes.contains(cls.getName()))
				classes.add(cls.getName());
		}
		
		for (Class c : cls.getDeclaredClasses())
			verifyClass(c, classes, objects, members, functions);
	}
	
	//--------------------------------------------------------------------------
	
	private String getPath(String path)
	{
		File f = new File(path);
		
		if (f.isAbsolute())
			return path;
		else
			return f.getAbsolutePath();
	}
	
	//--------------------------------------------------------------------------
	
	private URL getURL(String path) throws MalformedURLException
	{
		File file = new File(path);
		
		if (file.isAbsolute())
			return file.toURI().toURL();
		else
			return file.getAbsoluteFile().toURI().toURL();
	}
	
	//--------------------------------------------------------------------------

	public void execute()
	{
		System.out.println("-----------------SCRIPT ANALYZER-----------------");
		System.out.println("Analyzing java files for script annotations.\n");
		
		ArrayList<String> classNameList = new ArrayList<>();
		ArrayList<URL> file_urls= new ArrayList<>();
		
		ArrayList<String> classes = new ArrayList<>();
		HashMap<String, Class> objects = new HashMap<>();
		HashMap<Class, ArrayList<String>> members = new HashMap<>();
		HashMap<Class, String[]> functions = new HashMap<>();
				
		File file = new File(srcDir);
		
		if (!file.isAbsolute())
			file = file.getAbsoluteFile();
	
		try
		{
			getClassesFromDirectory(file, file_urls, classNameList, "");
		}
		catch (MalformedURLException ex)
		{
			ex.printStackTrace();
			
			System.err.println("-----------------ANALYSIS FAILED-----------------");
			
			return;
		}
		
		try
		{
			for (String jarPath : jars)
				getClassesFromJar(getPath(jarPath), classNameList);
		}
		catch (IOException ex)
		{
			ex.printStackTrace();
			
			System.err.println("-----------------ANALYSIS FAILED-----------------");
			
			return;
		}
		
		URL[] urls;
		
		try
		{
			ArrayList<URL> jar_urls = new ArrayList<>();

			for (String jarPath : jars)
				jar_urls.add(getURL(jarPath));

			for (String jarPath : classpath)
			{
				URL url = getURL(jarPath);

				if (!jar_urls.contains(url))
					jar_urls.add(url);
			}

			urls = new URL[file_urls.size() + jar_urls.size() + 1];
				
			for (int i = 0 ; i < file_urls.size() ; i++)
				urls[i] = file_urls.get(i);
			
			urls[file_urls.size()] = file.toURI().toURL();
			
			for (int i = 0 ; i < jar_urls.size() ; i++)
				urls[file_urls.size() + i + 1] = new URL("jar", "", jar_urls.get(i) + "!/");
		}
		catch (MalformedURLException ex)
		{
			ex.printStackTrace();
			
			System.err.println("-----------------ANALYSIS FAILED-----------------");
			
			return;
		}
		
		try
		{
			ClassLoader cl = new URLClassLoader(urls);

			for (String path : classNameList)
			{
				try
				{
					verifyClass(cl.loadClass(path), classes, objects, members, functions);
				}
				catch(NoClassDefFoundError error)
				{
					System.err.println("Couldn't load " + path + " because of " + error.getMessage());
				}
			}
		}
		catch (ClassNotFoundException ex)
		{
			ex.printStackTrace();
			
			System.err.println("-----------------ANALYSIS FAILED-----------------");
			
			return;
		}

		try
		{
			File directory = new File(tgtDir);				
			directory.mkdirs();
			
			try (ObjectOutputStream stream = new ObjectOutputStream(new FileOutputStream(directory.getAbsolutePath() + "/classes.scrdat")))
			{
				stream.writeObject(classes);
			}
			
			try (ObjectOutputStream stream = new ObjectOutputStream(new FileOutputStream(directory.getAbsolutePath() + "/objects.scrdat")))
			{
				stream.writeObject(objects);
			}
			
			try (ObjectOutputStream stream = new ObjectOutputStream(new FileOutputStream(directory.getAbsolutePath() + "/members.scrdat")))
			{
				stream.writeObject(members);
			}
			
			try (ObjectOutputStream stream = new ObjectOutputStream(new FileOutputStream(directory.getAbsolutePath() + "/functions.scrdat")))
			{
				stream.writeObject(functions);
			}
		}
		catch (IOException ex)
		{
			ex.printStackTrace();
			
			System.err.println("-----------------ANALYSIS FAILED-----------------");
			
			return;
		}

		System.out.println("----------------ANALYSIS FINISHED----------------");
	}
	
	//--------------------------------------------------------------------------
}
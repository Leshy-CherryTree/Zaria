/****************************************/
/* JarCreator.java						*/
/* Created on: 31-May-2014				*/
/* Copyright Cherry Tree Studio 2014	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.editor.scripting;

import eu.cherrytree.zaria.editor.EditorApplication;
import eu.cherrytree.zaria.scripting.preprocessor.ScriptPreprocessor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.jar.Attributes;
import java.util.jar.Attributes.Name;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import org.mozilla.javascript.RhinoException;



/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public class JarCreator
{
	//--------------------------------------------------------------------------
	
	public static class ScriptJarCreationException extends Exception
	{
		private ArrayList<ScriptPreprocessor.ScriptError> errors;

		public ScriptJarCreationException(ArrayList<ScriptPreprocessor.ScriptError> errors)
		{
			this.errors = errors;
		}
		
		public ScriptJarCreationException(ScriptPreprocessor.ScriptError error)
		{
			errors = new ArrayList<>();
			errors.add(error);
		}

		public ArrayList<ScriptPreprocessor.ScriptError> getErrors()
		{
			return errors;
		}
				
		@Override
		public String getMessage()
		{
			StringBuilder errorBuilder = new StringBuilder();
			
			errorBuilder.append("There were errors during jar creation.\n\n");
			
			for(ScriptPreprocessor.ScriptError error : errors)
				errorBuilder.append(error.getMessage()).append("\n");
			
			return errorBuilder.toString();
		}				
	}
	
	//--------------------------------------------------------------------------
	
	public static void createJar(String srcDir, String outJar) throws IOException, ScriptJarCreationException
	{
		Manifest manifest = new Manifest();
		manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");
		manifest.getMainAttributes().put(new Name("Created-By"), "Zone Editor v" + EditorApplication.getVersion());
		
		try (JarOutputStream target = new JarOutputStream(new FileOutputStream(outJar), manifest))
		{
			add(new File(srcDir), target, srcDir);
		}
		catch(ScriptPreprocessor.ScriptProcessorError error)
		{
			throw new ScriptJarCreationException(error);
		}
	}
	
	//--------------------------------------------------------------------------
	
	private static String getPath(String path, String baseDir)
	{
		return path.replace(baseDir, File.separator).replace("\\", "/");
	}
	
	//--------------------------------------------------------------------------
	
	private static void add(File source, JarOutputStream target, String baseDir) throws IOException, ScriptPreprocessor.ScriptError, ScriptJarCreationException
	{		
		if (source.isDirectory())
		{
			String name = getPath(source.getAbsolutePath(), baseDir);
			
			if (!name.isEmpty())
			{
				if (!name.endsWith("/"))
					name += "/";
				
				JarEntry entry = new JarEntry(name);
				entry.setTime(source.lastModified());
				
				target.putNextEntry(entry);
				target.closeEntry();
			}

			ArrayList<ScriptPreprocessor.ScriptError> scriptErrorInfo = new ArrayList<>();
			
			for (File nestedFile : source.listFiles())
			{
				try
				{
					add(nestedFile, target, baseDir);
				}
				catch(ScriptPreprocessor.ScriptError error)					
				{
					scriptErrorInfo.add(error);
				}
				catch(ScriptJarCreationException ex)					
				{
					scriptErrorInfo.addAll(ex.getErrors());
				}
			}
			
			if(!scriptErrorInfo.isEmpty())
				throw new ScriptJarCreationException(scriptErrorInfo);
		}
		else
		{
			JarEntry entry = new JarEntry(getPath(source.getAbsolutePath(), baseDir));
			entry.setTime(source.lastModified());
			target.putNextEntry(entry);

			String script = ScriptPreprocessor.preProcess(source);
			
			try
			{
				ScriptPreprocessor.validateScript(source.getName(), script);
			}
			catch(RhinoException ex)
			{
				throw new ScriptPreprocessor.ScriptValidationError(ex);
			}
			
			target.write(script.getBytes(Charset.forName("UTF-8")));
			
			target.closeEntry();
		}
	}
	
	//--------------------------------------------------------------------------
}

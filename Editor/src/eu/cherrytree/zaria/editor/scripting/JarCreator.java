/****************************************/
/* JarCreator.java						*/
/* Created on: 31-May-2014				*/
/* Copyright Cherry Tree Studio 2014	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.editor.scripting;

import com.google.common.io.Files;

import eu.cherrytree.zaria.editor.EditorApplication;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.jar.Attributes;
import java.util.jar.Attributes.Name;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;



/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public class JarCreator
{
	//--------------------------------------------------------------------------
	
	public static void createJar(String srcDir, String outJar) throws IOException
	{
		Manifest manifest = new Manifest();
		manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");
		manifest.getMainAttributes().put(new Name("Created-By"), "Zone Editor v" + EditorApplication.getVersion());
		
		try (JarOutputStream target = new JarOutputStream(new FileOutputStream(outJar), manifest))
		{
			add(new File(srcDir), target, srcDir);
		}
	}
	
	//--------------------------------------------------------------------------
	
	private static String getPath(String path, String baseDir)
	{
		return path.replace(baseDir, File.separator).replace("\\", "/");
	}

	//--------------------------------------------------------------------------
	
	private static void add(File source, JarOutputStream target, String baseDir) throws IOException
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

			for (File nestedFile : source.listFiles())
			{
				add(nestedFile, target, baseDir);
			}
		}
		else
		{
			JarEntry entry = new JarEntry(getPath(source.getAbsolutePath(), baseDir));
			entry.setTime(source.lastModified());
			target.putNextEntry(entry);

			target.write(Files.toByteArray(source));
			
			target.closeEntry();
		}
	}
	
	//--------------------------------------------------------------------------
}

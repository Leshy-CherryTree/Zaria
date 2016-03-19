/****************************************/
/* ScriptInterface.java					*/
/* Created on: 19-03-2016				*/
/* Copyright Cherry Tree Studio 2016	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.editor;

import eu.cherrytree.zaria.editor.database.DataBase;
import eu.cherrytree.zaria.editor.debug.DebugConsole;
import eu.cherrytree.zaria.editor.document.ZoneDocument;
import eu.cherrytree.zaria.editor.serialization.Serializer;
import eu.cherrytree.zaria.serialization.ZariaObjectDefinition;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;
import java.util.logging.Level;

import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.NativeJavaObject;
import org.mozilla.javascript.Scriptable;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public class ScriptInterface
{
	//--------------------------------------------------------------------------
	
	public static class NativeObject extends NativeJavaObject
	{
		public NativeObject(Scriptable scope, Object javaObject, Class staticType)
		{
			super(scope, javaObject, staticType);
		}

		public Object getJavaObject()
		{
			return javaObject;
		}				
		
		public ZariaObjectDefinition getDefinition()
		{
			if (ZariaObjectDefinition.class.isAssignableFrom(javaObject.getClass()))
				return (ZariaObjectDefinition) javaObject;
			
			return null;
		}
	}		
	
	//--------------------------------------------------------------------------
	
	private static <T> ArrayList<T> getArray(NativeArray array)
	{				
		ArrayList<T> ret = new ArrayList<>();
		
		for (Object object : array.getIds())
		{
			Object obj = array.get((Integer) object, null); 
			
			if (obj instanceof NativeObject)
			{
				NativeObject n_obj = (NativeObject) obj;
				ret.add((T) n_obj.getJavaObject());
			}
			else
			{
				ret.add((T) obj);
			}
		}
				
		return ret;
	}

	//--------------------------------------------------------------------------
	
	public static void trace(String str)
	{
		EditorApplication.getDebugConsole().addLine("Script: " + str);
	}
	
	//--------------------------------------------------------------------------
	
	public static ZariaObjectDefinition[] loadZone(String path)
	{
		if (!path.endsWith(".zone"))
			path += ".zone";
		
		try
		{
			File file = new File(EditorApplication.getAssetsLocation() + path);
			String text = ZoneDocument.loadFileAsString(file);
			
			return Serializer.createDeserializationMapper().readValue(text, ZariaObjectDefinition[].class);
		}
		catch (IOException ex)
		{
			DebugConsole.logger.log(Level.SEVERE, null, ex);
		}
		
		return new ZariaObjectDefinition[]{};
	}
	
	//--------------------------------------------------------------------------
	
	public static void saveZone(NativeArray definitions, String path)
	{
		if (!path.endsWith(".zone"))
			path += ".zone";		
		
		File file = new File(EditorApplication.getAssetsLocation() + path);
		file.getParentFile().mkdirs();
		
		ArrayList<ZariaObjectDefinition> defs;
		
		try
		{							
			defs = getArray(definitions);
		}
		catch (ClassCastException ex)
		{
			DebugConsole.logger.log(Level.SEVERE, null, ex);
			return;
		}		
		
		ZariaObjectDefinition[] def_array = new ZariaObjectDefinition[defs.size()];
		defs.toArray(def_array);
		
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(file)))
		{
			writer.write(ZoneDocument.getNewHeader(file.getName()) + Serializer.getText(def_array));
			DataBase.save(def_array, file.getAbsolutePath());
		}
		catch (IOException ex)
		{
			DebugConsole.logger.log(Level.SEVERE, null, ex);
		}
		
	}
	
	//--------------------------------------------------------------------------
	
	public static ZariaObjectDefinition createNewDefinition(String type)
	{
		try
		{
			Class cls = Class.forName(type);									
			return ReflectionTools.createNewDefinition(cls);
		}
		catch (ClassNotFoundException ex)
		{
		}
		
		try
		{
			Class cls = Serializer.getJarLoader().loadClass(type);
			return ReflectionTools.createNewDefinition(cls);
		}
		catch (ClassNotFoundException ex)
		{			
		}
		
		EditorApplication.getDebugConsole().addLine("Script: Couldn't create object of type " + type);
		
		return null;
	}
	
	//--------------------------------------------------------------------------
	
	public static void setDefinitionID(NativeObject object, String id)
	{
		try
		{
			ZariaObjectDefinition def = object.getDefinition();
			
			if (def != null)
				ReflectionTools.setDefinitionFieldValue(def, "id", id);
			else
				EditorApplication.getDebugConsole().addLine("Script: input object is not a ZariaObjectDefinition.");
		}
		catch (SecurityException | IllegalArgumentException | NoSuchFieldException ex)
		{
			DebugConsole.logger.log(Level.SEVERE, null, ex);
		}
	}
	
	//--------------------------------------------------------------------------
	
	public static String getDefinitionID(NativeObject object)
	{
		ZariaObjectDefinition def = object.getDefinition();
			
		if (def != null)
			return def.getID();
		else
			EditorApplication.getDebugConsole().addLine("Script: input object is not a ZariaObjectDefinition.");
		
		return "";
	}
	
	//--------------------------------------------------------------------------
	
	public static void setString(NativeObject object, String field, String value)
	{
		try
		{
			ZariaObjectDefinition def = object.getDefinition();
			
			if (def != null)
				ReflectionTools.setDefinitionFieldValue(def, field, value);
			else
				EditorApplication.getDebugConsole().addLine("Script: input object is not a ZariaObjectDefinition.");
		}
		catch (SecurityException | IllegalArgumentException | NoSuchFieldException ex)
		{
			DebugConsole.logger.log(Level.SEVERE, null, ex);
		}
	}
	
	//--------------------------------------------------------------------------
	
	public static void setInt(NativeObject object, String field, int value)
	{
		try
		{
			ZariaObjectDefinition def = object.getDefinition();
			
			if (def != null)
				ReflectionTools.setDefinitionFieldValue(def, field, value);
			else
				EditorApplication.getDebugConsole().addLine("Script: input object is not a ZariaObjectDefinition.");
		}
		catch (SecurityException | IllegalArgumentException | NoSuchFieldException ex)
		{
			DebugConsole.logger.log(Level.SEVERE, null, ex);
		}
	}
	
	//--------------------------------------------------------------------------
	
	public static void setFloat(NativeObject object, String field, double value)
	{
		try
		{
			ZariaObjectDefinition def = object.getDefinition();
			
			if (def != null)
				ReflectionTools.setDefinitionFieldValue(def, field, value);
			else
				EditorApplication.getDebugConsole().addLine("Script: input object is not a ZariaObjectDefinition.");
		}
		catch (SecurityException | IllegalArgumentException | NoSuchFieldException ex)
		{
			DebugConsole.logger.log(Level.SEVERE, null, ex);
		}
	}
	
	//--------------------------------------------------------------------------
	
	public static void setBoolean(NativeObject object, String field, boolean value)
	{
		try
		{
			ZariaObjectDefinition def = object.getDefinition();
			
			if (def != null)
				ReflectionTools.setDefinitionFieldValue(def, field, value);
			else
				EditorApplication.getDebugConsole().addLine("Script: input object is not a ZariaObjectDefinition.");
		}
		catch (SecurityException | IllegalArgumentException | NoSuchFieldException ex)
		{
			DebugConsole.logger.log(Level.SEVERE, null, ex);
		}
	}
	
	//--------------------------------------------------------------------------
	
	public static void setLink(NativeObject object, String field, NativeObject value)
	{
		try
		{
			ZariaObjectDefinition valueDef = object.getDefinition();
			
			if (valueDef != null)
			{
				ZariaObjectDefinition def = object.getDefinition();
			
				if (def != null)
					ReflectionTools.setDefinitionFieldValue(def, field, valueDef.getUUID());
				else
					EditorApplication.getDebugConsole().addLine("Script: input object is not a ZariaObjectDefinition.");
			}
			else
			{
				EditorApplication.getDebugConsole().addLine("Script: input object is not a ZariaObjectDefinition.");						
			}
		}
		catch (SecurityException | IllegalArgumentException | NoSuchFieldException ex)
		{
			DebugConsole.logger.log(Level.SEVERE, null, ex);
		}
	}
	
	//--------------------------------------------------------------------------
	
	public static void setLinkArray(NativeObject object, String field, NativeArray value)
	{
		ArrayList<ZariaObjectDefinition> defs;
		
		try
		{							
			defs = getArray(value);
		}
		catch (ClassCastException ex)
		{
			DebugConsole.logger.log(Level.SEVERE, null, ex);
			return;
		}	
		
		UUID[] uuids = new UUID[defs.size()];
		
		for (int i = 0 ; i < uuids.length ; i++)
			uuids[i] = defs.get(i).getUUID();
		
		try
		{
			ZariaObjectDefinition def = object.getDefinition();
			
			if (def != null)
				ReflectionTools.setDefinitionFieldValue(def, field, uuids);
			else
				EditorApplication.getDebugConsole().addLine("Script: input object is not a ZariaObjectDefinition.");
		}
		catch (SecurityException | IllegalArgumentException | NoSuchFieldException ex)
		{
			DebugConsole.logger.log(Level.SEVERE, null, ex);
		}
	}
	
	//--------------------------------------------------------------------------
}

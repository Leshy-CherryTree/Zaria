/****************************************/
/* Serializer.java						*/
/* Created on: 01-Dec-2013				*/
/* Copyright Cherry Tree Studio 2013		*/
/* Released under EUPL v1.1				*/
/****************************************/


package eu.cherrytree.zaria.editor.serialization;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import eu.cherrytree.zaria.editor.debug.DebugConsole;
import java.io.IOException;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.logging.Level;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public class Serializer
{	
	//--------------------------------------------------------------------------
		
	private static String [] jarPaths;
	
	private static ClassLoader jarLoader;
	
	//--------------------------------------------------------------------------

	public static void setJarPaths(String[] jarPaths)
	{
		Serializer.jarPaths = jarPaths;
	}
				
	//--------------------------------------------------------------------------
	
	public static String[] getJarPaths()
	{
		return jarPaths;
	}
	
	//--------------------------------------------------------------------------
	
	public static ClassLoader getJarLoader()
	{
		return jarLoader;
	}
	
	//--------------------------------------------------------------------------
	
	public static void init() throws MalformedURLException
	{
		URL [] urls = new URL[jarPaths.length];
		
		for(int i = 0 ; i < jarPaths.length ; i++)
			urls[i] = new URL("jar:file:" + jarPaths[i] + "!/");
		
		jarLoader = URLClassLoader.newInstance(urls);
	}
	
	//--------------------------------------------------------------------------
	
	private static ObjectMapper createMapper()
	{
		ObjectMapper mapper = new ObjectMapper();
		
		mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
		mapper.setVisibility(PropertyAccessor.GETTER, JsonAutoDetect.Visibility.NONE);
		mapper.setVisibility(PropertyAccessor.IS_GETTER, JsonAutoDetect.Visibility.NONE);
		mapper.setVisibility(PropertyAccessor.SETTER, JsonAutoDetect.Visibility.NONE);
		mapper.setVisibility(PropertyAccessor.CREATOR, JsonAutoDetect.Visibility.NONE);
		
		mapper.configure(MapperFeature.AUTO_DETECT_GETTERS, false);
		mapper.configure(MapperFeature.AUTO_DETECT_CREATORS, false);
		mapper.configure(MapperFeature.AUTO_DETECT_SETTERS, false);
		mapper.configure(MapperFeature.AUTO_DETECT_IS_GETTERS, false);
		mapper.configure(MapperFeature.AUTO_DETECT_IS_GETTERS, false);
		mapper.configure(MapperFeature.USE_GETTERS_AS_SETTERS, false);
		
		return mapper;
	}
	
	//--------------------------------------------------------------------------
	
	public static ObjectMapper createSerializationMapper()
	{
		ObjectMapper mapper = createMapper();
		
		mapper.configure(JsonGenerator.Feature.QUOTE_FIELD_NAMES, false);	
			
		return mapper;
	}
	
	//--------------------------------------------------------------------------
						
	public static ObjectMapper createDeserializationMapper()
	{
		try
		{
			if(Thread.currentThread().getContextClassLoader() != jarLoader)
				Thread.currentThread().setContextClassLoader(jarLoader);
			
			ObjectMapper mapper = createMapper();
			
			mapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
			mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
								
			mapper.configure(DeserializationFeature.WRAP_EXCEPTIONS, true);	
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			
			return mapper;
		}
		catch (Exception ex)
		{
			DebugConsole.logger.log(Level.SEVERE, null, ex);
		}
		
		return null;
	}
	
	//--------------------------------------------------------------------------
	
	public static String getText(Object obj) throws JsonProcessingException
	{				
		return setIndentation(createSerializationMapper().writeValueAsString(obj));
	}
	
	//--------------------------------------------------------------------------
	
	public static <Type> Type copy(Type object) throws IOException
	{
		return (Type) createDeserializationMapper().readValue(createSerializationMapper().writeValueAsString(object), object.getClass());
	}
	
	//--------------------------------------------------------------------------
				
	private static String setIndentation(String text)
	{
		String[] split = text.split("\"");
		text = "";
		
		for(int i = 0 ; i < split.length ; i++)
		{
			if(i != 0)
				text += "\"";
			
			if(i % 2 == 0)
			{
				text +=	split[i].replace(",", ",\n")
								.replace(":", " : ")
								.replace("{", "\n{\n")
								.replace("}", "\n}\n")
								.replace("[", "\n[\n")
								.replace("]", "\n]\n")
								.replace("\n]\n,\n", "\n],\n")
								.replace("\n}\n,\n", "\n},\n");
			}
			else
				text += split[i];
		}
		
		String indent = "";		
		String ret = "";
		boolean eol = false;
		
		for(int i = 0 ; i < text.length() ; i++)
		{						
			char c = text.charAt(i);
			
			if(c == '{' || c == '[')
				indent += "\t";
			else if(c == '}'  || c == ']')
				indent = indent.substring(0, indent.length()-1);
			
			if(eol)
			{			
				if(!indent.isEmpty())
				{
					if(c == '{' || c == '[')
						ret += indent.substring(0, indent.length()-1);
					else
						ret += indent;
				}
				eol = false;
			}
			
			if(c == '\n')
				eol = true;
			
			ret += c;
		}

		return ret.trim() + "\n";
	}
	
	//--------------------------------------------------------------------------
}

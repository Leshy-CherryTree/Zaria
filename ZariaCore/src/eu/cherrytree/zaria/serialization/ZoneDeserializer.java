/****************************************/
/* ZoneDeserializer.java					*/
/* Created on: 10-01-2016				*/
/* Copyright Cherry Tree Studio 2016	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.serialization;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;

import java.util.ArrayList;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public class ZoneDeserializer
{
	
	//--------------------------------------------------------------------------
	
	public static ZariaObjectDefinition[] loadDefinitions(String path) throws IOException, ValidationException
	{
		if (!path.startsWith("/"))
			path = "/" + path;
		
		return loadDefinitions(ZoneDeserializer.class.getResourceAsStream(path));
	}
	
	//--------------------------------------------------------------------------
	
	public static ZariaObjectDefinitionLibrary loadLibrary(String path) throws IOException, ValidationException
	{
		if (!path.startsWith("/"))
			path = "/" + path;
			
		return loadLibrary(ZoneDeserializer.class.getResourceAsStream(path));
	}
	
    //--------------------------------------------------------------------------

    public static ZariaObjectDefinition[] loadDefinitions(InputStream inputStream) throws IOException, ValidationException
    {
		assert inputStream != null;
		
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
		mapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
		mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
		
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

		ZariaObjectDefinition[] data = mapper.readValue(inputStream, ZariaObjectDefinition[].class);
		
		ArrayList<DefinitionValidation> errors = new ArrayList<>();
		
		for(ZariaObjectDefinition def : data)
		{
			DefinitionValidation val = def.validate();
			
			if(val.hasErrors())
				errors.add(val);
		}
		
		if(!errors.isEmpty())
			throw new ValidationException("Couldn't validate definition file.", errors);

		return data;
    }

	//--------------------------------------------------------------------------

	public static ZariaObjectDefinitionLibrary loadLibrary(InputStream inputStream) throws IOException, ValidationException
	{
		assert inputStream != null;
		
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
		mapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
		mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
		
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

		ZariaObjectDefinitionLibrary library = mapper.readValue(inputStream, ZariaObjectDefinitionLibrary.class);
				
		DefinitionValidation val = library.validate();		
		
		if(val.hasErrors())
		{
			if(!val.getSubValidations().isEmpty() && val.getExceptions().isEmpty())
			{
				throw new ValidationException("Couldn't validate library " + library.getID(), val.getSubValidations());
			}
			else
			{
				ArrayList<DefinitionValidation> array = new ArrayList<>();
				array.add(val);
				
				throw new ValidationException("Couldn't validate "  + library.getID(), array);
			}
		}
		
		return library;
	}

	//--------------------------------------------------------------------------
}

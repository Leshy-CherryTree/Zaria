/****************************************/
/* ZoneProperty.java					*/
/* Created on: 17-Oct-2013				*/
/* Copyright Cherry Tree Studio 2013	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.editor.properties;

import eu.cherrytree.zaria.editor.debug.DebugConsole;
import eu.cherrytree.zaria.editor.document.ZoneDocument;
import eu.cherrytree.zaria.editor.document.ZoneMetadata;
import eu.cherrytree.zaria.serialization.ZariaObjectDefinition;
import java.lang.reflect.Field;
import java.util.logging.Level;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public class ZoneMetadataProperty extends ZoneProperty
{
	//--------------------------------------------------------------------------
	
	private static final long serialVersionUID = 1L;
	
	//--------------------------------------------------------------------------	

	public ZoneMetadataProperty(String name, Class type, String description, ZoneDocument.DocumentType documentType, boolean editable)
	{		
		super(name, name, type, description, null, documentType, null, null, false, editable, null);	
	}
	
	//--------------------------------------------------------------------------	
	
	@Override
	public String getCategory()
	{
		return "Editor Properties";
	}
	
	//--------------------------------------------------------------------------	
	
	@Override
	public void readFromObject(Object object)
	{
		ZoneMetadata metadata = ZoneDocument.getMetadata((ZariaObjectDefinition) object);
	
		try
		{
			Field field = metadata.getClass().getDeclaredField(name);
			field.setAccessible(true);
			initializeValue(field.get(metadata));
		}
		catch(NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException ex)
		{
			DebugConsole.logger.log(Level.SEVERE, null, ex);
		}
	}
	
	//--------------------------------------------------------------------------	
	
	@Override
	public void writeToObject(Object object)
	{
		ZoneMetadata metadata = ZoneDocument.getMetadata((ZariaObjectDefinition) object);
		
		try
		{
			Field field = metadata.getClass().getDeclaredField(name);
			field.setAccessible(true);						
			field.set(metadata, getValue());			
		}
		catch(NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException ex)
		{
			DebugConsole.logger.log(Level.SEVERE, null, ex);
		}
		
		ZoneDocument.saveMetadata((ZariaObjectDefinition) object, metadata);
	}
	
	//--------------------------------------------------------------------------	
}

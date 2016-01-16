/****************************************/
/* ZoneClass.java						*/
/* Created on: 09-May-2013				*/
/* Copyright Cherry Tree Studio 2013	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.editor.classlist;

import com.fasterxml.jackson.core.JsonProcessingException;

import eu.cherrytree.zaria.editor.EditorApplication;
import eu.cherrytree.zaria.editor.datamodels.palette.IconMaker;
import eu.cherrytree.zaria.editor.serialization.Serializer;

import eu.cherrytree.zaria.serialization.ColorName;
import eu.cherrytree.zaria.serialization.ZariaObjectDefinition;
import eu.cherrytree.zaria.serialization.annotations.DefinitionAffilation;
import eu.cherrytree.zaria.serialization.annotations.DefinitionAffilationType;

import eu.cherrytree.zaria.serialization.annotations.DefinitionCategory;
import eu.cherrytree.zaria.serialization.annotations.DefinitionColor;
import eu.cherrytree.zaria.serialization.annotations.DefinitionDescription;

import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public class ZoneClass
{		
	//--------------------------------------------------------------------------
	
	public static class InvalidClassHierarchyException extends Exception
	{
		public InvalidClassHierarchyException(Class c)
		{
			super("Class " + c.getCanonicalName() + " has invalid hierarchy");					
		}		
	}
	
	//--------------------------------------------------------------------------

	private String category;
	private String name;
	private String toolTip;
	
	private ImageIcon icon;
	
	private Class<? extends ZariaObjectDefinition> objectClass;
	
	//--------------------------------------------------------------------------

	public ZoneClass(Class<? extends ZariaObjectDefinition> c) throws InvalidClassHierarchyException, InstantiationException, IllegalAccessException, JsonProcessingException
	{
		objectClass = c;
		
		category = getCategory(c);
		icon = IconMaker.getIcon(getColor(c));
		
		name = c.getSimpleName();
		
		toolTip = getToolTip(c);
		
		checkDeprecated(c);

		if(EditorApplication.getDebugConsole() != null)
			EditorApplication.getDebugConsole().addLine("Loaded " + c.getCanonicalName() + " of category " + category);
	}
	
	//--------------------------------------------------------------------------

	public Class<? extends ZariaObjectDefinition> getObjectClass()
	{
		return objectClass;
	}
		
	//--------------------------------------------------------------------------

	public static String getCategory(Class c) throws InvalidClassHierarchyException
	{
		DefinitionCategory cat = ((DefinitionCategory) c.getAnnotation(DefinitionCategory.class));

		if(cat == null)
		{
			if(c.getSuperclass() == null)
				throw new InvalidClassHierarchyException(c);

			return getCategory(c.getSuperclass());
		}
		else
			return cat.value();
	}

	//--------------------------------------------------------------------------
	
	public static ColorName getColor(Class c) throws InvalidClassHierarchyException
	{
		DefinitionColor cat = ((DefinitionColor) c.getAnnotation(DefinitionColor.class));

		if(cat == null)
		{
			if(c.getSuperclass() == null)
				throw new InvalidClassHierarchyException(c);

			return getColor(c.getSuperclass());
		}
		else
			return cat.value();
	}
	
	//--------------------------------------------------------------------------
	
	public static String getToolTip(Class c)
	{
		DefinitionDescription description = ((DefinitionDescription) c.getAnnotation(DefinitionDescription.class));
		
		if(description != null)
			return description.value();
		else
			return null;
	}

	//--------------------------------------------------------------------------
	
	public static boolean getIsUsableInEditor(Class c)
	{
		DefinitionAffilation affilation = ((DefinitionAffilation) c.getAnnotation(DefinitionAffilation.class));
		
		if(affilation != null)
			return affilation.value() == DefinitionAffilationType.WORK_FILES || affilation.value() == DefinitionAffilationType.ASSETS_AND_WORK_FILES;
		else if(c.getSuperclass() != null)
			return getIsUsableInEditor(c.getSuperclass());
		else
			throw new RuntimeException("Class has not DefinitionAffilation annotation anywhere in it's hierarchy.");
	}
	
	//--------------------------------------------------------------------------
	
	public static boolean getIsUsableInLibrary(Class c)
	{
		DefinitionAffilation affilation = ((DefinitionAffilation) c.getAnnotation(DefinitionAffilation.class));

		if(affilation != null)
			return affilation.value() == DefinitionAffilationType.ASSETS || affilation.value() == DefinitionAffilationType.ASSETS_AND_WORK_FILES;
		else if(c.getSuperclass() != null)
			return getIsUsableInLibrary(c.getSuperclass());
		else
			throw new RuntimeException("Class has not DefinitionAffilation annotation anywhere in it's hierarchy.");
	}
	
	//--------------------------------------------------------------------------
	
	private void checkDeprecated(Class c)
	{
		Deprecated dep = (Deprecated) c.getAnnotation(Deprecated.class);
		
		if(dep != null)
			category = "Deprecated - " + category;
	}

	//--------------------------------------------------------------------------
	
	public String getName()
	{
		return name;
	}
	
	//--------------------------------------------------------------------------

	public String getCategory()
	{
		return category;
	}
	
	//--------------------------------------------------------------------------

	public Icon getIcon()
	{
		return icon;
	}		
	
	//--------------------------------------------------------------------------

	public String getToolTip()
	{
		return toolTip;
	}
	
	//--------------------------------------------------------------------------
}
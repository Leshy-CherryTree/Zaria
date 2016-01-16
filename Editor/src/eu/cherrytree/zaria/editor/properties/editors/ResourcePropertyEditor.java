/****************************************/
/* ResourcePropertyEditor.java          */
/* Created on: 21-Jul-2013				*/
/* Copyright Cherry Tree Studio 2013	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.editor.properties.editors;

import eu.cherrytree.zaria.editor.EditorApplication;
import eu.cherrytree.zaria.serialization.ResourceType;



/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public class ResourcePropertyEditor extends FilePropertyEditor
{
	//--------------------------------------------------------------------------
	
	private ResourceType resourceType;
	
	//--------------------------------------------------------------------------

	public ResourcePropertyEditor(ResourceType resourceType)
	{
		super(EditorApplication.getAssetsLocation());
		
		this.resourceType = resourceType;
	}

	//--------------------------------------------------------------------------

	@Override
	public String getFileTypeString()
	{
		return resourceType.getSimpleName();
	}
	
	//--------------------------------------------------------------------------

	@Override
	public String[] getFileTypeExtensions()
	{
		return resourceType.getExtensions();
	}
	
	//--------------------------------------------------------------------------
}

/****************************************/
/* ResourcePropertyInfo.java				*/
/* Created on: 13-02-2016				*/
/* Copyright Cherry Tree Studio 2016	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.editor.properties;

import eu.cherrytree.zaria.serialization.ResourceType;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public class ResourcePropertyInfo
{
	//--------------------------------------------------------------------------
	
	private String name;
	private String[] extensions;

	//--------------------------------------------------------------------------
	
	public ResourcePropertyInfo(ResourceType type)
	{
		name = type.getSimpleName();
		extensions = type.getExtensions();
	}

	//--------------------------------------------------------------------------
	
	public ResourcePropertyInfo(String name, String[] extensions)
	{
		this.name = name;
		this.extensions = extensions;
	}
	
	//--------------------------------------------------------------------------

	public String[] getExtensions()
	{
		return extensions;
	}
	
	//--------------------------------------------------------------------------

	public String getName()
	{
		return name;
	}
	
	//--------------------------------------------------------------------------
}

/****************************************/
/* ObjectDefinitiontType.java 			*/
/* Created on: 27-Apr-2014				*/
/* Copyright Cherry Tree Studio 2014	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.editor.datamodels.definitions;

import eu.cherrytree.zaria.editor.classlist.ZoneClass;
import java.util.ArrayList;
import javax.swing.Icon;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public class ObjectDefinitiontType implements Comparable<ObjectDefinitiontType>
{
	//--------------------------------------------------------------------------
	
	private ZoneClass zoneClass;
	
	private ArrayList<ObjectDefinitionWrapper> definitions = new ArrayList<>();

	//--------------------------------------------------------------------------
	
	public ObjectDefinitiontType(ZoneClass zoneClass)
	{
		this.zoneClass = zoneClass;
	}
			
	//--------------------------------------------------------------------------
	
	public ArrayList<ObjectDefinitionWrapper> getDefinitions()
	{
		return definitions;
	}
	
	//--------------------------------------------------------------------------

	@Override
	public int compareTo(ObjectDefinitiontType type)
	{
		return zoneClass.getName().compareTo(type.zoneClass.getName());
	}

	//--------------------------------------------------------------------------
	
	@Override
	public String toString()
	{
		return zoneClass.getName();
	}
	
	//--------------------------------------------------------------------------
	
	public Icon getIcon()
	{
		return zoneClass.getIcon();
	}
	
	//--------------------------------------------------------------------------
}

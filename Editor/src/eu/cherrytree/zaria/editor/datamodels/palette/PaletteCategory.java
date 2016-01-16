/****************************************/
/* PaletteCategory.java                */
/* Created on: 06-May-2013				*/
/* Copyright Cherry Tree Studio 2013	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.editor.datamodels.palette;

import java.util.ArrayList;
import java.util.Collections;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public class PaletteCategory extends PaletteElement<PaletteCategory> implements PaletteBranch
{
	//--------------------------------------------------------------------------
	
	private ArrayList<PaletteElement> objects =  new ArrayList<>();
	
	//--------------------------------------------------------------------------

	public PaletteCategory(String name)
	{
		super(name);
	}		
	
	//--------------------------------------------------------------------------

	public void addObject(PaletteElement obj)
	{	
		if(!objects.contains(obj))
			objects.add(obj);
		
		Collections.sort(objects);
	}
	
	//--------------------------------------------------------------------------

	public ArrayList<PaletteElement> getObjects()
	{
		return objects;
	}
	
	//--------------------------------------------------------------------------

	@Override
	public Object getChild(int index)
	{
		return objects.get(index);
	}

	//--------------------------------------------------------------------------

	@Override
	public int getChildCount()
	{
		return objects.size();
	}
	
	//--------------------------------------------------------------------------
	
	@Override
	public int getIndexOfChild(Object object)
	{
		return objects.indexOf(object);
	}
	
	//--------------------------------------------------------------------------

	@Override
	public int compareTo(PaletteCategory obj)
	{
		if((getName().startsWith("Deprecated - ") && obj.getName().startsWith("Deprecated - ")) || (!getName().startsWith("Deprecated - ") && !obj.getName().startsWith("Deprecated - ")))
			return getName().compareTo(obj.getName());
		else
			return getName().startsWith("Deprecated - ") && !obj.getName().startsWith("Deprecated - ") ? 1 : -1;
	}
	
	//--------------------------------------------------------------------------
	
	@Override
	public boolean isLeaf()
	{
		return false;
	}
	
	//--------------------------------------------------------------------------
}

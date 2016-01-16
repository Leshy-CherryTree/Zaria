/****************************************/
/* PaletteRoot.java						*/
/* Created on: 09-May-2013				*/
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
public class PaletteRoot extends PaletteElement<PaletteRoot> implements PaletteBranch
{
	//--------------------------------------------------------------------------
	
	private ArrayList<PaletteCategory> categories =  new ArrayList<>();
	
	//--------------------------------------------------------------------------
	
	public PaletteRoot()
	{
		super("Palette");
	}
	
	//--------------------------------------------------------------------------

	public ArrayList<PaletteCategory> getCategories()
	{
		return categories;
	}
	
	//--------------------------------------------------------------------------
	
	public PaletteCategory getCategory(String name)
	{		
		for(PaletteCategory category : categories)
		{
			if(category.getName().equals(name))
				return category;
		}
		
		PaletteCategory category = new PaletteCategory(name);
		categories.add(category);
		
		Collections.sort(categories);
		
		return category;		
	}
	
	//--------------------------------------------------------------------------

	@Override
	public Object getChild(int index)
	{
		return categories.get(index);
	}

	//--------------------------------------------------------------------------

	@Override
	public int getChildCount()
	{	
		return categories.size();
	}
	
	//--------------------------------------------------------------------------

	@Override
	public int getIndexOfChild(Object object)
	{
		return categories.indexOf(object);
	}
	
	//--------------------------------------------------------------------------

	@Override
	public int compareTo(PaletteRoot root)
	{
		if(this.categories.size() != root.categories.size())
			return this.categories.size() > root.categories.size() ? -1 : 1;
		
		for(int i = 0 ; i < this.categories.size() ; i++)
		{
			int comp = this.categories.get(i).compareTo(root.categories.get(i));
			
			if(comp != 0)
				return comp;
		}
		
		return 0;
	}
	
	//--------------------------------------------------------------------------
	
	@Override
	public boolean isLeaf()
	{
		return false;
	}
	
	//--------------------------------------------------------------------------
}

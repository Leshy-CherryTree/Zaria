/****************************************/
/* PlacableCategory.java				*/
/* Created on: 19-Oct-2013				*/
/* Copyright Cherry Tree Studio 2013	*/
/* Released under EUPL v1.1				*/
/****************************************/


package eu.cherrytree.zaria.editor.datamodels;

import eu.cherrytree.zaria.editor.EditorApplication;

import java.util.ArrayList;
import java.util.Collections;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public class PlacableCategory implements Comparable<PlacableCategory>
{
	//--------------------------------------------------------------------------
	
	private String name;
	private String path;
	private String filter = "";
	
	private ArrayList<PlacableTreeElement> elements = new ArrayList<>();
	private ArrayList<PlacableTreeElement> visibleElements = new ArrayList<>();
	private ArrayList<PlacableCategory> categories = new ArrayList<>();
	private ArrayList<PlacableCategory> visibleCategories = new ArrayList<>();
	
	//--------------------------------------------------------------------------
	
	public PlacableCategory(String name, String path)
	{
		this.name = name;
		this.path = path;
	}
			
	//--------------------------------------------------------------------------
	
	public void addElement(PlacableTreeElement element)
	{
		String elem_cat = element.getCategory();
		
		if(elem_cat.equals(path))
		{
			elements.add(element);
			EditorApplication.getDebugConsole().addLine("Adding map object definition " + element.toString() + " at " + path);
		}
		else
		{
			boolean found = false;
			
			// Try to add the object to existing categories.
			for(PlacableCategory category : categories)
			{
				if(elem_cat.startsWith(category.path))
				{
					category.addElement(element);
					found = true;
					break;
				}
			}
			
			// If no existing categories fit, create a new one.
			if(!found)
			{
				String cat_name = elem_cat;
				
				if(!path.isEmpty())
					cat_name = cat_name.substring(cat_name.indexOf(path) + path.length()+1);
				
				String [] cats = cat_name.split("\\.");
				String cat_path = (path.isEmpty() ? "" : path + ".") + cats[0];
				
				PlacableCategory category = new PlacableCategory(cats[0], cat_path);
				category.addElement(element);
				categories.add(category);
			}
		}
		
		updateFilter();
	}
	
	//--------------------------------------------------------------------------

	public Object getChild(int index)
	{
		if(index >= visibleCategories.size())
			return visibleElements.get(index - visibleCategories.size());
		else
			return visibleCategories.get(index);
	}
	
	//--------------------------------------------------------------------------

	public int getChildCount()
	{
		return visibleCategories.size() + visibleElements.size();
	}
	
	//--------------------------------------------------------------------------

	public int getIndexOfChild(Object child)
	{
		if(PlacableTreeElement.class.isAssignableFrom(child.getClass()))
			return visibleCategories.size() + visibleElements.indexOf(child);
		else if(PlacableCategory.class.isAssignableFrom(child.getClass()))
			return visibleCategories.indexOf(child);
		else
			return -1;
	}
	
	//--------------------------------------------------------------------------

	public void sort()
	{
		Collections.sort(elements);
		Collections.sort(categories);
		
		for(PlacableCategory category : categories)
			category.sort();
		
		updateFilter();
	}

	//--------------------------------------------------------------------------
	
	private void updateFilter()
	{
		visibleCategories.clear();
		visibleElements.clear();
		
		for(PlacableCategory category : categories)
		{
			category.updateFilter();
			
			if(category.getChildCount() > 0)
				visibleCategories.add(category);
		}
		
		for(PlacableTreeElement element : elements)
		{
			assert element != null;
			assert filter != null;
			assert element.toString() != null;
			assert filter.toLowerCase() != null;

			if(element.toString().toLowerCase().contains(filter.toLowerCase()))
				visibleElements.add(element);			
		}
	}
	
	//--------------------------------------------------------------------------

	@Override
	public String toString()
	{
		return name;
	}

	//--------------------------------------------------------------------------	
	
	public String getPath()
	{
		return path;
	}
	
	//--------------------------------------------------------------------------
	
	@Override
	public int compareTo(PlacableCategory t)
	{
		return name.compareTo(t.toString());
	}
	
	//--------------------------------------------------------------------------
	
	public boolean remove(String id)
	{				
		for(PlacableTreeElement element : elements)
		{
			if(element.toString().equals(id))
			{
				elements.remove(element);
				updateFilter();
				return true;
			}
		}
		
		for(PlacableCategory category : categories)
		{
			if(category.remove(id))
			{
				if(category.getChildCount() == 0)
					categories.remove(category);
				
				
				updateFilter();
				return true;
			}
		}
		
		return false;
	}
	
	//--------------------------------------------------------------------------

	public void setFilter(String filter)
	{	
		this.filter = filter;
		
		for(PlacableCategory category : categories)
			category.setFilter(filter);
		
		updateFilter();
	}
	
	//--------------------------------------------------------------------------
}

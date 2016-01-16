/****************************************/
/* HelpTopicCategory.java				*/
/* Created on: 07-Jul-2014				*/
/* Copyright Cherry Tree Studio 2014	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.editor.datamodels.help;

import java.util.ArrayList;
import java.util.Collections;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public class HelpTopicCategory implements Comparable<HelpTopicCategory>
{
	//--------------------------------------------------------------------------
	
	private String name;
	private ArrayList<HelpTopicCategory> categories = new ArrayList<>();
	private ArrayList<HelpTopic> topics = new ArrayList<>();
	
	//--------------------------------------------------------------------------
	
	public HelpTopicCategory(String name)
	{
		this.name = name;
	}
		
	//--------------------------------------------------------------------------
	
	public HelpTopicCategory getCategory(String name)
	{		
		name = Character.toUpperCase(name.charAt(0)) + name.substring(1);
		
		for(HelpTopicCategory category : categories)
		{
			if(category.name.equals(name))
				return category;
		}
		
		HelpTopicCategory category = new HelpTopicCategory(name);
		categories.add(category);
		
		Collections.sort(categories);
		
		return category;
	}
	
	//--------------------------------------------------------------------------
	
	public void addTopic(HelpTopic topic)
	{
		topics.add(topic);
		
		Collections.sort(topics);
	}
	
	//--------------------------------------------------------------------------

	public Object getChild(int index)
	{
		if(categories.size() > index)
			return categories.get(index);
		else
			return topics.get(index - categories.size());
	}
	
	//--------------------------------------------------------------------------

	public int getChildCount()
	{
		return categories.size() + topics.size();
	}
	
	//--------------------------------------------------------------------------

	public int getIndex(Object child)
	{
		if(categories.contains(child))
			return categories.indexOf(child);
		else if(topics.contains(child))
			return categories.size() + topics.indexOf(child);
		else
			return -1;
	}
			
	//--------------------------------------------------------------------------

	@Override
	public String toString()
	{
		return name;
	}
			
	//--------------------------------------------------------------------------

	@Override
	public int compareTo(HelpTopicCategory category)
	{
		return name.compareTo(category.name);
	}
	
	//--------------------------------------------------------------------------	
}

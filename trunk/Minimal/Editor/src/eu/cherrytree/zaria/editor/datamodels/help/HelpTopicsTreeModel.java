/****************************************/
/* HelpTopicsTreeModel.java 			*/
/* Created on: 07-Jul-2014				*/
/* Copyright Cherry Tree Studio 2014	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.editor.datamodels.help;

import eu.cherrytree.zaria.editor.classlist.ZoneScriptFunction;
import java.util.ArrayList;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public class HelpTopicsTreeModel implements TreeModel
{
	//--------------------------------------------------------------------------
	
	private HelpTopicCategory root = new HelpTopicCategory("root");

	//--------------------------------------------------------------------------
	
	public HelpTopicsTreeModel(String[] links, String base, ArrayList<ZoneScriptFunction> functions)
	{
		for(String link : links)
			createHtmlTopic(link.replace(base, ""), link);
		
		for(ZoneScriptFunction function : functions)
			createFunctionTopic(function);
	}
	
	//--------------------------------------------------------------------------
	
	private void createHtmlTopic(String path, String fullPath)
	{
		String[] names = path.split("/");
		HelpTopicCategory category = root;
		
		for(int i = 0 ; i < names.length-1 ; i++)
			category = category.getCategory(names[i]);
		
		category.addTopic(new HtmlHelpTopic(fullPath));
	}
	
	//--------------------------------------------------------------------------
	
	private void createFunctionTopic(ZoneScriptFunction function)
	{	
		HelpTopicCategory category = root.getCategory("Scripting").getCategory("Functions").getCategory(function.getCategory());		
		category.addTopic(new ZoneFunctionHelpTopic(function));
	}
	
	//--------------------------------------------------------------------------
	@Override
	public Object getRoot()
	{
		return root;
	}
	
	//--------------------------------------------------------------------------

	@Override
	public Object getChild(Object parent, int index)
	{
		if(parent instanceof HelpTopicCategory)
			return ((HelpTopicCategory) parent).getChild(index);
		else
			return null;
	}
	
	//--------------------------------------------------------------------------

	@Override
	public int getChildCount(Object parent)
	{
		if(parent instanceof HelpTopicCategory)
			return ((HelpTopicCategory) parent).getChildCount();
		else
			return 0;
	}
	
	//--------------------------------------------------------------------------

	@Override
	public boolean isLeaf(Object node)
	{
		return node instanceof HelpTopic;
	}
	
	//--------------------------------------------------------------------------
	
	@Override
	public int getIndexOfChild(Object parent, Object child)
	{
		if(parent instanceof HelpTopicCategory)
			return ((HelpTopicCategory) parent).getIndex(child);
		else 
			return -1;
	}
	
	//--------------------------------------------------------------------------

	@Override
	public void valueForPathChanged(TreePath path, Object newValue)
	{
		// Intentionally empty.
	}
	
	//--------------------------------------------------------------------------
	
	@Override
	public void addTreeModelListener(TreeModelListener l)
	{
		// Intentionally empty.
	}
	
	//--------------------------------------------------------------------------

	@Override
	public void removeTreeModelListener(TreeModelListener l)
	{
		// Intentionally empty.
	}

	//--------------------------------------------------------------------------
}

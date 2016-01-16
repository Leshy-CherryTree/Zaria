/****************************************/
/* ObjectDefinitonTreeModel.java 		*/
/* Created on: 27-Apr-2014				*/
/* Copyright Cherry Tree Studio 2014	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.editor.datamodels.definitions;

import com.fasterxml.jackson.core.JsonProcessingException;
import eu.cherrytree.zaria.editor.classlist.ZoneClass;
import eu.cherrytree.zaria.editor.document.ZoneDocument;
import eu.cherrytree.zaria.editor.serialization.Serializer;
import eu.cherrytree.zaria.serialization.ZariaObjectDefinition;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.JTree;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public class ObjectDefinitonTreeModel implements TreeModel
{
	//--------------------------------------------------------------------------
	
	private JTree tree;
	private Class<? extends ZariaObjectDefinition> baseClass;
	
	private ArrayList<ObjectDefinitiontType> types = new ArrayList<>();
	
	//--------------------------------------------------------------------------
	
	public ObjectDefinitonTreeModel(Class<? extends ZariaObjectDefinition> baseClass, JTree tree)
	{
		assert baseClass != null;
		
		this.baseClass = baseClass;
		this.tree = tree;
	}		
	
	//--------------------------------------------------------------------------
	
	protected void updateTree()
	{
		tree.updateUI();
		tree.expandPath(new TreePath(types));
	}
	
	//--------------------------------------------------------------------------

	public void setFile(File file) throws IOException, ZoneClass.InvalidClassHierarchyException, InstantiationException, IllegalAccessException
	{
		setText(ZoneDocument.loadFileAsString(file));
	}
	
	//--------------------------------------------------------------------------
	
	public void setText(String text) throws IOException, ZoneClass.InvalidClassHierarchyException, InstantiationException, IllegalAccessException
	{
		setDefinitionArray(Serializer.createDeserializationMapper().readValue(text, ZariaObjectDefinition[].class));
	}
	
	//--------------------------------------------------------------------------
			
	public void setDefinitionArray(ZariaObjectDefinition[] definitions) throws ZoneClass.InvalidClassHierarchyException, InstantiationException, IllegalAccessException, JsonProcessingException
	{
		HashMap<Class, ObjectDefinitiontType> typesMap = new HashMap<>();
		types.clear();
		
		for (ZariaObjectDefinition definition : definitions)
		{
			if(!baseClass.isAssignableFrom(definition.getClass()))
				continue;
			
			ObjectDefinitiontType type;
			
			if(!typesMap.containsKey(definition.getClass()))
			{
				type = new ObjectDefinitiontType(new ZoneClass(definition.getClass()));
				typesMap.put(definition.getClass(), type);
				types.add(type);
			}
			else			
				type = typesMap.get(definition.getClass());
			
			type.getDefinitions().add(new ObjectDefinitionWrapper(definition));
		}
		
		updateTree();
	}

	//--------------------------------------------------------------------------
			
	@Override
	public Object getRoot()
	{
		return types;
	}
	
	//--------------------------------------------------------------------------

	@Override
	public Object getChild(Object parent, int index)
	{
		if(parent == types)
			return types.get(index);
		else if(parent instanceof ObjectDefinitiontType)
			return ((ObjectDefinitiontType) parent).getDefinitions().get(index);
		else
			return null;
	}
	
	//--------------------------------------------------------------------------

	@Override
	public int getChildCount(Object parent)
	{
		if(parent == types)
			return types.size();
		else if(parent instanceof ObjectDefinitiontType)
			return ((ObjectDefinitiontType) parent).getDefinitions().size();
		else
			return 0;
	}
	
	//--------------------------------------------------------------------------

	@Override
	public boolean isLeaf(Object node)
	{
		return node instanceof ObjectDefinitionWrapper;
	}
	
	//--------------------------------------------------------------------------
	
	@Override
	public int getIndexOfChild(Object parent, Object child)
	{
		if(parent == types)
			types.indexOf(child);
		else if(parent instanceof ObjectDefinitiontType)
			return ((ObjectDefinitiontType) parent).getDefinitions().indexOf(child);
		
		return -1;
	}
	
	//--------------------------------------------------------------------------

	@Override
	public void addTreeModelListener(TreeModelListener l)
	{
		
	}
	
	//--------------------------------------------------------------------------

	@Override
	public void removeTreeModelListener(TreeModelListener l)
	{
		
	}
	
	//--------------------------------------------------------------------------
	
	@Override
	public void valueForPathChanged(TreePath path, Object newValue)
	{
		// Intentionally empty.
	}
	
	//--------------------------------------------------------------------------
}

/****************************************/
/* DefinitionListModel.java				*/
/* Created on: 02-08-2016				*/
/* Copyright Cherry Tree Studio 2016	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.gdx.particles.editor.datamodel;

import eu.cherrytree.zaria.serialization.ZariaObjectDefinition;

import java.util.ArrayList;

import javax.swing.AbstractListModel;
import javax.swing.event.ListDataListener;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public class DefinitionListModel extends AbstractListModel<DefinitionWrapper>
{
	//--------------------------------------------------------------------------
	
	private ArrayList<DefinitionWrapper> definitions = new ArrayList<>();
	
	//--------------------------------------------------------------------------
	
	public void clear()
	{
		definitions.clear();
	}
	
	//--------------------------------------------------------------------------
	
	public void addDefinition(ZariaObjectDefinition definition)
	{
		definitions.add(new DefinitionWrapper(definition));
	}
	
	//--------------------------------------------------------------------------

	@Override
	public int getSize()
	{
		return definitions.size();
	}
	
	//--------------------------------------------------------------------------

	@Override
	public DefinitionWrapper getElementAt(int index)
	{
		return definitions.get(index);
	}
	
	//--------------------------------------------------------------------------

	public void update()
	{
		fireContentsChanged(this, 0, getSize());
	}

	//--------------------------------------------------------------------------
}

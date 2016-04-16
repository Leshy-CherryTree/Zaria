/****************************************/
/* ScriptPaletteDataModel.java 			*/
/* Created on: 06-Jul-2014				*/
/* Copyright Cherry Tree Studio 2014	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.editor.datamodels.palette;

import eu.cherrytree.zaria.editor.classlist.ZoneClassList;
import eu.cherrytree.zaria.editor.classlist.ZoneScriptStaticFunction;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public class ScriptPaletteDataModel extends PaletteDataModel
{
	//--------------------------------------------------------------------------
		
	private ZoneClassList classList;	

	private String filter = "";
	
	//--------------------------------------------------------------------------

	public ScriptPaletteDataModel(ZoneClassList classList)
	{
		this.classList  = classList;
	}
			
	//--------------------------------------------------------------------------
	
	public final void populate()
	{
		root = new PaletteRoot();
		
		for(ZoneScriptStaticFunction function : classList.getScriptFunctions())
		{
			String cat = function.getCategory();

			if(function.getName().toLowerCase().contains(filter))
				root.getCategory(cat).addObject(new PalettFunctionTemplate(function));
		}
		
		for(TreeModelListener listener : listeners)		
			listener.treeStructureChanged(new TreeModelEvent(this, new Object[]{root}));
	}

	//--------------------------------------------------------------------------	

	@Override
	public void setFilter(String filter)
	{
		this.filter = filter.toLowerCase();
		populate();
	}
	
	//--------------------------------------------------------------------------
}

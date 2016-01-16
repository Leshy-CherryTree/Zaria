/****************************************/
/* ZonePaletteDataModel.java            */
/* Created on: 06-May-2013				*/
/* Copyright Cherry Tree Studio 2013	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.editor.datamodels.palette;

import eu.cherrytree.zaria.editor.classlist.ZoneClass;
import eu.cherrytree.zaria.editor.classlist.ZoneClassList;
import eu.cherrytree.zaria.editor.document.ZoneDocument;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;


/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public class ZonePaletteDataModel extends PaletteDataModel
{
	//--------------------------------------------------------------------------
		
	private ZoneClassList classList;	
	private ZoneDocument.DocumentType currentType;
	
	private String filter = "";
	
	//--------------------------------------------------------------------------

	public ZonePaletteDataModel(ZoneClassList classList)
	{
		this.classList  = classList;
	}
			
	//--------------------------------------------------------------------------
	
	public final void populate(ZoneDocument.DocumentType type)
	{
		root = new PaletteRoot();
		
		for(ZoneClass c : classList.getClasses())
		{
			String cat = c.getCategory();
			
			if(c.getName().toLowerCase().contains(filter))
				root.getCategory(cat).addObject(new PaletteObjectTemplate(c));
		}
		
		for(TreeModelListener listener : listeners)		
			listener.treeStructureChanged(new TreeModelEvent(this, new Object[]{root}));
		
		currentType = type;
	}
	
	//--------------------------------------------------------------------------
	
	public ZoneDocument.DocumentType getCurrentType()
	{
		return currentType;
	}

	//--------------------------------------------------------------------------	

	@Override
	public void setFilter(String filter)
	{
		if(currentType != null)
		{
			this.filter = filter.toLowerCase();
			populate(currentType);
		}
	}
	
	//--------------------------------------------------------------------------
}

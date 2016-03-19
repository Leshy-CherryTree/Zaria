/****************************************/
/* LinkRenderer.java						*/
/* Created on: 13-Mar-2015				*/
/* Copyright Cherry Tree Studio 2015		*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.editor.properties.renderers;

import eu.cherrytree.zaria.editor.database.DataBase;
import java.util.UUID;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public class LinkRenderer extends DefaultCellRenderer
{
	//--------------------------------------------------------------------------
	
	@Override
	protected String convertToString(Object value)
	{
		if(value == null)
			return "";
		
		try
		{
			return DataBase.getID((UUID) value);
		}
		catch (DataBase.DuplicateUUIDFoundException ex)
		{
			return "ERROR";
		}
	}

	//--------------------------------------------------------------------------
}

/****************************************/
/* LinkRenderer.java						*/
/* Created on: 13-Mar-2015				*/
/* Copyright Cherry Tree Studio 2015		*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.editor.properties.renderers;

import eu.cherrytree.zaria.editor.EditorApplication;
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
		if (value == null)
			return "";
		
		try
		{
			UUID uuid = (UUID) value;
			String name = DataBase.getID(uuid);
			String location = DataBase.getLocation(uuid);

			if (location != null)
			{
				if (location.startsWith(EditorApplication.getAssetsLocation()))
					location = location.substring(EditorApplication.getAssetsLocation().length());
				else if (location.startsWith(EditorApplication.getScriptsLocation()))
					location = location.substring(EditorApplication.getScriptsLocation().length());
			}

			return name + " [" + location + "]";
		}
		catch (DataBase.DuplicateUUIDFoundException ex)
		{
			return "ERROR";
		}
	}

	//--------------------------------------------------------------------------
}

/****************************************/
/* ArrayListRenderer.java				*/
/* Created on: 25-May-2014				*/
/* Copyright Cherry Tree Studio 2014	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.editor.components;

import eu.cherrytree.zaria.editor.EditorApplication;
import eu.cherrytree.zaria.editor.database.DataBase;
import eu.cherrytree.zaria.editor.debug.DebugConsole;

import java.awt.Component;
import java.util.UUID;
import java.util.logging.Level;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public class ArrayListRenderer extends DefaultListCellRenderer
{
	private boolean showingLinks;
	
	//--------------------------------------------------------------------------

	public ArrayListRenderer(boolean showingLinks)
	{
		this.showingLinks = showingLinks;
	}
		
	//--------------------------------------------------------------------------
	
	private int getSpaces(int value)
	{
		if(value == 0)
			return 1;
		else
			return (int) (Math.floor(Math.log10(value)) + 1);
	}
	
	//--------------------------------------------------------------------------
	
	private String getString(Object obj)
	{
		if (obj == null)
		{
			return "";
		}
		else if (showingLinks)
		{
			assert obj.getClass() == UUID.class;
			
			try
			{
				UUID uuid = (UUID) obj;
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
				DebugConsole.logger.log(Level.SEVERE, null, ex);
				return "ERROR";
			}
		}
		else
		{
			return obj.toString();
		}
	}
	
	//--------------------------------------------------------------------------

	@Override
	public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus)
	{
		DefaultListCellRenderer component = (DefaultListCellRenderer) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
				
		int max = getSpaces(list.getModel().getSize());
		int idx_chrs = getSpaces(index);
		
		String spaces = "";
		
		for(int i = 0 ; i < max-idx_chrs ; i++)
			spaces += " ";
		
		component.setText(spaces + index + ". " + getString(value));
						
		return component;
	}
	
	//--------------------------------------------------------------------------
}

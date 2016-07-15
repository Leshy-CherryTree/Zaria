/****************************************/
/* EditorFactory.java					*/
/* Created on: 15-07-2016				*/
/* Copyright Cherry Tree Studio 2016	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.editor.dialogs;

import eu.cherrytree.zaria.editor.debug.DebugConsole;
import eu.cherrytree.zaria.serialization.ZariaObjectDefinition;
import eu.cherrytree.zaria.math.Curve;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import javax.swing.JFrame;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public class EditorFactory
{
	//--------------------------------------------------------------------------
	
	private static final HashMap<Class<? extends ZariaObjectDefinition>,Class<? extends EditorDialog>> editors = new HashMap<>();
	
	//--------------------------------------------------------------------------
	
	static
	{
		editors.put(Curve.class, CurveEditingDialog.class);
	}
	
	//--------------------------------------------------------------------------
	
	public static boolean hasEditor(Class<? extends ZariaObjectDefinition> cls)
	{
		for(Class type : editors.keySet())
		{
			if(type.isAssignableFrom(cls))
				return true;
		}
		
		return false;
	}
	
	//--------------------------------------------------------------------------
	
	public static EditorDialog getEditor(ZariaObjectDefinition definition, JFrame parentFrame)
	{
		Class editor_class = null;
		
		for(Map.Entry<Class<? extends ZariaObjectDefinition>,Class<? extends EditorDialog>> entry : editors.entrySet())
		{
			if(entry.getKey().isAssignableFrom(definition.getClass()))
				editor_class = entry.getValue();
		}
		
		if(editor_class != null)
		{
			try
			{
				EditorDialog dialog = (EditorDialog) editor_class.getDeclaredConstructor(JFrame.class, ZariaObjectDefinition.class).newInstance(parentFrame, definition);

				return dialog;
			}
			catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException ex)
			{
				DebugConsole.logger.log(Level.SEVERE, null, ex);
			}
		}
		
		return null;
	}
	
	//--------------------------------------------------------------------------
}

/****************************************/
/* ArrayPropertyEditor.java             */
/* Created on: 21-Jul-2013				*/
/* Copyright Cherry Tree Studio 2013	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.editor.properties.editors;

import eu.cherrytree.zaria.editor.document.DocumentManager;
import eu.cherrytree.zaria.editor.properties.renderers.ArrayPropertyRenderer;

import java.awt.event.ActionEvent;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public class ArrayPropertyEditor extends DialogPropertyEditor
{
	//--------------------------------------------------------------------------
	
	private ArrayPropertyRenderer renderer;
	
	private Object array;
	private String arrayName;
	
	private boolean links;

	//--------------------------------------------------------------------------

	public ArrayPropertyEditor(String type, String arrayName, boolean links)
	{		
		super(new ArrayPropertyRenderer(type));
		
		this.arrayName = arrayName;
		this.links = links;
		
		renderer = (ArrayPropertyRenderer) getComponent();
	}
	
	//--------------------------------------------------------------------------

	@Override
	public void setValue(Object value)
	{
		array = value;
		renderer.setValue(array);
	}

	//--------------------------------------------------------------------------

	@Override
	public Object getValue()
	{
		return array;
	}

	//--------------------------------------------------------------------------

	@Override
	public void actionPerformed(ActionEvent event)
	{
		if(event.getSource() == button)
		{
			Object array_obj = DocumentManager.openEditArrayDialog(arrayName, array, links);
			
			if (array_obj != null)
			{
				Object old = array;
				array = array_obj;
				
				firePropertyChange(old, array);
				renderer.setValue(array);
			}
		}
	}
	
	//--------------------------------------------------------------------------
}

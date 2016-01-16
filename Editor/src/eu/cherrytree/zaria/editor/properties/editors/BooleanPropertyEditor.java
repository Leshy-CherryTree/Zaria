/****************************************/
/* BooleanPropertyEditor.java			*/
/* Created on: 13-Oct-2013				*/
/* Copyright Cherry Tree Studio 2013	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.editor.properties.editors;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;

/** 
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 * 
 * Based upon the unmaintained code from L2FProd.com.
 */
public class BooleanPropertyEditor extends AbstractPropertyEditor
{
	//--------------------------------------------------------------------------
	
	private JCheckBox editor;

	//--------------------------------------------------------------------------

	public BooleanPropertyEditor()
	{
		editor = new JCheckBox();		
		editor.setOpaque(false);
		
		editor.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				firePropertyChange( editor.isSelected() ? Boolean.FALSE : Boolean.TRUE,
									editor.isSelected() ? Boolean.TRUE : Boolean.FALSE);
									editor.transferFocus();
			}
		});
	}

	//--------------------------------------------------------------------------

	@Override
	public Object getValue()
	{
		return editor.isSelected() ? Boolean.TRUE : Boolean.FALSE;
	}

	//--------------------------------------------------------------------------

	@Override
	public void setValue(Object value)
	{
		editor.setSelected(Boolean.TRUE.equals(value));
	}

	//--------------------------------------------------------------------------

	@Override
	public Component getCustomEditor()
	{
		return editor;
	}

	//--------------------------------------------------------------------------

	@Override
	public boolean supportsCustomEditor()
	{
		return false;
	}

	//--------------------------------------------------------------------------
}

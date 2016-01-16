/****************************************/
/* NumberPropertyEditor.java			*/
/* Created on: 13-Oct-2013				*/
/* Copyright Cherry Tree Studio 2013	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.editor.properties.editors;

import eu.cherrytree.zaria.editor.properties.propertysheet.UIUtils;

import java.awt.Component;
import javax.swing.JFormattedTextField;


/** 
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 * 
 * Based upon the unmaintained code from L2FProd.com.
 */
public class NumberPropertyEditor extends AbstractNumberPropertyEditor
{
	//--------------------------------------------------------------------------
	
	private JFormattedTextField editor;

	//--------------------------------------------------------------------------

	public NumberPropertyEditor(Class type)
	{
		super(type);
		
		editor = new JFormattedTextField();
		
		editor.setValue(getDefaultValue());
		editor.setBorder(UIUtils.EMPTY_BORDER);
	}

	//--------------------------------------------------------------------------

	@Override
	public void setValue(Object value)
	{
		if(value instanceof Number)
		{
			editor.setText(value.toString());
			lastValidValue = (Number) value;
		}
		else
		{
			editor.setValue(getDefaultValue());
			lastValidValue = 0;
		}
	}

	//--------------------------------------------------------------------------

	@Override
	public String getValueText()
	{
		return editor.getText();
	}
	
	//--------------------------------------------------------------------------

	@Override
	public Component getCustomEditor()
	{
		return editor;
	}
	
	//--------------------------------------------------------------------------
}
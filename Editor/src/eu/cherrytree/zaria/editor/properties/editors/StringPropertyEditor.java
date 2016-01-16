/****************************************/
/* StringPropertyEditor.java			*/
/* Created on: 13-Oct-2013				*/
/* Copyright Cherry Tree Studio 2013	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.editor.properties.editors;

import eu.cherrytree.zaria.editor.properties.propertysheet.UIUtils;
import java.awt.Component;
import javax.swing.JTextField;
import javax.swing.text.JTextComponent;

/** 
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 * 
 * Based upon the unmaintained code from L2FProd.com.
 */
public class StringPropertyEditor extends AbstractPropertyEditor
{
	//--------------------------------------------------------------------------
	
	private JTextField editor;

	//--------------------------------------------------------------------------

	public StringPropertyEditor()
	{
		editor = new JTextField();
		editor.setBorder(UIUtils.EMPTY_BORDER);
	}

	//--------------------------------------------------------------------------

	@Override
	public Object getValue()
	{
		return editor.getText();
	}

	//--------------------------------------------------------------------------

	@Override
	public void setValue(Object value)
	{
		if(value == null)
			editor.setText("");
		else
			editor.setText(value.toString());
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

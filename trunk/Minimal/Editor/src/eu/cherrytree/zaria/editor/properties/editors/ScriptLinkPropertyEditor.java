/****************************************/
/* ScriptLinkPropertyEditor.java 		*/
/* Created on: 02-Jun-2014				*/
/* Copyright Cherry Tree Studio 2014	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.editor.properties.editors;

import eu.cherrytree.zaria.editor.EditorApplication;
import eu.cherrytree.zaria.editor.document.DocumentManager;
import eu.cherrytree.zaria.editor.properties.propertysheet.UIUtils;

import java.awt.event.ActionEvent;
import javax.swing.JTextField;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public class ScriptLinkPropertyEditor extends DialogPropertyEditor
{
	//--------------------------------------------------------------------------
	
	private String id = "";
	private JTextField textField;

	//--------------------------------------------------------------------------

	public ScriptLinkPropertyEditor()
	{
		super(new JTextField());	
		
		textField = (JTextField) getComponent();
		
		textField.setBorder(UIUtils.EMPTY_BORDER);
		textField.addActionListener(this);		
	}

	//--------------------------------------------------------------------------

	@Override
	public void setValue(Object value)
	{		
		id = (String) value;
		textField.setText(id);
	}

	//--------------------------------------------------------------------------

	@Override
	public Object getValue()
	{
		return id;
	}

	//--------------------------------------------------------------------------
	
	private void updateID(String newID)
	{
		if(!id.equals(newID))
		{
			String old = newID;
			id = newID;

			firePropertyChange(old, id);
		}
	}
	
	//--------------------------------------------------------------------------

	@Override
	public void actionPerformed(ActionEvent event)
	{
		if(event.getSource() == button)
		{
			String new_id = DocumentManager.openBrowseScriptsDialog(EditorApplication.getScriptsLocation());
			
			if(new_id != null && !new_id.isEmpty())
			{
				updateID(new_id);
				textField.setText(new_id);
			}
		}
		else if(event.getSource() == textField)
		{
			updateID(textField.getText());			
		}
	}
	
	//--------------------------------------------------------------------------
}

/****************************************/
/* LinkPropertyEditor.java				*/
/* Created on: 10-May-2014				*/
/* Copyright Cherry Tree Studio 2014	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.editor.properties.editors;

import eu.cherrytree.zaria.editor.EditorApplication;
import eu.cherrytree.zaria.editor.database.DataBase;
import eu.cherrytree.zaria.editor.document.DocumentManager;
import eu.cherrytree.zaria.editor.properties.propertysheet.UIUtils;
import eu.cherrytree.zaria.serialization.ZariaObjectDefinition;

import java.awt.event.ActionEvent;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public class LinkPropertyEditor extends DialogPropertyEditor
{
	//--------------------------------------------------------------------------
	
	private UUID uuid = null;
	private JLabel textField;
	
	private Class<? extends ZariaObjectDefinition> baseClass;
	
	//--------------------------------------------------------------------------

	public LinkPropertyEditor(Class<? extends ZariaObjectDefinition> baseClass, boolean isLibrary)
	{
		super(new JLabel());	
		
		this.baseClass = baseClass;
		
		textField = (JLabel) getComponent();
		
		textField.setBorder(UIUtils.EMPTY_BORDER);
	}

	//--------------------------------------------------------------------------

	@Override
	public void setValue(Object value)
	{		
		uuid = (UUID) value;
		
		updateText();
	}

	//--------------------------------------------------------------------------

	@Override
	public Object getValue()
	{
		return uuid;
	}

	//--------------------------------------------------------------------------
	
	private void updateID(UUID newUUID)
	{
		if(	(uuid != null && newUUID == null) ||
			(uuid == null && newUUID != null) ||
			(uuid != null && !uuid.equals(newUUID)))
		{
			UUID old = newUUID;
			uuid = newUUID;

			firePropertyChange(old, uuid);
		}
	}
	
	//--------------------------------------------------------------------------
	
	private void updateText()
	{
		if(uuid == null)
		{
			textField.setText("");
		}
		else
		{
			try
			{
				textField.setText(DataBase.getID(uuid));
			}
			catch (DataBase.DuplicateUUIDFoundException ex)
			{
				textField.setText("ERROR");
			}	
		}		
	}
	
	//--------------------------------------------------------------------------

	@Override
	public void actionPerformed(ActionEvent event)
	{
		if(event.getSource() == button)
		{
			UUID new_uuid = DocumentManager.openBrowseDefinitionsDialog(baseClass, EditorApplication.getAssetsLocation());
			
			if(new_uuid != null)
			{
				updateID(new_uuid);
				updateText();
			}
		}
	}
	
	//--------------------------------------------------------------------------
}
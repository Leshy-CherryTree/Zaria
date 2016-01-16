/****************************************/
/* DialogPropertyEditor.java            */
/* Created on: 20-Jul-2013				*/
/* Copyright Cherry Tree Studio 2013	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.editor.properties.editors;

import eu.cherrytree.zaria.editor.properties.propertysheet.PercentLayout;

import java.awt.Component;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public abstract class DialogPropertyEditor extends AbstractPropertyEditor implements ActionListener
{
	//--------------------------------------------------------------------------
	
	protected JButton button;
	private JPanel editor;
	private JComponent component;

	//--------------------------------------------------------------------------

	public DialogPropertyEditor(JComponent componenent)
	{
		this.component = componenent;
		
		editor = new JPanel(new PercentLayout(PercentLayout.HORIZONTAL, 0));
		button = new JButton("...");

		editor.add("*", componenent);
		componenent.setOpaque(false);

		editor.add(button);
		button.addActionListener(this);
		editor.setOpaque(false);
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
	
	protected JComponent getComponent()
	{
		return component;
	}
	
	//--------------------------------------------------------------------------
}

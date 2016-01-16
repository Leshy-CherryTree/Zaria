/****************************************/
/* SliderNumberPropertyEditor.java 		*/
/* Created on: 06-Apr-2014				*/
/* Copyright Cherry Tree Studio 2014	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.editor.properties.editors;

import eu.cherrytree.zaria.editor.properties.propertysheet.PercentLayout;
import eu.cherrytree.zaria.editor.properties.propertysheet.UIUtils;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFormattedTextField;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public abstract class SliderNumberPropertyEditor extends AbstractNumberPropertyEditor implements ChangeListener, ActionListener
{
	//--------------------------------------------------------------------------
	
	protected JSlider slider;
	protected JFormattedTextField textField;
	
	private JPanel editor;	
	
	//--------------------------------------------------------------------------

	//TODO Make this work for all number types.
	
	public SliderNumberPropertyEditor(Class type)
	{
		super(type);
		
		this.textField = new JFormattedTextField();
		
		editor = new JPanel(new PercentLayout(PercentLayout.HORIZONTAL, 0));
		slider = new JSlider(JSlider.HORIZONTAL);
				
		editor.add("60",textField);
		
		textField.setValue(getDefaultValue());
		textField.setBorder(UIUtils.EMPTY_BORDER);
		textField.addActionListener(this);
		
		editor.add("*",slider);
		editor.setOpaque(false);
		
		slider.addChangeListener(this);
	}
	
	//--------------------------------------------------------------------------

	@Override
	public Component getCustomEditor()
	{
		return editor;
	}

	//--------------------------------------------------------------------------

	@Override
	public String getValueText()
	{
		return textField.getText();
	}
	
	//--------------------------------------------------------------------------
	
	@Override
	public void stateChanged(ChangeEvent e)
	{
		// TODO This makes things reliant on the width of the slider, which is stupid.
		//		Find another way to set this.
		textField.setValue(convertToTextFieldValue(slider.getValue()));
		textField.getParent().repaint();
	}
	
	//--------------------------------------------------------------------------
	
	@Override
	public void actionPerformed(ActionEvent evt)
	{						
		if(evt.getSource() == textField)
		{
			slider.setValue(convertToSliderValue(textField.getValue()));
			firePropertyChange(getValue(), textField.getValue());
		}
	}		
	
	//--------------------------------------------------------------------------
	
	protected abstract Object convertToTextFieldValue(int sliderValue);	
	protected abstract int convertToSliderValue(Object value);
	
	//--------------------------------------------------------------------------
}

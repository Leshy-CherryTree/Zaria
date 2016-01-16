/****************************************/
/* IntegerSliderPropertyEditor.java 	*/
/* Created on: 06-Apr-2014				*/
/* Copyright Cherry Tree Studio 2014	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.editor.properties.editors;

import eu.cherrytree.zaria.editor.properties.MinMaxInfo;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public class IntegerSliderPropertyEditor extends SliderNumberPropertyEditor
{
	//--------------------------------------------------------------------------
	
	public IntegerSliderPropertyEditor(MinMaxInfo<Integer> minMaxInfo)
	{				
		super(Integer.class);
		
		slider.setMinimum(minMaxInfo.getMin());
		slider.setMaximum(minMaxInfo.getMax());
	}
			
//	//--------------------------------------------------------------------------
//
//	@Override
//	public String getValueText()
//	{				
//		return Integer.toString(slider.getValue());
//	}
//	
//	//--------------------------------------------------------------------------

	@Override
	public void setValue(Object value)
	{
		if(value instanceof Number)
		{
			slider.setValue((int) value);
			textField.setValue(value);
			lastValidValue = (Number) value;
		}
		else
		{
			slider.setValue((int) getDefaultValue());
			textField.setValue(getDefaultValue());
			lastValidValue = 0;
		}
	}
	
	//--------------------------------------------------------------------------

	@Override
	protected Object convertToTextFieldValue(int sliderValue)
	{
		return sliderValue;
	}
	
	//--------------------------------------------------------------------------
	
	@Override
	protected int convertToSliderValue(Object value)
	{
		return (int) value;
	}
	
	//--------------------------------------------------------------------------
}

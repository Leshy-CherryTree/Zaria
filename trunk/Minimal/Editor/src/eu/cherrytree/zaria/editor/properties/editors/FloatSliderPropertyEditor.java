/****************************************/
/* FloatSliderPropertyEditor.java 		*/
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
public class FloatSliderPropertyEditor  extends SliderNumberPropertyEditor
{
	//--------------------------------------------------------------------------
	
	private static final int maxSliderValue = 0x800000;		// Max of a 24-bit signed value.
	private static final float sliderDivider = maxSliderValue;
	
	//--------------------------------------------------------------------------
	
	private float step;
	private float min;

	//--------------------------------------------------------------------------
	
	public FloatSliderPropertyEditor(MinMaxInfo<Float> minMaxInfo)
	{				
		super(Float.class);
		
		min = minMaxInfo.getMin();
		
		if (minMaxInfo.isMoreThanMin())
			slider.setMinimum(1);
		else
			slider.setMinimum(0);
		
		if (minMaxInfo.isLessThanMax())
			slider.setMaximum(maxSliderValue - 1);
		else
			slider.setMaximum(maxSliderValue);
		
		step = (minMaxInfo.getMax() - minMaxInfo.getMin()) / sliderDivider;
	}
			
	//--------------------------------------------------------------------------

	@Override
	public void setValue(Object value)
	{
		if(value instanceof Number)
		{
			slider.setValue(convertToSliderValue(value));
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
		float value = sliderValue;
		value *= step;
		value += min;
		
		return value;
	}
	
	//--------------------------------------------------------------------------
	
	@Override
	protected int convertToSliderValue(Object value)
	{
		float float_value = ((float) value - min) / step;
		
		return (int) float_value;
	}
	
	//--------------------------------------------------------------------------
}
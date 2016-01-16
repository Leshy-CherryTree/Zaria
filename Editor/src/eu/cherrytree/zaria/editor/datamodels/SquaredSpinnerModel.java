/****************************************/
/* SquaredSpinnerModel.java 			*/
/* Created on: 27-Apr-2014				*/
/* Copyright Cherry Tree Studio 2014	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.editor.datamodels;

import javax.swing.AbstractSpinnerModel;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public class SquaredSpinnerModel extends AbstractSpinnerModel
{
	//--------------------------------------------------------------------------
	
	private float value;
	private float min;
	private float max;
	
	//--------------------------------------------------------------------------
	
	public SquaredSpinnerModel(float value, float min, float max)
	{
		this.value = value;
		this.min = min;
		this.max = max;
	}
			
	//--------------------------------------------------------------------------

	@Override
	public Object getValue()
	{
		return value;
	}
	
	//--------------------------------------------------------------------------

	@Override
	public void setValue(Object value)
	{
		if(!value.equals(this.value))
		{
			float newval = (float) value;

			if(newval >= min && newval <= max)
			{
				this.value = newval;
				fireStateChanged();
			}
		}
	}
	
	//--------------------------------------------------------------------------

	@Override
	public Object getNextValue()
	{
		return 2 * value;
	}
	
	//--------------------------------------------------------------------------

	@Override
	public Object getPreviousValue()
	{
		return value / 2;
	}	
	
	//--------------------------------------------------------------------------
}

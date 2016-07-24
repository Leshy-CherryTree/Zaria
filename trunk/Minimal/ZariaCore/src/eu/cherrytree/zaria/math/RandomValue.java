/****************************************/
/* RandomValue.java						*/
/* Created on: 24-07-2016				*/
/* Copyright Cherry Tree Studio 2016	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.math;

import eu.cherrytree.zaria.debug.DebugManager;
import eu.cherrytree.zaria.serialization.DefinitionValidation;
import eu.cherrytree.zaria.serialization.annotations.DefinitionDescription;
import eu.cherrytree.zaria.serialization.annotations.FieldDescription;
import eu.cherrytree.zaria.utilities.Random;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
@DefinitionDescription("Random value between min and max.")
public class RandomValue extends Value
{
	//--------------------------------------------------------------------------
	
	@FieldDescription("Minimum which this value can have.")
	public float min = 0.0f;
	
	@FieldDescription("Maximum which this value can have.")
	public float max = 0.0f;
	
	@FieldDescription("Whether to use gaussian distriburion when choosing random value.")
	public boolean gaussian = false;

	//--------------------------------------------------------------------------
	
	@Override
	public float get()
	{
		if (min == max)
		{
			if (DebugManager.isActive())
			{
				DebugManager.alert("Random Value" + getID() + " has min equal max!");
				
				return min;
			}
		}
		
		float rand = gaussian ? Random.getGuassian() : Random.getFloat();
		
		return min + (max - min) * rand;
	}
	
	//--------------------------------------------------------------------------

	@Override
	public void onValidate(DefinitionValidation validation)
	{
		super.onValidate(validation);
		
		validation.addError(min > max, "min", min, "Min value must be less than the Max value");
	}

	//--------------------------------------------------------------------------
}

/****************************************/
/* MinMaxInfo.java						*/
/* Created on: 06-Apr-2014				*/
/* Copyright Cherry Tree Studio 2014		*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.editor.properties;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public class MinMaxInfo<Type extends Number>
{
	//--------------------------------------------------------------------------
	
	private Type min;
	private Type max;
	
	private boolean moreThanMin;
	private boolean lessThanMax;
	
	private Class<Type> type;
	
	//--------------------------------------------------------------------------

	public MinMaxInfo(Class<Type> type, Type min, Type max, boolean moreThanMin, boolean lessThanMax)
	{
		this.min = min;
		this.max = max;
		this.moreThanMin = moreThanMin;
		this.lessThanMax = lessThanMax;
	}		
	
	//--------------------------------------------------------------------------

	public Type getMin()
	{
		return min;
	}

	//--------------------------------------------------------------------------
	
	public Type getMax()
	{
		return max;
	}

	//--------------------------------------------------------------------------
	
	public boolean isMoreThanMin()
	{
		return moreThanMin;
	}
	
	//--------------------------------------------------------------------------

	public boolean isLessThanMax()
	{
		return lessThanMax;
	}
	
	//--------------------------------------------------------------------------

	public Class<Type> getType()
	{
		return type;
	}
	
	//--------------------------------------------------------------------------
}

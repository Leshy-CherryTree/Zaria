/****************************************/
/* Curve.java							*/
/* Created on: 15-07-2016				*/
/* Copyright Cherry Tree Studio 2016	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.math;

import eu.cherrytree.zaria.serialization.ColorName;
import eu.cherrytree.zaria.serialization.DefinitionValidation;
import eu.cherrytree.zaria.serialization.ZariaObjectDefinition;
import eu.cherrytree.zaria.serialization.annotations.DefinitionCategory;
import eu.cherrytree.zaria.serialization.annotations.DefinitionColor;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.interpolation.LinearInterpolator;
import org.apache.commons.math3.analysis.interpolation.SplineInterpolator;
import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;
import org.apache.commons.math3.fitting.PolynomialCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoints;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
@DefinitionCategory("Math")
@DefinitionColor(ColorName.CadetBlue)
public class Curve extends ZariaObjectDefinition
{
	//--------------------------------------------------------------------------
	
	public enum CurveType
	{
		Linear(3),
		Spline(3),
		Polynomial(2);	
		
		private int minPoints;

		private CurveType(int minPoints)
		{
			this.minPoints = minPoints;
		}
				
		public int getMinPoints()
		{
			return minPoints;
		}				
	}
	
	//--------------------------------------------------------------------------
	
	private float[] x = { 0.0f , 0.5f , 1.0f };
	private float[] y = { 0.0f , 0.5f , 1.0f };
	
	private CurveType type =  CurveType.Linear;
	
	//--------------------------------------------------------------------------
	
	private transient float maxX,maxY,minX,minY;
	private transient UnivariateFunction function;
	
	//--------------------------------------------------------------------------
	
	public float getValue(float x)
	{
		if (x <= minX)
			return minY;
		else if (x >= maxX)
			return maxY;
		else
			return (float) function.value(x);
	}
	
	//--------------------------------------------------------------------------
	
	@Override
	public void onPreLoad()
	{
		switch (type)
		{
			case Linear:
			{
				double dx[] = new double[x.length];
				double dy[] = new double[y.length];
				
				for (int i = 0 ; i < x.length ; i++)
				{
					dx[i] = x[i];
					dy[i] = y[i];
				}
				
				minX = (float) x[0];
				minY = (float) y[0];

				maxX = (float) x[x.length-1];
				maxY = (float) y[y.length-1];
				
				function = new LinearInterpolator().interpolate(dx, dy);
			}
				break;
				
			case Spline:
			{
				double dx[] = new double[x.length];
				double dy[] = new double[y.length];
				
				for (int i = 0 ; i < x.length ; i++)
				{
					dx[i] = x[i];
					dy[i] = y[i];
				}
				
				minX = (float) x[0];
				minY = (float) y[0];

				maxX = (float) x[x.length-1];
				maxY = (float) y[y.length-1];
				
				function = new SplineInterpolator().interpolate(dx, dy);
			}
				break;
				
			case Polynomial:
			{
				WeightedObservedPoints wob = new WeightedObservedPoints();
		
				for (int i = 0 ; i < x.length ; i++)
					wob.add(x[i], y[i]);
		
				minX = -Float.MAX_VALUE;
				minY = -Float.MAX_VALUE;

				maxX = Float.MAX_VALUE;
				maxY = Float.MAX_VALUE;
				
				function = new PolynomialFunction(PolynomialCurveFitter.create(x.length).fit(wob.toList()));
			}
				break;
		}
	}
	
	//--------------------------------------------------------------------------

	@Override
	public void onValidate(DefinitionValidation validation)
	{
		validation.addError(x.length != y.length, "x y", x.length + " ; " + y.length, "Length of x and y arrays is not the same");
		validation.addError(x.length < type.getMinPoints(), "x y", x.length + " ; " + y.length, "There is too few points for the chosen curve type [" + type + "]");
		
		for (int i = 1 ; i < x.length ; i++)
		{
			if (x[i-1] > x[i])
			{
				validation.addError("x", "index: " + i, "Points are not monotonous.");
				break;
			}
		}
	}
	
	//--------------------------------------------------------------------------
}

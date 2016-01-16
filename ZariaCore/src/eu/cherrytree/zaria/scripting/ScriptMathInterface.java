/****************************************/
/* ScriptMathInterface.java 			*/
/* Created on: 05-Jul-2014				*/
/* Copyright Cherry Tree Studio 2014	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.scripting;

import eu.cherrytree.zaria.scripting.annotations.ScriptFunction;

/**
 * Exposes a mix of java.lang.Math and com.jme3.math.FastMath to script. 
 * As JavaScript uses doubles by default all FastMath functions that used floats
 * have been changed to use doubles.
 * 
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public class ScriptMathInterface
{
	//--------------------------------------------------------------------------
	
	@ScriptFunction
	(	
		category = "Math",
		description = "The ratio of the circumference of a circle to its diameter.",
		returnValue = "Returns PI.",
		parameters = {}
	)
    public static double pi()
	{
		return Math.PI;
	}
	
	//--------------------------------------------------------------------------
    
	@ScriptFunction
	(
		category = "Math",
		description = "PI multiplied by 2.",
		returnValue = "Returns PI multiplied by 2.",
		parameters = {}
	)
    public static double twoPi()
	{
		return 2.0 * Math.PI;
	}
	
	//--------------------------------------------------------------------------
    
	@ScriptFunction
	(
		category = "Math",
		description = "PI divided by 2.",
		returnValue = "Returns PI divided by 2.",
		parameters = {}
	)
    public static double halfPi()
	{
		return 0.5 * Math.PI;
	}
	
	//--------------------------------------------------------------------------
    
	@ScriptFunction
	(
		category = "Math",
		description = "PI divided by 4.",
		returnValue = "Returns PI divided by 4.",
		parameters = {}
	)
    public static double quarterPi()
	{
		return 0.25 * Math.PI;
	}
	
	//--------------------------------------------------------------------------
    
	@ScriptFunction
	(
		category = "Math",
		description = "1 divided by PI.",
		returnValue = "Returns 1 divided by PI.",
		parameters = {}
	)
    public static double inversePi()
	{
		return 1.0 / Math.PI;
	}
	
	//--------------------------------------------------------------------------
    
	@ScriptFunction
	(
		category = "Math",
		description = "1 divided by 2 * PI.",
		returnValue = "Returns 1 divided by 2 * PI.",
		parameters = {}
	)
    public static double invereseTwoPi()
	{
		return 1.0 / (2.0 * Math.PI);
	}
  
	//--------------------------------------------------------------------------
	
	@ScriptFunction
	(
		category = "Math",
		description = "Converts an angle measured in radians to an approximately equivalent angle measured in degrees.",
		returnValue = "The measurement of the angle in degrees.",
		parameters = {"angle", "An angle, in radians."}
	)
	public static double toDegrees(double angle)
	{
		return angle * (180.0 / Math.PI);
	}
	
	//--------------------------------------------------------------------------
	
	@ScriptFunction
	(
		category = "Math",
		description = "Converts an angle measured in degrees to an approximately equivalent angle measured in radians.",
		returnValue = "The measurement of the angle in radians.",
		parameters = {"angle", "An angle, in degrees."}
	)
	public static double toRadians(double angle)
	{
		return angle * (Math.PI / 180.0);
	}
	
	//--------------------------------------------------------------------------
	
	@ScriptFunction
	(
		category = "Math",
		description = "Linear interpolation from startValue to endValue by the given percent.",
		returnValue = "The interpolated value between startValue and endValue.",
		parameters =
		{
			"scale", "Scale value to use. if 1, use endValue, if 0, use startValue.",
			"startValue", "Begining value.",
			"endValue", "Ending value."
		}
	)
	public static double interpolate(double scale, double startValue, double endValue)
	{
		if (startValue == endValue)
			return startValue;
		
		if (scale <= 0)		
			return startValue;
		
		if (scale >= 1.0)
			return endValue;
		
		return ((1.0 - scale) * startValue) + (scale * endValue);
	}
	
	//--------------------------------------------------------------------------

	@ScriptFunction
	(
		category = "Math",
		description = "Returns the arc cosine of a value; the returned angle is in the range 0.0 through PI.",
		returnValue = "The arc cosine of the argument.",
		parameters = {"value", "The value whose arc cosine is to be returned."}
	)
	public static double acos(double value)
	{
		if (-1.0 < value)
		{
			if (value < 1.0)
				return Math.acos(value);

			return 0.0;
		}

		return Math.PI;
	}
	
	//--------------------------------------------------------------------------

	@ScriptFunction
	(
		category = "Math",
		description = "Returns the arc sine of a value; the returned angle is in the range -PI/2 through PI/2.",
		returnValue = "The arc sine of the argument.",
		parameters = {"value", "The value whose arc sine is to be returned."}
	)
	public static double asin(double value)
	{
		if (-1.0 < value)
		{
			if (value < 1.0)
				return Math.asin(value);

			return Math.PI / 2.0;
		}

		return -(Math.PI / 2.0);
	}
	
	//--------------------------------------------------------------------------

	@ScriptFunction
	(
		category = "Math",
		description = "Returns the arc tangent of a value; the returned angle is in the range -PI through PI/2.",
		returnValue = "The arc tangent of the argument.",
		parameters = {"value", "The value whose arc tangent is to be returned."}
	)
    public static double atan(double value)
	{
        return Math.atan(value);
    }
	
	//--------------------------------------------------------------------------

	@ScriptFunction
	(
		category = "Math",
		description = "Returns the angle theta from the conversion of rectangular coordinates (x,y) to polar coordinates (r,theta).",
		returnValue = "The theta component of the point (r,theta) in polar coordinates that corresponds to the point (x,y) in Cartesian coordinates.",
		parameters =
		{
			"t", "The ordinate coordinate.",
			"x", "The abscissa coordinate."
		}
	)
    public static double atan2(double y, double x)
	{
        return Math.atan2(y, x);
    }
	
	//--------------------------------------------------------------------------

	@ScriptFunction
	(
		category = "Math",
		description = "Returns the smallest (closest to negative infinity) value that is greater than or equal to the argument and is equal to a mathematical integer.",
		returnValue = "The smallest (closest to negative infinity) floating-point value that is greater than or equal to the argument and is equal to a mathematical integer.",
		parameters = {"value", "A value."}
	)
    public static double ceil(double value)
	{
        return Math.ceil(value);
    }
		
	//--------------------------------------------------------------------------

	@ScriptFunction
	(
		category = "Math",
		description = "Returns the largest (closest to positive infinity) value that is less than or equal to the argument and is equal to a mathematical integer.",
		returnValue = "The largest (closest to positive infinity) floating-point value that less than or equal to the argument  and is equal to a mathematical integer.",
		parameters = {"value", "A value."}
	)
	public static double floor(double value)
	{
		return Math.floor(value);
	}
	
	//--------------------------------------------------------------------------
	
	@ScriptFunction
	(
		category = "Math",
		description = "Returns the closest integer to the argument, with ties rounding up.",
		returnValue = "The value of the argument rounded to the nearest integer.",
		parameters = {"value", "A floating-point value to be rounded."}
	)
	public static double round(double value)
	{
		return Math.round(value);
	}
	
	//--------------------------------------------------------------------------

	@ScriptFunction
	(
		category = "Math",
		description = "Returns the trigonometric cosine of an angle.",
		returnValue = "The cosine of the argument.",
		parameters = {"angle", "An angle, in radians."}
	)
    public static double cos(double angle)
	{
        return Math.cos(angle);
    }
	
	//--------------------------------------------------------------------------

	@ScriptFunction
	(
		category = "Math",
		description = "Returns the trigonometric sine of an angle.",
		returnValue = "The sine of the argument.",
		parameters = {"angle", "An angle, in radians."}
	)
	public static double sin(double angle)
	{
		return Math.sin(angle);
	}
			
	//--------------------------------------------------------------------------

	@ScriptFunction
	(
		category = "Math",
		description = "Returns the trigonometric tangent of an angle.",
		returnValue = "The tangent of the argument.",
		parameters = {"angle","An angle, in radians."}
	)
	public static double tan(double angle)
	{
		return Math.tan(angle);
	}
	
	//--------------------------------------------------------------------------

	@ScriptFunction
	(
		category = "Math",
		description = "Returns the signum function of the argument.",
		details = "Return zero if the argument is zero, 1.0 if the argument is greater than zero, -1.0 if the argument is less than zero.",
		returnValue = "The signum function of the argument.",
		parameters = {"value", "The value whose signum is to be returned"}
	)
	public static double sign(double value)
	{
		return Math.signum(value);
	}
	
	//--------------------------------------------------------------------------

	@ScriptFunction
	(
		category = "Math",
		description = "Returns Euler's number e raised to the power of the input value.",
		returnValue = "The value of e^value where e is the base of the natural logarithms.",
		parameters = {"value", "The exponent to raise e to."}
	)
	public static double exp(double value)
	{
		return Math.exp(value);
	}
	
	//--------------------------------------------------------------------------

	@ScriptFunction
	(
		category = "Math",
		description = "Returns the absolute value of the input value.",
		returnValue = "The absolute value of the argument.",
		parameters = {"value", "The argument whose absolute value is to be determined."}
	)
	public static double abs(double value)
	{
		return value < 0 ? -value : value;
	}
	
	//--------------------------------------------------------------------------

	@ScriptFunction
	(
		category = "Math",
		description = "Returns the natural logarithm (base e) of the input value.",
		returnValue = "The natural logarithm of the input value.",
		parameters = {"value", "A value."}
	)
	public static double logN(double value)
	{
		return Math.log(value);
	}
	
	//--------------------------------------------------------------------------
	
	@ScriptFunction
	(
		category = "Math",
		description = "Returns the base 10 logarithm of the input value.",
		returnValue = "The base 10 logarithm of the input value.",
		parameters = {"value", "A value."}
	)
	public static double log10(double value)
	{
		return Math.log10(value);
	}
	
	//--------------------------------------------------------------------------

	@ScriptFunction
	(
		category = "Math",
		description = "Returns the logarithm of value with given base.",
		returnValue = "The logarithm of value with given base.",
		parameters =
		{
			"value", "A value.",
			"base", "Base of logarithm."
		}
	)
	public static double log(double value, double base)
	{
		return Math.log(value) / Math.log(base);
	}
	
	//--------------------------------------------------------------------------

	@ScriptFunction
	(
		category = "Math",
		description = "Returns the value of the first argument raised to the power of the second argument.",
		returnValue = "The value of base^exponent.",
		parameters =
		{
			"base", "The base.",
			"exponent", "The exponent."
		}
	)
	public static double pow(double base, double exponent)
	{
		return Math.pow(base, exponent);
	}
	
	//--------------------------------------------------------------------------

	@ScriptFunction
	(
		category = "Math",
		description = "Returns the value squared.",
		returnValue = "The square of the given value.",
		parameters = {"value","The vaule to square."}
	)
	public static double sqr(double value)
	{
		return value * value;
	}
	
	//--------------------------------------------------------------------------

	@ScriptFunction
	(
		category = "Math",
		description = "Returns the value cubed.",
		returnValue = "The cube of the given value.",
		parameters = {"value","The vaule to cube."}
	)
	public static double cqr(double value)
	{
		return value * value * value;
	}
	
	//--------------------------------------------------------------------------

	@ScriptFunction
	(
		category = "Math",
		description = "Returns the correctly rounded positive square root of the input value.",
		details = "If the argument is NaN or less than zero, the result is NaN.",
		returnValue = "The positive square root of the input value. ",
		parameters = {"value", "A value."}
	)
	public static double sqrt(double value)
	{
		return Math.sqrt(value);
	}
	
	//--------------------------------------------------------------------------

	@ScriptFunction
	(
		category = "Math",
		description = "Returns the cube root of the input value.",
		returnValue = "The cube root of the input value.",
		parameters = {"value", "A value."}
	)
	public static double cbrt(double value)
	{
		return Math.cbrt(value);
	}

	//--------------------------------------------------------------------------

	@ScriptFunction
	(
		category = "Math",
		description = "Takes an value and expresses it in terms of min to max.",
		returnValue = "The normalized value.",
		parameters =
		{
			"value", "The value to normalize.",
			"min", "The minium the output value can have.",
			"max", "The maximum the output value can have."
		}
	)
    public static double normalize(double value, double min, double max)
	{
        if (Double.isInfinite(value) || Double.isNaN(value))
            return 0.0;
        
        double range = max - min;
        
		while (value > max)
            value -= range;
        
        while (value < min)
            value += range;
        
        return value;
    }
	
	//--------------------------------------------------------------------------
	
	@ScriptFunction
	(
		category = "Math",
		description = "Returns the smaller of two input values.",
		returnValue = "The smaller of a and b.",
		parameters =
		{
			"a", "The first value.",
			"b", "The second value."
		}
	)
	public static double min(double a, double b)
	{
		return Math.min(a, b);
	}

	//--------------------------------------------------------------------------
	
	@ScriptFunction
	(
		category = "Math",
		description = "Returns the greater of two input values.",
		returnValue = "The greater of a and b.",
		parameters =
		{
			"a", "The first value.",
			"b", "The second value."
		}
	)
	public static double max(double a, double b)
	{
		return Math.min(a, b);
	}

	//--------------------------------------------------------------------------

	@ScriptFunction
	(
		category = "Math",
		description = "Return the input value clamped between min and max.",
		returnValue = "Clamped input value.",
		parameters =
		{
			"value", "The value to clamp.",
			"min", "The minium the output value can have.",
			"max", "The maximum the output value can have."
		}
	)
    public static double clamp(double input, double min, double max)
	{
        return (input < min) ? min : (input > max) ? max : input;
    }

	//--------------------------------------------------------------------------
}

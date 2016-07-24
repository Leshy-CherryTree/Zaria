/****************************************/
/* Math.java								*/
/* Created on: 24-07-2016				*/
/* Copyright Cherry Tree Studio 2016	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.utilities;

/**
 *
 * Based upon FastMath from the jMonkeyEngine
 */
public final class MathUtil
{
	//--------------------------------------------------------------------------

    /** The value PI as a float. (180 degrees) */
    public static final float PI = (float) Math.PI;
  
	/** The value 2PI as a float. (360 degrees) */
    public static final float TWO_PI = 2.0f * PI;
    
	/** The value PI/2 as a float. (90 degrees) */
    public static final float HALF_PI = 0.5f * PI;
    
	/** The value PI/4 as a float. (45 degrees) */
    public static final float QUARTER_PI = 0.25f * PI;
    
	/** The value 1/PI as a float. */
    public static final float INV_PI = 1.0f / PI;
    
	/** The value 1/(2PI) as a float. */
    public static final float INV_TWO_PI = 1.0f / TWO_PI;
	
	//--------------------------------------------------------------------------
	
	public static float toDeg(float rad)
	{
		return rad * 180.0f * INV_PI;
	}

	//--------------------------------------------------------------------------
	
	public static float toRad(float deg)
	{
		return deg * PI / 180.0f;
	}

	//--------------------------------------------------------------------------
	
	/**
	 * Returns true if the number is a power of 2 (2,4,8,16...)
	 *
	 * A good implementation found on the Java boards. note: a number is a power of two if and only if it is the
	 * smallest number with that number of significant bits. Therefore, if you subtract 1, you know that the new number
	 * will have fewer bits, so ANDing the original number with anything less than it will give 0.
	 *
	 * @param number The number to test.
	 * @return True if it is a power of two.
	 */
	public static boolean isPowerOfTwo(int number)
	{
		return (number > 0) && (number & (number - 1)) == 0;
	}
	
	//--------------------------------------------------------------------------

	/**
	 * Get the next power of two of the given number.
	 *
	 * E.g. for an input 100, this returns 128. Returns 1 for all numbers <= 1.
	 *
	 * @param number The number to obtain the POT for.
	 * @return The next power of two.
	 */
	public static int nearestPowerOfTwo(int number)
	{
		number--;
		number |= number >> 1;
		number |= number >> 2;
		number |= number >> 4;
		number |= number >> 8;
		number |= number >> 16;
		number++;
		number += (number == 0) ? 1 : 0;
		return number;
	}
	
	//--------------------------------------------------------------------------

	/**
	 * Linear interpolation from startValue to endValue by the given percent. Basically: ((1 - percent) * startValue) +
	 * (percent * endValue)
	 *
	 * @param scale scale value to use. if 1, use endValue, if 0, use startValue.
	 * @param startValue Beginning value. 0% of f
	 * @param endValue ending value. 100% of f
	 * @return The interpolated value between startValue and endValue.
	 */
	public static float interpolateLinear(float scale, float startValue, float endValue)
	{
		if (startValue == endValue)
			return startValue;
		if (scale <= 0f)
			return startValue;
		if (scale >= 1f)
			return endValue;

		return ((1f - scale) * startValue) + (scale * endValue);
	}
	
	//--------------------------------------------------------------------------

	/**
	 * Linear extrapolation from startValue to endValue by the given scale. if scale is between 0 and 1 this method
	 * returns the same result as interpolateLinear if the scale is over 1 the value is linearly extrapolated. Note that
	 * the end value is the value for a scale of 1.
	 *
	 * @param scale the scale for extrapolation
	 * @param startValue the starting value (scale = 0)
	 * @param endValue the end value (scale = 1)
	 * @return an extrapolation for the given parameters
	 */
	public static float extrapolateLinear(float scale, float startValue, float endValue)
	{
		return ((1f - scale) * startValue) + (scale * endValue);
	}

	//--------------------------------------------------------------------------
	
	/**
	 * Interpolate a spline between at least 4 control points following the Catmull-Rom equation. here is the
	 * interpolation matrix m = [ 0.0 1.0 0.0 0.0 ] [-T 0.0 T 0.0 ] [ 2T T-3 3-2T -T ] [-T 2-T T-2 T ] where T is the
	 * curve tension the result is a value between p1 and p2, t=0 for p1, t=1 for p2
	 *
	 * @param u value from 0 to 1
	 * @param T The tension of the curve
	 * @param p0 control point 0
	 * @param p1 control point 1
	 * @param p2 control point 2
	 * @param p3 control point 3
	 * @return Catmull–Rom interpolation
	 */
	public static float interpolateCatmullRom(float u, float T, float p0, float p1, float p2, float p3)
	{
		float c1, c2, c3, c4;
		c1 = p1;
		c2 = -1.0f * T * p0 + T * p2;
		c3 = 2 * T * p0 + (T - 3) * p1 + (3 - 2 * T) * p2 + -T * p3;
		c4 = -T * p0 + (2 - T) * p1 + (T - 2) * p2 + T * p3;

		return (float) (((c4 * u + c3) * u + c2) * u + c1);
	}
	
	//--------------------------------------------------------------------------

	/**
	 * Interpolate a spline between at least 4 control points following the Bezier equation. here is the interpolation
	 * matrix m = [ -1.0 3.0 -3.0 1.0 ] [ 3.0 -6.0 3.0 0.0 ] [ -3.0 3.0 0.0 0.0 ] [ 1.0 0.0 0.0 0.0 ] where T is the
	 * curve tension the result is a value between p1 and p3, t=0 for p1, t=1 for p3
	 *
	 * @param u value from 0 to 1
	 * @param p0 control point 0
	 * @param p1 control point 1
	 * @param p2 control point 2
	 * @param p3 control point 3
	 * @return Bezier interpolation
	 */
	public static float interpolateBezier(float u, float p0, float p1, float p2, float p3)
	{
		float oneMinusU = 1.0f - u;
		float oneMinusU2 = oneMinusU * oneMinusU;
		float u2 = u * u;
		
		return p0 * oneMinusU2 * oneMinusU + 3.0f * p1 * u * oneMinusU2 + 3.0f * p2 * u2 * oneMinusU + p3 * u2 * u;
	}
	
	//--------------------------------------------------------------------------

	/**
	 * Returns 1/sqrt(fValue)
	 *
	 * @param fValue The value to process.
	 * @return 1/sqrt(fValue)
	 * @see java.lang.Math#sqrt(double)
	 */
	public static float invSqrt(float fValue)
	{
		return (float) (1.0f / Math.sqrt(fValue));
	}
	
	//--------------------------------------------------------------------------

	public static float fastInvSqrt(float x)
	{
		float xhalf = 0.5f * x;
		int i = Float.floatToIntBits(x); // get bits for floating value
		
		i = 0x5f375a86 - (i >> 1); // gives initial guess y0
		x = Float.intBitsToFloat(i); // convert bits back to float
		x = x * (1.5f - xhalf * x * x); // Newton step, repeating increases accuracy
		
		return x;
	}

	//--------------------------------------------------------------------------

	/**
	 * Returns the logarithm of value with given base, calculated as log(value)/log(base), so that pow(base,
	 * return)==value (contributed by vear)
	 *
	 * @param value The value to log.
	 * @param base Base of logarithm.
	 * @return The logarithm of value with given base
	 */
	public static float log(float value, float base)
	{
		return (float) (Math.log(value) / Math.log(base));
	}

	//--------------------------------------------------------------------------

	/**
	 * Returns the value squared. fValue ^ 2
	 *
	 * @param fValue The value to square.
	 * @return The square of the given value.
	 */
	public static float sqr(float fValue)
	{
		return fValue * fValue;
	}
	
	//--------------------------------------------------------------------------

	/**
	 * Takes an value and expresses it in terms of min to max.
	 *
	 * @param val - the angle to normalize (in radians)
	 * @return the normalized angle (also in radians)
	 */
	public static float normalize(float val, float min, float max)
	{
		if (Float.isInfinite(val) || Float.isNaN(val))
			return 0f;
		
		float range = max - min;
		while (val > max)
			val -= range;

		while (val < min)
			val += range;

		return val;
	}
	
	//--------------------------------------------------------------------------

	/**
	 * @param x the value whose sign is to be adjusted.
	 * @param y the value whose sign is to be used.
	 * @return x with its sign changed to match the sign of y.
	 */
	public static float copysign(float x, float y)
	{
		if (y >= 0 && x <= -0)
			return -x;
		else if (y < 0 && x >= 0)
			return -x;
		else
			return x;
	}
	
	//--------------------------------------------------------------------------

	/**
	 * Determine if two floats are approximately equal. This takes into account the magnitude of the floats, since large
	 * numbers will have larger differences be close to each other.
	 *
	 * Should return true for a=100000, b=100001, but false for a=10000, b=10001.
	 *
	 * @param a The first float to compare
	 * @param b The second float to compare
	 * @return True if a and b are approximately equal, false otherwise.
	 */
	public static boolean approximateEquals(float a, float b)
	{
		if (a == b)
			return true;
		else
			return (Math.abs(a - b) / Math.max(Math.abs(a), Math.abs(b))) <= 0.00001f;
	}
	
	//--------------------------------------------------------------------------
}

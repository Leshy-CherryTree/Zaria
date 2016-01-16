/****************************************/
/* EndianConverter.java                 */
/* Created on: Feb 7, 2012              */
/* Copyright Cherry Tree Studio 2012	*/
/* Released under EUPL v1.1		*/
/* Ported from the jPAJ project.	*/
/****************************************/

package eu.cherrytree.zaria.file;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public class EndianConverter
{
    //--------------------------------------------------------------------------

    public static byte [] toByteArray(short value)
    {
	return new byte[]
	{
	    (byte)(value),
	    (byte)(value >>> 8),
	};
    }

    //--------------------------------------------------------------------------

    public static byte [] toByteArray(int value)
    {
        return new byte[]
	{
            (byte)(value),
            (byte)(value >>> 8),
            (byte)(value >>> 16),
            (byte)(value >>> 24)
        };
    }

    //--------------------------------------------------------------------------

    public static byte [] toByteArray(long value)
    {
        return new byte[]
	{
            (byte)(value),
            (byte)(value >>> 8),
            (byte)(value >>> 16),
            (byte)(value >>> 24),
            (byte)(value >>> 32),
            (byte)(value >>> 40),
            (byte)(value >>> 48),
            (byte)(value >>> 56)
        };
    }

    //--------------------------------------------------------------------------

    public static byte [] toByteArray(float value)
    {
    	return toByteArray(Float.floatToRawIntBits(value));
    }

    //--------------------------------------------------------------------------

    public static byte [] toByteArray(double value)
    {
    	return toByteArray(Double.doubleToRawLongBits(value));
    }

    //--------------------------------------------------------------------------

    public static short toShort(byte [] value)
    {
	if (value == null || value.length != 2)
	    return 0x0;

	return (short)((0xff & value[0]) << 0 | (0xff & value[1]) << 8);
    }

    //--------------------------------------------------------------------------

    public static int toInt(byte [] value)
    {
	if (value == null || value.length != 4)
	    return 0x0;

	return (int)(	(0xff & value[0]) << 0 | (0xff & value[1]) << 8 |
			(0xff & value[2]) << 16 | (0xff & value[3]) << 24 );
    }

    //--------------------------------------------------------------------------

    public static long toLong(byte [] value)
    {
	if (value == null || value.length != 8)
	    return 0x0;

	return (long)(	(0xff & value[0]) << 0 | (0xff & value[1]) << 8 |
			(0xff & value[2]) << 16 | (0xff & value[3]) << 24 |
			(0xff & value[4]) << 32 | (0xff & value[5]) << 40 |
			(0xff & value[6]) << 48 | (0xff & value[7]) << 56 );
    }

    //--------------------------------------------------------------------------

    public static float toFloat(byte[] value)
    {
	if (value == null || value.length != 4)
	    return 0x0;

	return Float.intBitsToFloat(toInt(value));
    }

    //--------------------------------------------------------------------------

    public static double toDouble(byte[] value)
    {
	if (value == null || value.length != 8)
	    return 0x0;

	return Double.longBitsToDouble(toLong(value));
    }

    //------------------------------------------------------------------------------
}
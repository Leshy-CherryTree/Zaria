/****************************************/
/* Random.java							*/
/* Created on: Feb 2, 2012				*/
/* Copyright Cherry Tree Studio 2012		*/
/* Released under EUPL v1.1				*/
/* Ported from the jPAJ project.			*/
/****************************************/

package eu.cherrytree.zaria.utilities;

import java.util.List;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public class Random
{
    //--------------------------------------------------------------------------

    private static boolean haveNextGaussian = false;
    private static float nextGaussian;

    private static MersenneTwisterFast rand;

    //--------------------------------------------------------------------------

	public static void init()
	{
		rand = new MersenneTwisterFast();
	}

	//--------------------------------------------------------------------------

	public static void init(long s)
	{
		rand = new MersenneTwisterFast(s);
	}

	//--------------------------------------------------------------------------

	public static void reSeed()
	{
		rand.setSeed(System.currentTimeMillis());
	}

	//--------------------------------------------------------------------------

	public static void reSeed(int s)
	{
		rand.setSeed(s);
	}

	//--------------------------------------------------------------------------

	public static int getInteger()
	{
		return rand.nextInt();
	}

	//--------------------------------------------------------------------------

	public static float getFloat()
	{
		return rand.nextFloat();
	}

    //--------------------------------------------------------------------------
	
	/**
	 * Simulates rolling of x amount of y side dice.
	 * 
	 * @param amount Amount of dice to be rolled.
	 * @param sides How many sides should the dice have.
	 * 
	 * @return Summed up value of all rolls.
	 */
	public static int rollDice(int amount, int sides)
	{
		assert sides > 1 && amount > 0;
		
		int value = 0;
		
		for(int i = 0 ; i < amount ; i++)
			value += (getInteger() % sides) + 1;
		
		return value;
	}
	
	//--------------------------------------------------------------------------

    /**
     * Returns a pseudorandom, Gaussian distributed float value with mean 0.0 and standard deviation 1.0.
     * Based on java.util.Random.nextFloat()
     * @return Pseudorandom, Gaussian ("normally") distributed double value with mean 0.0 and standard deviation 1.0.
     */
    public static float getGuassian()
    {
        if (haveNextGaussian)
        {
            haveNextGaussian = false;

            return nextGaussian;
        }
        else
        {
            float v1, v2, s;

            do
            {
                v1 = 2 * getFloat() - 1;
                v2 = 2 * getFloat() - 1;
                s = v1 * v1 + v2 * v2;
            }
            while (s >= 1 || s == 0);

            float multiplier = (float) Math.sqrt(-2 * Math.log(s)/s);

            nextGaussian = v2 * multiplier;
            haveNextGaussian = true;

            return v1 * multiplier;
        }
    }

    //--------------------------------------------------------------------------

    /**
     * Returns a pseudorandom, Gaussian distributed float value based on given standard deviation and mean value (peak).
     * @param deviation Standard deviation.
     * @param peak Mean value.
     * @return Pseudorandom, uniformly distributed float value.
     */
    public static float getGuassian(float deviation, float peak)
    {
        return getGuassian() * deviation + peak;
    }

	//--------------------------------------------------------------------------
	
	 public static <T> T getItem(List<T> items)
	 {
		 if(items.isEmpty())
			 return null;
		 
		 int index = Math.abs(getInteger() % items.size());
		 return items.get(index);
	 }
	
	//--------------------------------------------------------------------------
}

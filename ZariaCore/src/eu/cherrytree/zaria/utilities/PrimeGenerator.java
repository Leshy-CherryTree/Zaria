/****************************************/
/* PrimeGenerator.java					*/
/* Created on: 06-Oct-2014				*/
/* Copyright Cherry Tree Studio 2014	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.utilities;

/**
 * Uses the Sieve of Atkin algorithm to generate prime numbers.
 * Based upon public domain code from StackOveflow.
 * 
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public class PrimeGenerator
{
	//--------------------------------------------------------------------------
	
	private static final int limit = 25000000;
	
	//--------------------------------------------------------------------------
	
	private static int[] primes;	
	
	//--------------------------------------------------------------------------

	public static int[] getPrimes()
	{
		if(primes == null)
			init(1000000);
					
		return primes;
	}
	
	//--------------------------------------------------------------------------

	public static void init(int amount)
	{	
		assert amount <= 1565927 : "With the current settings max 1565927 primes can be generated, " + amount + " is too many";
		
		primes = new int[amount];
		
		boolean[] sieve = new boolean[limit + 1];
		int limitSqrt = (int) Math.sqrt((double) limit);
		
		sieve[0] = false;
		sieve[1] = false;
		sieve[2] = true;
		sieve[3] = true;
		
		assert sieve[4] == false;

		for (int x = 1 ; x <= limitSqrt ; x++)
		{
			for (int y = 1 ; y <= limitSqrt ; y++)
			{
				int n = (4 * x * x) + (y * y);
				
				if (n <= limit && (n % 12 == 1 || n % 12 == 5))
					sieve[n] = !sieve[n];

				n = (3 * x * x) + (y * y);
				
				if (n <= limit && (n % 12 == 7))
					sieve[n] = !sieve[n];

				n = (3 * x * x) - (y * y);
				
				if (x > y && n <= limit && (n % 12 == 11))
					sieve[n] = !sieve[n];
			}
		}

		for (int n = 5 ; n <= limitSqrt ; n++)
		{
			if (sieve[n])
			{
				int x = n * n;
				for (int i = x ; i <= limit ; i += x)
					sieve[i] = false;
			}
		}
		
		int index = 0;
		
		for (int i = limit; i >= 0; i--)
		{
			if (sieve[i])
			{
				primes[index] = i;
				index++;				
				
				if(index == primes.length)
					return;
			}
		}
	}
	
	//--------------------------------------------------------------------------
}
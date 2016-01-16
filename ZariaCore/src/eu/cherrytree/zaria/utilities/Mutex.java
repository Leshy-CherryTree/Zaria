/****************************************/
/* Mutex.java							*/
/* Created on: 25-Jan-2013				*/
/* Copyright Cherry Tree Studio 2013	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.utilities;

import eu.cherrytree.zaria.base.ApplicationRuntimeError;
import eu.cherrytree.zaria.debug.DebugManager;


/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public class Mutex
{
	//--------------------------------------------------------------------------
	
	private String name;
	private long time = -1;
	
	//--------------------------------------------------------------------------

	public Mutex(String name)
	{
		this.name = name;
	}
	
	//--------------------------------------------------------------------------
		
	public synchronized void lock()
	{
		DebugManager.trace("Locking mutex " + name + ".", DebugManager.TraceLevel.DETAILS);
		
		try
		{			
			wait();
			time = System.currentTimeMillis();
		}
		catch (InterruptedException ex)
		{
			ApplicationRuntimeError error = new ApplicationRuntimeError("Mutex interrupted!", ex);
			error.addInfo("Name", name);
			throw error;			
		}
	}
	
	//--------------------------------------------------------------------------
	
	public synchronized void unlock()
	{
		notifyAll();
		
		if(time > 0)
		{
			time = System.currentTimeMillis() - time;
			DebugManager.trace("Unlocking mutex " + name + " after " + time + " ms.", DebugManager.TraceLevel.DETAILS);
			time = -1;
		}
	}
	
	//--------------------------------------------------------------------------
}

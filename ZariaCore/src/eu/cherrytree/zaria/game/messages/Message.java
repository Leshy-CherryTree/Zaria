/****************************************/
/* Message.java							*/
/* Created on: 07-Jan-2014				*/
/* Copyright Cherry Tree Studio 2014	*/
/* Released under EUPL v1.1				*/
/****************************************/


package eu.cherrytree.zaria.game.messages;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public abstract class Message
{
	//--------------------------------------------------------------------------
	
	private boolean continueProcessing = true;
	
	//--------------------------------------------------------------------------

	public final boolean isContinueProcessing()
	{
		return continueProcessing;
	}
	
	//--------------------------------------------------------------------------
	
	/** Call when the message is handled and no other changes would be needed/valid. */
	public final void cancelProcessing()
	{
		continueProcessing = false;
	}
	
	//--------------------------------------------------------------------------
	
	public void startedProcessing()
	{
		// Intentionally empty.
	}
	
	//--------------------------------------------------------------------------
	
	public final void finishedProcessing()
	{
		continueProcessing = true;
	}
	
	//--------------------------------------------------------------------------
}

/****************************************/
/* MessageHandler.java					*/
/* Created on: 12-Jan-2014				*/
/* Copyright Cherry Tree Studio 2014	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.game.messages;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public interface MessageHandler
{
	public Class<? extends Message> [] getHandledMessageTypes();
	public void handleMessage(Message message);
}

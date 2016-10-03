/****************************************/
/* SyncInterface.java					*/
/* Created on: 04-10-2016				*/
/* Copyright Cherry Tree Studio 2016	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.debug;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public interface SyncInterface extends Remote
{
	public void sync(String zoneSource) throws RemoteException;
}

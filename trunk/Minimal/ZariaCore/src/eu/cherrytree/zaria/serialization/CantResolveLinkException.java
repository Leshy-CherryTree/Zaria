/****************************************/
/* CantResolveLinkException.java 		*/
/* Created on: 05-Jul-2014				*/
/* Copyright Cherry Tree Studio 2014	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.serialization;

import java.util.UUID;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public class CantResolveLinkException extends Exception
{
	//--------------------------------------------------------------------------
	
	public CantResolveLinkException(UUID uuid, String libraryId)
	{
		super("Can't find link " + uuid + " in library " + libraryId);
	}
	
	//--------------------------------------------------------------------------
	
	public CantResolveLinkException(String uuidsString, String libraryId)
	{
		super("Can't find link " + uuidsString + " in library " + libraryId);
	}
	
	//--------------------------------------------------------------------------
}

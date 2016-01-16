/****************************************/
/* ValueAlreadySetException.java 		*/
/* Created on: 15-Jun-2014				*/
/* Copyright Cherry Tree Studio 2014	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.serialization;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public class ValueAlreadySetException extends Exception
{
	public ValueAlreadySetException(String name)
	{
		super("Value with name \"" + name + "\" is already added to the capsule.");
	}		
}

/****************************************/
/* SaveCapsule.java						*/
/* Created on: 18-Mar-2013				*/
/* Copyright Cherry Tree Studio 2013		*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.serialization;

import java.io.Serializable;
import java.util.UUID;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public interface SaveCapsule extends Serializable
{
	//--------------------------------------------------------------------------
	
    public void write(String name, byte value) throws ValueAlreadySetException;
	public void write(String name, boolean value) throws ValueAlreadySetException;
	public void write(String name, short value) throws ValueAlreadySetException;
    public void write(String name, int value) throws ValueAlreadySetException;
	public void write(String name, long value) throws ValueAlreadySetException;
    public void write(String name, float value) throws ValueAlreadySetException;
    public void write(String name, double value) throws ValueAlreadySetException;
	public void write(String name, char value) throws ValueAlreadySetException;
    public void write(String name, String value) throws ValueAlreadySetException;
	public void write(String name, UUID value) throws ValueAlreadySetException;
    public void writeObject(String name, Serializable value) throws ValueAlreadySetException;
	public void writeCapsule(String name, SaveCapsule value) throws ValueAlreadySetException;
	public SaveCapsule createCapsule(String name) throws ValueAlreadySetException;
	
	public boolean isEmpty();
	
	//--------------------------------------------------------------------------
}

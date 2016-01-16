/****************************************/
/* LoadCapsule.java						*/
/* Created on: 18-Mar-2013				*/
/* Copyright Cherry Tree Studio 2013	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.serialization;

import java.io.Serializable;
import java.util.UUID;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public interface LoadCapsule extends Serializable
{
	//--------------------------------------------------------------------------
	
    public byte readByte(String name, byte defaultValue);
	public boolean readBoolean(String name, boolean defaultValue);
	public short readShort(String name, short defaulValue);
    public int readInt(String name, int defaultValue);
	public long readLong(String name, long defaultValue);
    public float readFloat(String name, float defaultValue) ;
    public double readDouble(String name, double defaultValue);
	public char readChar(String name, char defaultValue);
    public String readString(String name, String defaultValue);	
	public UUID readUUID(String name, UUID defaultValue);
	public Serializable readObject(String name);
	public LoadCapsule readCapsule(String name);
	
	public boolean isEmpty();
	
	//--------------------------------------------------------------------------
}

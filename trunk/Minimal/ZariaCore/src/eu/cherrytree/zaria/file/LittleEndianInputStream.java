/****************************************/
/* LittleEndianInputStream.java         */
/* Created on: Feb 7, 2012              */
/* Copyright Cherry Tree Studio 2012	*/
/* Released under EUPL v1.1		*/
/****************************************/

package eu.cherrytree.zaria.file;

import java.io.IOException;
import java.io.InputStream;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public class LittleEndianInputStream
{
    //--------------------------------------------------------------------------

    private InputStream stream;

    //--------------------------------------------------------------------------

    public LittleEndianInputStream(InputStream in)
    {
	this.stream = in;
    }

    //--------------------------------------------------------------------------

    public byte readByte() throws IOException
    {
	byte [] r = new byte[1];
        stream.read(r);
        return r[0];
    }

    //--------------------------------------------------------------------------

    public void readBytes(byte [] bytes) throws IOException
    {
        stream.read(bytes);
    }

    //--------------------------------------------------------------------------

    public short readShort() throws IOException
    {
        byte [] r = new byte[2];
        stream.read(r);
        return EndianConverter.toShort(r);
    }

    //--------------------------------------------------------------------------

    public int readInt() throws IOException
    {
        byte [] r = new byte[4];
        stream.read(r);
        return EndianConverter.toInt(r);
    }

    //--------------------------------------------------------------------------

    public long readLong() throws IOException
    {
        byte [] r = new byte[8];
        stream.read(r);
        return EndianConverter.toLong(r);
    }

    //--------------------------------------------------------------------------

    public float readFloat() throws IOException
    {
        byte [] r = new byte[4];
        stream.read(r);
        return EndianConverter.toFloat(r);
    }

    //--------------------------------------------------------------------------

    public double readDouble() throws IOException
    {
        byte [] r = new byte[8];
        stream.read(r);
        return EndianConverter.toDouble(r);
    }

    //--------------------------------------------------------------------------

    public void close() throws IOException
    {
        stream.close();
    }

    //--------------------------------------------------------------------------

    public InputStream getInputStream()
    {
    	return stream;
    }

    //--------------------------------------------------------------------------
}

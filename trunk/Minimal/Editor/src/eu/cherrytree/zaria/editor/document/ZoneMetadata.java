/****************************************/
/* NodeMetadata.java						*/
/* Created on: 07-Jul-2013				*/
/* Copyright Cherry Tree Studio 2013		*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.editor.document;


import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
@XmlRootElement(name = "metadata")
public class ZoneMetadata
{
	//--------------------------------------------------------------------------

	private int locX;
	private int locY;
	
    private boolean folded;

	//--------------------------------------------------------------------------

	public int getLocX()
	{
		return locX;
	}

	//--------------------------------------------------------------------------

	public int getLocY()
	{
		return locY;
	}

	//--------------------------------------------------------------------------

	public void setLocX(int locX)
	{
		this.locX = locX;
	}

	//--------------------------------------------------------------------------

	public void setLocY(int locY)
	{
		this.locY = locY;
	}
	
	//--------------------------------------------------------------------------
	
	public boolean isFolded()
	{
		return folded;
	}
	
	//--------------------------------------------------------------------------

	public void setFolded(boolean folded)
	{
		this.folded = folded;
	}
	
	//--------------------------------------------------------------------------
}

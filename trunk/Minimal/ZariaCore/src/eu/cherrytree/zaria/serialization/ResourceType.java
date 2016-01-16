/****************************************/
/* ResourceType.java					*/
/* Created on: 11-May-2013				*/
/* Copyright Cherry Tree Studio 2013	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.serialization;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public enum ResourceType
{
	//--------------------------------------------------------------------------
	
	TEXTURE				("png","jpg","jpeg","gif","dds"),
	AUDIO				("wav","mp3","ogg"),	
	FONT				("fnt");
	
	//--------------------------------------------------------------------------
	
	private String [] extensions;
	
	//--------------------------------------------------------------------------

	private ResourceType(String ... extensions)
	{
		this.extensions = extensions;
	}

	//--------------------------------------------------------------------------

	public String[] getExtensions()
	{
		return extensions;
	}
	
	//--------------------------------------------------------------------------
	
	public String getSimpleName()
	{
		String ret = super.toString().toLowerCase().replace("_", " ");
		ret = ret.substring(0, 1).toUpperCase() + ret.substring(1);
		return ret;
	}
	
	//--------------------------------------------------------------------------
	
	@Override
	public String toString()
	{
		String ret = getSimpleName();
		ret += " (";
		
		for(int i = 0 ; i < extensions.length ; i++)
		{
			ret += "." + extensions[i];
			
			if(i < extensions.length-1)
				ret += ", ";
		}
		
		ret += ")";
		
		return ret;
	}
	
	//--------------------------------------------------------------------------
}

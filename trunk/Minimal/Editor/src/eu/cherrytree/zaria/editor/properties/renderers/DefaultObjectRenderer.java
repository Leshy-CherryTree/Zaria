/****************************************/
/* DefaultObjectRenderer.java			*/
/* Created on: 13-Oct-2013				*/
/* Copyright Cherry Tree Studio 2013	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.editor.properties.renderers;

/** 
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 * 
 * Based upon the unmaintained code from L2FProd.com.
 */
public class DefaultObjectRenderer
{
	//--------------------------------------------------------------------------

	public String getText(Object object)
	{
		if(object == null)
			return null;

		return object.toString();
	}

	//--------------------------------------------------------------------------
}

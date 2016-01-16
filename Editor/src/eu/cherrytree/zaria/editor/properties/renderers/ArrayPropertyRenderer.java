/****************************************/
/* ArrayPropertyRenderer.java           */
/* Created on: 22-Jul-2013				*/
/* Copyright Cherry Tree Studio 2013	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.editor.properties.renderers;

import java.lang.reflect.Array;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public class ArrayPropertyRenderer extends DefaultCellRenderer
{
	//--------------------------------------------------------------------------
	
	private static final long serialVersionUID = 1L;
	
	//--------------------------------------------------------------------------
	
	private String typeName;

	//--------------------------------------------------------------------------

	public ArrayPropertyRenderer(String typeName)
	{
		assert typeName != null;
		
		this.typeName = typeName;
	}
			
	//--------------------------------------------------------------------------

	@Override
	protected String convertToString(Object value)
	{		
		assert value != null;
		
		return typeName + "[" + Array.getLength(value) + "]";
	}

	//--------------------------------------------------------------------------
}

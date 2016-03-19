/****************************************/
/* ZoneGraphPort.java					*/
/* Created on: 20-Apr-2014				*/
/* Copyright Cherry Tree Studio 2014	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.editor.graph;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.util.mxPoint;
import eu.cherrytree.zaria.serialization.ZariaObjectDefinition;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public class ZoneGraphOutputPort extends ZoneGraphPort
{
	//--------------------------------------------------------------------------
	
	private static final long serialVersionUID = 1L;
	
	//--------------------------------------------------------------------------
			
	public ZoneGraphOutputPort(Class<? extends ZariaObjectDefinition> defintionClass, float width, float height)
	{
		super(defintionClass, defintionClass.getSimpleName(), defintionClass.getSimpleName());
		
		mxGeometry geo = new mxGeometry(0.0f, 0.0f, width, height);
		geo.setOffset(new mxPoint(0.0f, 0.0f));
		geo.setRelative(true);

		setGeometry(geo);
		setValue(defintionClass.getSimpleName());
						
		setStyle("fontFamily=" + nodeMetrics.getFont().getFamily() + ";" + "fontSize=" +  nodeMetrics.getFont().getSize() + ";" + "fontColor=#ffffff;verticalAlign=top;strokeWidth=1;strokeColor=#707070;fillColor=#000000");
		setVertex(true);
	}

	//--------------------------------------------------------------------------
	
	@Override
	public boolean isInput()
	{
		return false;
	}

	//--------------------------------------------------------------------------
	
	@Override
	public boolean isArray()
	{
		return false;
	}

	//--------------------------------------------------------------------------	
}
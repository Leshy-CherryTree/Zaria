/****************************************/
/* ZoneGraphPort.java					*/
/* Created on: 20-Apr-2014				*/
/* Copyright Cherry Tree Studio 2014		*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.editor.graph;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.util.mxPoint;

import eu.cherrytree.zaria.serialization.ZariaObjectDefinition;
import java.util.ArrayList;


/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public class ZoneGraphInputPort extends ZoneGraphPort
{
	//--------------------------------------------------------------------------
	
	private static final long serialVersionUID = 1L;	
	private static final float size = 4.0f;
	
	//--------------------------------------------------------------------------
	
	public static class ExternalMarker extends mxCell
	{
		ArrayList<LinkInfo> links = new ArrayList<>();

		public ExternalMarker(Class<? extends ZariaObjectDefinition> defintionClass)
		{
			mxGeometry ex_geo = new mxGeometry(0.0f, 0.0f, size * 3.0f, size * 1.5f);
			ex_geo.setOffset(new mxPoint(0.0f, size * 2.0f));
			ex_geo.setRelative(true);

			setGeometry(ex_geo);
			setStyle("handleBorderColor=black;labelPosition=left;fontFamily=" + externalMetrics.getFont().getFamily() + ";" + "fontSize=" +  externalMetrics.getFont().getSize() + ";" + ";align=right;verticalAlign=top;fontColor=#000000;strokeColor=#777777;fillColor=" + getColorString(defintionClass, 0.1f) + ";gradientColor=" + getColorString(defintionClass, -0.3f));
			setVertex(true);
			setConnectable(false);
		}

		public ArrayList<LinkInfo> getLinks()
		{
			return links;
		}
			
	}
	
	//--------------------------------------------------------------------------
	
	private ExternalMarker external;
	private boolean array;
	
	//--------------------------------------------------------------------------
			
	public ZoneGraphInputPort(Class<? extends ZariaObjectDefinition> defintionClass, String name, String displayName, boolean array, float offset)
	{
		super(defintionClass, name, displayName);
		this.displayName = this.displayName.toLowerCase();
		
		this.array = array;
		
		setValue(this.displayName);
		
		mxGeometry geo = new mxGeometry(1.0f, offset, size * 3.0f, size * 2.0f);
		geo.setOffset(new mxPoint(-size * 1.5f, 9.0f));
		geo.setRelative(true);
		
		setGeometry(geo);		
		
		setStyle("handleBorderColor=black;labelPosition=left;fontFamily=" + portMetrics.getFont().getFamily() + ";" + "fontSize=" +  portMetrics.getFont().getSize() + ";" + ";align=right;verticalAlign=middle;fontColor=#000000;strokeColor=#777777;fillColor=" + getColorString(defintionClass, 0.1f) + ";gradientColor=" + getColorString(defintionClass,-0.1f));
					
		setVertex(true);
		
		external = new ExternalMarker(defintionClass);
	}
		
	//--------------------------------------------------------------------------
	
	@Override
	public void update(ZoneGraph graph)
	{
		external.getLinks().clear();
				
		for(LinkInfo info : getLinks())
		{
			if(info.isExternal())
				external.getLinks().add(info);
		}
		
		if(!external.getLinks().isEmpty())
			graph.addCell(external, this);
		else
			graph.removeCells(new Object[]{external});								
	}	

	//--------------------------------------------------------------------------
	
	@Override
	public boolean isInput()
	{
		return true;
	}

	//--------------------------------------------------------------------------
	
	@Override
	public boolean isArray()
	{
		return array;
	}

	//--------------------------------------------------------------------------	
}

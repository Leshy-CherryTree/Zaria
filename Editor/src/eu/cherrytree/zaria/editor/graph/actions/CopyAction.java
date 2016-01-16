/****************************************/
/* CopyAction.java						*/
/* Created on: 25-Jul-2013				*/
/* Copyright Cherry Tree Studio 2013	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.editor.graph.actions;

import eu.cherrytree.zaria.editor.document.GraphEditorState;
import eu.cherrytree.zaria.editor.graph.ZoneGraph;
import eu.cherrytree.zaria.editor.graph.ZoneGraphNode;
import java.awt.event.ActionEvent;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public class CopyAction extends ZoneGraphAction
{
	//--------------------------------------------------------------------------
	
	private static final long serialVersionUID = 1L;
	
	//--------------------------------------------------------------------------
	
	public static void perform(ZoneGraph graph)
	{
		if(graph != null)
		{
			Object [] objects = graph.getSelectionCells();
			
			int counter = 0;
			
			for(Object obj : objects)
			{
				if(obj instanceof ZoneGraphNode)
					counter++;
			}
				
			if(counter > 0)
			{
				ZoneGraphNode [] nodes = new ZoneGraphNode[counter];
				
				counter = 0;
			
				for(Object obj : objects)
				{
					if(obj instanceof ZoneGraphNode)
					{
						nodes[counter] = (ZoneGraphNode) obj;
						counter++;
					}
				}

				GraphEditorState.getGraphClipboard().setNodes(nodes);
			}
		}
	}
	
	//--------------------------------------------------------------------------
	
	@Override
	public void actionPerformed(ActionEvent event)
	{
		perform(getGraph(event));
	}
	
	//--------------------------------------------------------------------------
}

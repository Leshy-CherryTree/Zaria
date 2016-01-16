/****************************************/
/* CutAction.java						*/
/* Created on: 24-Jul-2013				*/
/* Copyright Cherry Tree Studio 2013	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.editor.graph.actions;

import eu.cherrytree.zaria.editor.graph.ZoneGraph;

import java.awt.event.ActionEvent;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public class CutAction extends ZoneGraphAction
{
	//--------------------------------------------------------------------------
	
	private static final long serialVersionUID = 1L;
	
	//--------------------------------------------------------------------------
	
	public static void perform(ZoneGraph graph)
	{
		CopyAction.perform(graph);
		
		if(graph != null)
			graph.removeCells();
	}
	
	//--------------------------------------------------------------------------
	
	@Override
	public void actionPerformed(ActionEvent event)
	{
		perform(getGraph(event));
	}
	
	//--------------------------------------------------------------------------
}

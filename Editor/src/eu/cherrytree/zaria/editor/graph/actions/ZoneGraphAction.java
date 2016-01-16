/****************************************/
/* ZoneGraphAction.java                */
/* Created on: 24-Jul-2013				*/
/* Copyright Cherry Tree Studio 2013	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.editor.graph.actions;

import eu.cherrytree.zaria.editor.document.GraphEditorState;
import eu.cherrytree.zaria.editor.graph.ZoneGraph;
import eu.cherrytree.zaria.editor.graph.ZoneGraphComponent;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public abstract class ZoneGraphAction extends AbstractAction
{
	//--------------------------------------------------------------------------

	protected ZoneGraph getGraph(ActionEvent e)
	{
		Object source = e.getSource();

		if (source instanceof ZoneGraphComponent)
			return (ZoneGraph) ((ZoneGraphComponent) source).getGraph();

		return null;
	}
	
	//--------------------------------------------------------------------------
	
	protected ZoneGraphComponent getComponent(ActionEvent e)
	{
		Object source = e.getSource();

		if (source instanceof ZoneGraphComponent)
			return ((ZoneGraphComponent) source);

		return null;
	}

	//--------------------------------------------------------------------------
	
	protected GraphEditorState getEditorState(ActionEvent e)
	{
		Object source = e.getSource();

		if (source instanceof ZoneGraphComponent)
			return ((ZoneGraphComponent) source).getEditorState();

		return null;	
	}
	
	//--------------------------------------------------------------------------
}

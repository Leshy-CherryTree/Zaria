/****************************************/
/* ConnectUndoAction.java				*/
/* Created on: 20-Aug-2014				*/
/* Copyright Cherry Tree Studio 2014	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.editor.graph.undo;

import eu.cherrytree.zaria.editor.document.GraphEditorState;
import eu.cherrytree.zaria.editor.graph.ZoneGraphInputPort;
import eu.cherrytree.zaria.editor.graph.ZoneGraphOutputPort;
import eu.cherrytree.zaria.editor.undo.UndoAction;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public class ConnectUndoAction implements UndoAction<GraphEditorState>
{
	//--------------------------------------------------------------------------
	
	private ZoneGraphInputPort inputPort;
	private ZoneGraphOutputPort outputPort;
	
	//--------------------------------------------------------------------------

	public ConnectUndoAction(ZoneGraphInputPort inputPort, ZoneGraphOutputPort outputPort)
	{
		this.inputPort = inputPort;
		this.outputPort = outputPort;
	}
	
	//--------------------------------------------------------------------------

	@Override
	public void run(GraphEditorState target, Type type)
	{
		target.undoConnect(inputPort, outputPort, type);
	}
	
	//--------------------------------------------------------------------------
}

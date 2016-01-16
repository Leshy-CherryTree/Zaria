/****************************************/
/* AddUndoAction.java					*/
/* Created on: 20-Aug-2014				*/
/* Copyright Cherry Tree Studio 2014	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.editor.graph.undo;

import eu.cherrytree.zaria.editor.document.GraphEditorState;
import eu.cherrytree.zaria.editor.graph.ZoneGraphNode;
import eu.cherrytree.zaria.editor.undo.UndoAction;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public class AddUndoAction implements UndoAction<GraphEditorState>
{
	//--------------------------------------------------------------------------
	
	private ZoneGraphNode node;
	
	//--------------------------------------------------------------------------

	public AddUndoAction(ZoneGraphNode node)
	{
		this.node = node;
	}
	
	//--------------------------------------------------------------------------

	@Override
	public void run(GraphEditorState target, Type type)
	{
		target.undoAdd(node, type);
	}
	
	//--------------------------------------------------------------------------
}
/****************************************/
/* MoveUndoAction.java					*/
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
public class MoveUndoAction implements UndoAction<GraphEditorState>
{
	//--------------------------------------------------------------------------
	
	private ZoneGraphNode node;
	private float x,y;

	//--------------------------------------------------------------------------
	
	public MoveUndoAction(ZoneGraphNode node, float x, float y)
	{
		this.node = node;
		this.x = x;
		this.y = y;
	}

	//--------------------------------------------------------------------------

	@Override
	public void run(GraphEditorState target, Type type)
	{
		target.undoMove(node, x, y, type);
	}
	
	//--------------------------------------------------------------------------
}
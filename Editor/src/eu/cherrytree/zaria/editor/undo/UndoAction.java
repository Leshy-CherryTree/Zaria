/****************************************/
/* UndoAction.java						*/
/* Created on: 02-Dec-2013				*/
/* Copyright Cherry Tree Studio 2013	*/
/* Released under EUPL v1.1				*/
/****************************************/


package eu.cherrytree.zaria.editor.undo;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public interface UndoAction<T extends UndoTarget>
{
	//--------------------------------------------------------------------------
	
	public enum Type
	{
		UNDO,
		REDO
	}
	
	//--------------------------------------------------------------------------
	
	public void run(T target, Type type);
	
	//--------------------------------------------------------------------------
}

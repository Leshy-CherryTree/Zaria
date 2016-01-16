/****************************************/
/* UndoStack.java						*/
/* Created on: 02-Dec-2013				*/
/* Copyright Cherry Tree Studio 2013	*/
/* Released under EUPL v1.1				*/
/****************************************/


package eu.cherrytree.zaria.editor.undo;

import java.util.Stack;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public class UndoStack<T extends UndoTarget>
{
	//--------------------------------------------------------------------------
	
	private T target;
	
	private Stack<UndoAction> undoActions = new Stack<>();
	private Stack<UndoAction> redoActions = new Stack<>();
	
	private boolean locked = false;
	
	//--------------------------------------------------------------------------
	
	public UndoStack(T target)
	{
		this.target = target;
	}
			
	//--------------------------------------------------------------------------
	
	public int undoSize()
	{
		return undoActions.size();
	}
	
	//--------------------------------------------------------------------------
	
	public int redoSize()
	{
		return redoActions.size();
	}
	
	//--------------------------------------------------------------------------
	
	public void undo()
	{
		assert !locked;
		
		if(undoActions.size() > 0 && !target.isDirty())
			undoActions.pop().run(target, UndoAction.Type.UNDO);
	}
	
	//--------------------------------------------------------------------------
	
	public void redo()
	{
		assert !locked;
		
		if(redoActions.size() > 0 && !target.isDirty())
			redoActions.pop().run(target, UndoAction.Type.REDO);
	}
	
	//--------------------------------------------------------------------------
	
	public void addUndoAction(UndoAction undoAction, boolean clearRedo)
	{
		assert !locked;
		
		undoActions.push(undoAction);
		
		if(clearRedo)
			redoActions.clear();
	}
	
	//--------------------------------------------------------------------------
	
	public void addRedoAction(UndoAction redoAction)
	{		
		assert !locked;
		
		redoActions.push(redoAction);		
	}
	
	//--------------------------------------------------------------------------
	
	public void clear()
	{
		assert !locked;
		
		undoActions.clear();
		redoActions.clear();		
	}
	
	//--------------------------------------------------------------------------
	
	public boolean isLocked()
	{
		return locked;
	}
	
	//--------------------------------------------------------------------------
	
	public void lock()
	{
		assert !locked;
		
		locked = true;
	}
	
	//--------------------------------------------------------------------------
	
	public void unlock()
	{
		assert locked;
		
		locked = false;
	}
	
	//--------------------------------------------------------------------------
}

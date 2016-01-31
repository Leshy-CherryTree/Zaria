/****************************************/
/* FindOptions.java						*/
/* Created on: 05-May-2013				*/
/* Copyright Cherry Tree Studio 2013	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.editor.document;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public class FindOptions
{
	//--------------------------------------------------------------------------
	
	public enum Direction
	{
		FORWARD,
		BACKWARD
	}
	
	//--------------------------------------------------------------------------
	
	public enum Scope
	{
		ALL,
		SELECTION
	}
	
	//--------------------------------------------------------------------------
	
	private Direction direction;
	private Scope scope;
	
	private boolean caseSensitive;
	private boolean wholeWord;
	private boolean wrap;
	private boolean mark;
	private boolean inSelection;
	
	//--------------------------------------------------------------------------
	
	public FindOptions(Direction direction, Scope scope, boolean caseSensitive, boolean wholeWord, boolean wrap, boolean mark, boolean inSelection)
	{
		this.direction = direction;
		this.scope = scope;
		this.caseSensitive = caseSensitive;
		this.wholeWord = wholeWord;
		this.wrap = wrap;
		this.mark = mark;
		this.inSelection = inSelection;
	}
	
	//--------------------------------------------------------------------------
	
	public Direction getDirection()
	{
		return direction;
	}

	//--------------------------------------------------------------------------

	public Scope getScope()
	{
		return scope;
	}

	//--------------------------------------------------------------------------

	public boolean isCaseSensitive()
	{
		return caseSensitive;
	}

	//--------------------------------------------------------------------------

	public boolean isWholeWord()
	{
		return wholeWord;
	}

	//--------------------------------------------------------------------------

	public boolean isWrap()
	{
		return wrap;
	}

	//--------------------------------------------------------------------------
	
	public boolean isMark()
	{
		return mark;
	}
	
	//--------------------------------------------------------------------------
	
	public boolean isInSelection()
	{
		return inSelection;
	}
	
	//--------------------------------------------------------------------------
}

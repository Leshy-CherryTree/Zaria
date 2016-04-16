/****************************************/
/* BasicCompletion.java					*/
/* Created on: 17-04-2016				*/
/* Copyright Cherry Tree Studio 2016	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.editor.document.autocomplete;

import eu.cherrytree.zaria.editor.classlist.ZoneScriptFunction;
import org.fife.ui.autocomplete.AbstractCompletion;
import org.fife.ui.autocomplete.CompletionProvider;


public class ScriptFunctionCompletion extends AbstractCompletion
{	
	//--------------------------------------------------------------------------
	
	private ZoneScriptFunction function;
	
	//--------------------------------------------------------------------------

	public ScriptFunctionCompletion(CompletionProvider provider, ZoneScriptFunction function)
	{
		super(provider);
		
		this.function = function;
	}
	
	//--------------------------------------------------------------------------

	@Override
	public String getToolTipText()
	{
		return function.getToolTip();
	}
	
	//--------------------------------------------------------------------------

	@Override
	public String getInputText()
	{
		return function.getName();
	}
	
	//--------------------------------------------------------------------------
	

	@Override
	public String getReplacementText()
	{
		return function.getName() + "(";
	}
	
	//--------------------------------------------------------------------------

	@Override
	public String getSummary()
	{
		return function.getDetails();
	}
	
	//--------------------------------------------------------------------------

	@Override
	public String toString()
	{
		return function.getName() + " - " + function.getSignature() + " [" + function.getCategory() + "]";
	}

	//--------------------------------------------------------------------------
}
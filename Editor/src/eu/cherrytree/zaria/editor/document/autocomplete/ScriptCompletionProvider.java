/****************************************/
/* ScriptCompletionProvider.java			*/
/* Created on: 16-04-2016				*/
/* Copyright Cherry Tree Studio 2016	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.editor.document.autocomplete;

import eu.cherrytree.zaria.editor.classlist.ZoneClassList;
import eu.cherrytree.zaria.editor.classlist.ZoneScriptMethod;
import eu.cherrytree.zaria.editor.classlist.ZoneScriptStaticFunction;

import org.fife.ui.autocomplete.BasicCompletion;
import org.fife.ui.autocomplete.DefaultCompletionProvider;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public class ScriptCompletionProvider extends DefaultCompletionProvider
{
	//--------------------------------------------------------------------------

	public ScriptCompletionProvider(ZoneClassList classList)
	{		
		addCompletion(new BasicCompletion(this, "break"));
		addCompletion(new BasicCompletion(this, "case"));
		addCompletion(new BasicCompletion(this, "catch"));
		addCompletion(new BasicCompletion(this, "continue")); 
		addCompletion(new BasicCompletion(this, "default"));
		addCompletion(new BasicCompletion(this, "delete"));
		addCompletion(new BasicCompletion(this, "do"));
		addCompletion(new BasicCompletion(this, "else"));
		addCompletion(new BasicCompletion(this, "finally"));
		addCompletion(new BasicCompletion(this, "for "));
		addCompletion(new BasicCompletion(this, "function"));
		addCompletion(new BasicCompletion(this, "if"));
		addCompletion(new BasicCompletion(this, "import"));
		addCompletion(new BasicCompletion(this, "in"));
		addCompletion(new BasicCompletion(this, "instanceof"));
		addCompletion(new BasicCompletion(this, "new"));
		addCompletion(new BasicCompletion(this, "return"));
		addCompletion(new BasicCompletion(this, "switch"));
		addCompletion(new BasicCompletion(this, "this"));
		addCompletion(new BasicCompletion(this, "throw"));
		addCompletion(new BasicCompletion(this, "try"));
		addCompletion(new BasicCompletion(this, "typeof"));
		addCompletion(new BasicCompletion(this, "var"));
		addCompletion(new BasicCompletion(this, "void"));
		addCompletion(new BasicCompletion(this, "while"));
		addCompletion(new BasicCompletion(this, "with"));
	  
		for (ZoneScriptStaticFunction function : classList.getScriptFunctions())
			addCompletion(new ScriptFunctionCompletion(this, function));
					
		for (ZoneScriptMethod method : classList.getScriptMethods())
			addCompletion(new ScriptFunctionCompletion(this, method));

//      
//      this.addCompletion(new ShorthandCompletion(this, "sysout",
//            "System.out.println(", "System.out.println("));
//      this.addCompletion(new ShorthandCompletion(this, "syserr",
//            "System.err.println(", "System.err.println("));

	}

	//--------------------------------------------------------------------------
}

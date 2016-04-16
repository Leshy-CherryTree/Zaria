/****************************************/
/* ZoneFunctionHelpTopic.java 			*/
/* Created on: 14-Jul-2014				*/
/* Copyright Cherry Tree Studio 2014	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.editor.datamodels.help;

import eu.cherrytree.zaria.editor.classlist.ZoneScriptStaticFunction;
import java.net.URL;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public class ZoneFunctionHelpTopic extends HelpTopic
{
	//--------------------------------------------------------------------------
	
	private String content;
	
	//--------------------------------------------------------------------------

	public ZoneFunctionHelpTopic(ZoneScriptStaticFunction function)
	{
		StringBuilder page_builder = new StringBuilder();
		
		page_builder.append("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01//EN\" \"http://www.w3.org/TR/html4/strict.dtd\">\n");
		page_builder.append("<html>\n");
		page_builder.append("<head>\n");
		page_builder.append("<title>").append(function.getName()).append("</title>\n");
		page_builder.append("</head>\n");
		page_builder.append("<body>\n");
		page_builder.append("<h1>");
		
		page_builder.append("<b>").append(function.getReturnType().getSimpleName()).append("</b> ").append(function.getName()).append("(");
		
		for(int  i = 0 ; i < function.getParameters().length ; i++)
		{
			page_builder.append("<b>").append(function.getParameters()[i].getType().getSimpleName()).append("</b> ").append(function.getParameters()[i].getName());
			
			if(i < function.getParameters().length-1)
				page_builder.append(", ");
		}
						
		page_builder.append(")");
		
		page_builder.append("</h1>\n");
		
		page_builder.append("<p>").append(function.getDescription()).append("</p>\n");
		
		if(!function.getDetails().isEmpty())
			page_builder.append("<p>").append(function.getDetails()).append("</p>\n");
				
		page_builder.append("<h2>Parameters</h2>\n");
		
		page_builder.append("<ul>\n");
			
		for(ZoneScriptStaticFunction.FunctionParameterInfo param : function.getParameters())
			page_builder.append("<li>").append(param.getName()).append(" - ").append("(<b>").append(param.getType().getSimpleName()).append("</b>) ").append(param.getDescription()).append("</li>\n");
					
		page_builder.append("\t<ul>\n");
		
		page_builder.append("<h2>Return value</h2>\n");
		
		page_builder.append("<p>").append("(<b>").append(function.getReturnType().getSimpleName()).append("</b>) ").append(function.getReturnDescription()).append("</p>");
			
		page_builder.append("</body>\n");
		page_builder.append("</html>\n");
		
		content = page_builder.toString();		
		name = function.getName();
	}
	
	//--------------------------------------------------------------------------

	@Override
	public URL getUrl()
	{
		return null;
	}
	
	//--------------------------------------------------------------------------

	@Override
	public String getContent()
	{
		return content;
	}
	
	//--------------------------------------------------------------------------
}

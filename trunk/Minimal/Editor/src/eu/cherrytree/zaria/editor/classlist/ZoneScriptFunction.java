/****************************************/
/* ZoneScriptFunction.java				*/
/* Created on: 06-Jul-2014				*/
/* Copyright Cherry Tree Studio 2014	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.editor.classlist;

import eu.cherrytree.zaria.scripting.annotations.ScriptFunction;
import java.lang.reflect.Method;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public class ZoneScriptFunction
{
	//--------------------------------------------------------------------------
	
	private static final int maxTooltipLineLength = 60;
	
	//--------------------------------------------------------------------------
	
	public class FunctionParameterInfo
	{
		private Class type;
		private String name;
		private String description;

		public FunctionParameterInfo(Class type, String name, String description)
		{
			this.type = type;
			this.name = name;
			this.description = description;
		}

		public Class getType()
		{
			return type;
		}

		public String getName()
		{
			return name;
		}

		public String getDescription()
		{
			return description;
		}			
	}
	
	//--------------------------------------------------------------------------
	
	private String category;
	private String name;
	private String template;
	private String toolTip;
	
	private String description;
	private String details;
	
	private String returnDescription;
	private Class returnType;
	
	private FunctionParameterInfo[] parameters;
				
	//--------------------------------------------------------------------------

	public ZoneScriptFunction(Method method, ScriptFunction annotation)
	{
		// Getting basic info.
		category = annotation.category();
		name = method.getName();
		description = annotation.description();
		details = annotation.details();
		returnDescription = annotation.returnValue();
		returnType = method.getReturnType();
		
		// Getting information on parameters.
		parameters = new FunctionParameterInfo[method.getParameterTypes().length];
		
		if(method.getParameterTypes().length * 2 == annotation.parameters().length)
		{
			for(int i = 0 ; i < method.getParameterTypes().length ; i++)
				parameters[i] = new FunctionParameterInfo(method.getParameterTypes()[i], annotation.parameters()[i*2], annotation.parameters()[i*2+1]);
		}
		else
		{
			details += " Information on parameters is invalid.";
			
			for(int i = 0 ; i < method.getParameterTypes().length ; i++)
				parameters[i] = new FunctionParameterInfo(method.getParameterTypes()[i], "arg" + i, "A " + method.getParameterTypes()[i].getSimpleName() + " value.");
		}		
		
		// Generating function template.
		template = name + "(";
		
		for(int  i = 0 ; i < parameters.length ; i++)
		{
			template += parameters[i].name;
			
			if(i < parameters.length-1)
				template += ", ";
		}
		
		template += ");";
		
		// Generating tooltip.
		StringBuilder tooltip_builder = new StringBuilder();
		
		tooltip_builder.append("<html>");
		
		tooltip_builder.append("<b>").append(returnType.getSimpleName()).append("</b> ").append(name).append("(");
		
		for(int  i = 0 ; i < parameters.length ; i++)
		{
			tooltip_builder.append("<b>").append(parameters[i].type.getSimpleName()).append("</b> ").append(parameters[i].name);
			
			if(i < parameters.length-1)
				tooltip_builder.append(", ");
		}
		
		tooltip_builder.append(")");
		tooltip_builder.append("<br>");
		
		String[] description_words = description.split(" ");
		
		StringBuilder line = new StringBuilder();
		
		for(int i = 0 ; i < description_words.length ; i++)
		{
			tooltip_builder.append(description_words[i]).append(" ");
			line.append(description_words[i]).append(" ");
			
			if(i < description_words.length-1 && line.length() > maxTooltipLineLength)
			{
				tooltip_builder.append("<br>");
				line.delete(0, line.length());
			}
		}
		
		tooltip_builder.append("</html>");
		
		toolTip = tooltip_builder.toString();
	}
		
	//--------------------------------------------------------------------------
	
	public String getCategory()
	{
		return category;
	}
	
	//--------------------------------------------------------------------------

	public String getName()
	{
		return name;
	}
	//--------------------------------------------------------------------------
	

	public String getTemplate()
	{
		return template;
	}
	
	//--------------------------------------------------------------------------
	
	public String getToolTip()
	{
		return toolTip;
	}
	
	//--------------------------------------------------------------------------

	public String getDescription()
	{
		return description;
	}
	
	//--------------------------------------------------------------------------

	public String getDetails()
	{
		return details;
	}
	
	//--------------------------------------------------------------------------
	
	public String getReturnDescription()
	{
		return returnDescription;
	}
	
	//--------------------------------------------------------------------------
	
	public Class getReturnType()
	{
		return returnType;
	}
			
	//--------------------------------------------------------------------------

	public FunctionParameterInfo[] getParameters()
	{
		return parameters;
	}
	
	//--------------------------------------------------------------------------
}

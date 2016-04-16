/****************************************/
/* ZoneScriptMethod.java					*/
/* Created on: 16-04-2016				*/
/* Copyright Cherry Tree Studio 2016	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.editor.classlist;

import java.lang.reflect.Method;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public abstract class ZoneScriptFunction
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
	
	private String signature;
	
	private FunctionParameterInfo[] parameterInfo;
				
	//--------------------------------------------------------------------------

	public ZoneScriptFunction(Method method, String category, String description, String details, String returnDescription, String[] parameters)
	{
		// Getting basic info.
		this.category = category;
		this.description = description;
		this.details = details;
		this.returnDescription = returnDescription;
				
		name = method.getName();		
		returnType = method.getReturnType();
		
		// Getting information on parameters.
		parameterInfo = new FunctionParameterInfo[method.getParameterTypes().length];
		
		if(method.getParameterTypes().length * 2 == parameters.length)
		{
			for(int i = 0 ; i < method.getParameterTypes().length ; i++)
				parameterInfo[i] = new FunctionParameterInfo(method.getParameterTypes()[i], parameters[i*2], parameters[i*2+1]);
		}
		else
		{
			details += " Information on parameters is invalid.";
			
			for(int i = 0 ; i < method.getParameterTypes().length ; i++)
				parameterInfo[i] = new FunctionParameterInfo(method.getParameterTypes()[i], "arg" + i, "A " + method.getParameterTypes()[i].getSimpleName() + " value.");
		}		
		
		// Generating function template.
		template = name + "(";
		
		for(int  i = 0 ; i < parameterInfo.length ; i++)
		{
			template += parameterInfo[i].name;
			
			if(i < parameterInfo.length-1)
				template += ", ";
		}
		
		template += ");";
		
		// Generating tooltip.
		StringBuilder tooltip_builder = new StringBuilder();
		
		tooltip_builder.append("<html>");
		
		tooltip_builder.append("<b>").append(returnType.getSimpleName()).append("</b> ").append(name).append("(");
		
		for(int  i = 0 ; i < parameterInfo.length ; i++)
		{
			tooltip_builder.append("<b>").append(parameterInfo[i].type.getSimpleName()).append("</b> ").append(parameterInfo[i].name);
			
			if(i < parameterInfo.length-1)
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
		
		signature = returnType.getSimpleName() + " " + name + "(";
		
		for (int i = 0 ; i < parameterInfo.length ; i++)
		{
			signature += parameterInfo[i].type.getSimpleName() + " " + parameterInfo[i].name;
			
			if (i < parameterInfo.length-1)
				signature += ", ";
		}
		
		signature += ")";
			
	}
	//--------------------------------------------------------------------------

	public String getSignature()
	{
		return signature;
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
		return parameterInfo;
	}
	
	//--------------------------------------------------------------------------
}
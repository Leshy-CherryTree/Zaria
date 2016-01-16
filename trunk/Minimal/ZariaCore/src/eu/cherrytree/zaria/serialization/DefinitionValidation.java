/****************************************/
/* DefinitionValidation.java			*/
/* Created on: 10-May-2013				*/
/* Copyright Cherry Tree Studio 2013	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.serialization;

import java.util.ArrayList;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public class DefinitionValidation
{
	//--------------------------------------------------------------------------
	
	public class ErrorInfo
	{
		private String field;
		private String value;
		private String error;

		public ErrorInfo(String field, String value, String error)
		{
			this.field = field;
			this.value = value;
			this.error = error;
		}	

		public String getField()
		{
			return field;
		}

		public String getValue()
		{
			return value;
		}
				
		public String getError()
		{
			return error;
		}				
	}
	
	//--------------------------------------------------------------------------
	
	private String className;
	private String id;
	
	private ArrayList<ErrorInfo> info = new ArrayList<>();
	private ArrayList<DefinitionValidation> subValidations = new ArrayList<>();
	private ArrayList<Exception> exceptions = new ArrayList<>();
			
	//--------------------------------------------------------------------------

	public DefinitionValidation(String className, String id)
	{
		this.className = className;
		this.id = id;
	}
			
	//--------------------------------------------------------------------------
	
	public void addError(boolean condition, String field, short value, String error)
	{
		if(condition)
			addError(field, value, error);
	}
	
	//--------------------------------------------------------------------------
	
	public void addError(String field, short value, String error)
	{
		addError(field, Short.toString(value), error);
	}
	
	//--------------------------------------------------------------------------
	
	public void addError(boolean condition, String field, int value, String error)
	{
		if(condition)
			addError(field, value, error);
	}
	
	//--------------------------------------------------------------------------
	
	public void addError(String field, int value, String error)
	{
		addError(field, Integer.toString(value), error);
	}
	
	//--------------------------------------------------------------------------
	
	public void addError(boolean condition, String field, long value, String error)
	{
		if(condition)
			addError(field, value, error);
	}
	
	//--------------------------------------------------------------------------
	
	public void addError(String field, long value, String error)
	{
		addError(field, Long.toString(value), error);
	}
	
	//--------------------------------------------------------------------------
	
	public void addError(boolean condition, String field, float value, String error)
	{
		if(condition)
			addError(field, value, error);
	}
	
	//--------------------------------------------------------------------------
	
	public void addError(String field, float value, String error)
	{
		addError(field, Float.toString(value), error);
	}
	
	//--------------------------------------------------------------------------
	
	public void addError(boolean condition, String field, double value, String error)
	{
		if(condition)
			addError(field, value, error);
	}
	
	//--------------------------------------------------------------------------
	
	public void addError(String field, double value, String error)
	{
		addError(field, Double.toString(value), error);
	}
	
	//--------------------------------------------------------------------------
	
	public void addError(boolean condition, String field, boolean value, String error)
	{
		if(condition)
			addError(field, value, error);
	}
	
	//--------------------------------------------------------------------------
	
	public void addError(String field, boolean value, String error)
	{
		addError(field, Boolean.toString(value), error);
	}
	
	//--------------------------------------------------------------------------
	
	public void addError(boolean condition, String field, byte value, String error)
	{
		if(condition)
			addError(field, value, error);
	}
	
	//--------------------------------------------------------------------------
	
	public void addError(String field, byte value, String error)
	{
		addError(field, Byte.toString(value), error);
	}
	
	//--------------------------------------------------------------------------
	
	public void addError(boolean condition, String field, char value, String error)
	{
		if(condition)
			addError(field, value, error);
	}
	
	//--------------------------------------------------------------------------
	
	public void addError(String field, char value, String error)
	{
		addError(field, Character.toString(value), error);
	}
	
	//--------------------------------------------------------------------------
	
	public void addError(boolean condition, String field, String value, String error)
	{
		if(condition)
			addError(field, value, error);
	}
	
	//--------------------------------------------------------------------------
	
	public void addError(String field, String value, String error)
	{
		info.add(new ErrorInfo(field, value, error));
	}
	
	//--------------------------------------------------------------------------
	
	public void addException(Exception exception)
	{
		if (ValidationException.class.isAssignableFrom(exception.getClass()))
		{						
			ValidationException val_ex = (ValidationException) exception;
			
			for(DefinitionValidation validation : val_ex.getValidation())
				subValidations.add(validation);
		}
		else
		{
			exceptions.add(exception);
		}
	}
	
	//--------------------------------------------------------------------------
	
	public void addSubValidation(DefinitionValidation validation)
	{
		subValidations.add(validation);
	}
	
	//--------------------------------------------------------------------------
	
	public ArrayList<ErrorInfo> getErrors()
	{
		return info;
	}
	
	//--------------------------------------------------------------------------

	public ArrayList<DefinitionValidation> getSubValidations()
	{
		return subValidations;
	}
			
	//--------------------------------------------------------------------------
	
	public ArrayList<Exception> getExceptions()
	{
		return exceptions;
	}
	
	//--------------------------------------------------------------------------

	public String getClassName()
	{
		return className;
	}

	//--------------------------------------------------------------------------

	public String getID()
	{
		return id;
	}
	
	//--------------------------------------------------------------------------
	
	public String getInfo()
	{
		String ret = "";
		
		for(ErrorInfo err : info)
			ret += "Validation Error: " + err.field + " [" + err.value + "]: " + err.error + "\n";	
		
		return ret;
	}
	
	//--------------------------------------------------------------------------	
			
	@Override
	public String toString()
	{
		String ret = getInfo();
		
		for(Exception exception : exceptions)
			ret += "Occurred Exception: " + exception.toString() + "\n";
		
		for(DefinitionValidation val : subValidations)
		{
			String subval = val.toString();
			subval = "\t" + subval.replace("\n", "\n\t");
			subval = subval.substring(0, subval.length()-1);
			ret += subval;
		}
		
		return ret;
	}
	
	//--------------------------------------------------------------------------
	
	public boolean hasErrors()
	{
		for(DefinitionValidation val : subValidations)
		{
			if(val.hasErrors())
				return true;
		}
		
		return !info.isEmpty() || !exceptions.isEmpty();
	}
	
	//--------------------------------------------------------------------------
}

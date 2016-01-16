/****************************************/
/* ApplicationRuntimeError.java         */
/* Created on: 30-Mar-2013				*/
/* Copyright Cherry Tree Studio 2013	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.base;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public class ApplicationRuntimeError extends Error
{
	//--------------------------------------------------------------------------
	
	private HashMap<String,String> information = new HashMap<>();
	
	//--------------------------------------------------------------------------
	
	public ApplicationRuntimeError()
	{
		super();
	}
	
	//--------------------------------------------------------------------------

	public ApplicationRuntimeError(String message)
	{
		super(message);
	}
	
	//--------------------------------------------------------------------------

	public ApplicationRuntimeError(Throwable cause)
	{
		super(cause);
	}
	
	//--------------------------------------------------------------------------

	public ApplicationRuntimeError(String message, Throwable cause)
	{
		super(message, cause);
	}
	
	//--------------------------------------------------------------------------
	
	public void addInfo(String key, String info)
	{
		assert key != null && !key.isEmpty();
		assert info != null && !info.isEmpty();
		
		information.put(key, info);
	}

	//--------------------------------------------------------------------------
	
	private String getCauses(Throwable throwable)
	{
		String ret = "";
		Throwable cause = throwable.getCause();
		
		if(cause != null)
		{
			ret += "Cause: " + cause.getClass().getName() + "\n";			
			
			String msg = cause.getMessage();
						
			if(msg != null && !msg.isEmpty())
				ret += "\t" + msg;
			
			String sub_cause = getCauses(cause);
			
			if(!sub_cause.isEmpty())
				ret += "\n\n" + sub_cause;
		}
						
		return ret;
	}
	
	//--------------------------------------------------------------------------
	
	public String getInfo()
	{
		String info = "";
		
		for (Map.Entry<String, String> entry : information.entrySet())
			info += entry.getKey() + ": " + entry.getValue() + "\n";
		
		return info;
	}
	
	//--------------------------------------------------------------------------

	@Override
	public String getMessage()
	{
		String message = "Application Runtime Error\n\n";
		
		String msg = super.getMessage();
		
		if(msg != null && !msg.isEmpty())
			message += msg + "\n";
		
		String cause = getCauses(this);
		
		if(!cause.isEmpty())
			message += "\n" + cause + "\n";
		
		String info = getInfo();
		
		if(!info.isEmpty())
			message += "\n" + info;
				
		return message;
	}
	
	//--------------------------------------------------------------------------
}

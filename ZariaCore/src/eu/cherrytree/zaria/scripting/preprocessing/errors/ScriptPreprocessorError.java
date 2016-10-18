/****************************************/
/* ScriptPreprocessorError.java			*/
/* Created on: 14-10-2016				*/
/* Copyright Cherry Tree Studio 2016	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.scripting.preprocessing.errors;

import java.util.ArrayList;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public class ScriptPreprocessorError extends ScriptError
{
	//--------------------------------------------------------------------------
	
	public static class Info
	{
		private String details;
		private String lineSource;
		private int offset;
		private int lineIndex;
		
		public Info(String details, String lineSource, int lineIndex, int offset)
		{
			this.details = details;
			this.lineSource = lineSource;
			this.lineIndex = lineIndex;
			this.offset = offset;
		}

		public String getDetails()
		{
			return details;
		}

		public String getLineSource()
		{
			return lineSource;
		}

		public int getOffset()
		{
			return offset;
		}

		public int getLineIndex()
		{
			return lineIndex;
		}
	}
	
	//--------------------------------------------------------------------------

	private ArrayList<Info> info;
	
	//--------------------------------------------------------------------------

	public ScriptPreprocessorError(Info info, String scriptName)
	{
		super(scriptName);

		this.info = new ArrayList<>();
		this.info.add(info);
	}
	
	//--------------------------------------------------------------------------

	public ScriptPreprocessorError(Info info, String scriptName, Throwable throwable)
	{
		super(scriptName, throwable);

		this.info = new ArrayList<>();
		this.info.add(info);
	}

	//--------------------------------------------------------------------------
	
	public ScriptPreprocessorError(ArrayList<Info> info, String scriptName)
	{
		super(scriptName);

		this.info = info;
	}
	
	//--------------------------------------------------------------------------
	
	public ScriptPreprocessorError(ArrayList<Info> info)
	{
		super("");

		this.info = info;
	}
	
	//--------------------------------------------------------------------------

	public ScriptPreprocessorError(ArrayList<Info> info, String scriptName, Throwable throwable)
	{
		super(scriptName, throwable);

		this.info = info;
	}
	
	//--------------------------------------------------------------------------

	public ArrayList<Info> getInfo()
	{
		return info;
	}
	
	//--------------------------------------------------------------------------

	@Override
	public String getMessage()
	{
		StringBuilder msg = new StringBuilder();

		msg.append(super.getMessage()).append("\n");

		if (getCause() != null)
		{
			msg.append("Cause:").append(getCause().getMessage());
		}

		for (Info inf : info)
		{
			msg.append("\"").append(inf.getLineSource()).append("\" ; Error ").append(inf.getDetails()).append("\n");
		}

		return msg.toString();
	}
	
	//--------------------------------------------------------------------------
}

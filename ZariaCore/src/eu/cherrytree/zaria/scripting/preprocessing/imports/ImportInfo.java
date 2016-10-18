/****************************************/
/* ImportInfo.java						*/
/* Created on: 14-10-2016				*/
/* Copyright Cherry Tree Studio 2016	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.scripting.preprocessing.imports;

import java.util.Objects;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public class ImportInfo
{
	//--------------------------------------------------------------------------
	
	private String source;
	private String target;
	private String whiteSpace;
	private String error;
	private int offset;

	//--------------------------------------------------------------------------
	
	public ImportInfo(String source, int offset)
	{
		this.source = source;
		this.target = removeComment(source.substring(source.indexOf("import") + 7, source.length() - 1).trim());
		this.offset = offset;

		StringBuilder str = new StringBuilder();

		for (int i = 0; i < source.length(); i++)
			str.append(" ");

		whiteSpace = str.toString();

		if (!isValid(target))
			target = null;
	}

	//--------------------------------------------------------------------------

	public String getError()
	{
		return error;
	}
	
	//--------------------------------------------------------------------------

	private boolean isValid(String source)
	{
		char[] chars = source.toCharArray();

		for (int i = 0; i < chars.length; i++)
		{
			char c = chars[i];

			if (c == '*')
			{
				if (i != chars.length - 1)
				{
					error = "Import has '*' character in an invalid place.";
					return false;
				}
			}
			else if (c == '.')
			{
				if (i == 0 || i == chars.length - 1)
				{
					error = "Import has '.' character in an invalid place.";
					return false;
				}
			}
			else if (!Character.isLetter(c) && !Character.isDigit(c) && c != '_' && c != '-')
			{
				error = "Import contains an invalid character '" + c + "'.";
				return false;
			}
		}

		return true;
	}
	
	//--------------------------------------------------------------------------

	private String removeComment(String line)
	{
		char[] chars = line.toCharArray();
		boolean comment = false;

		StringBuilder lineBuilder = new StringBuilder();

		for (int i = 0; i < chars.length; i++)
		{
			if (comment)
			{
				lineBuilder.append(" ");

				if (chars[i] == '*')
				{
					if (i < chars.length - 1)
					{
						if (chars[i + 1] == '/')
						{
							i++;
							lineBuilder.append(" ");
							comment = false;
						}
					}
				}
			}
			else
			{
				if (chars[i] == '/' && i < chars.length - 1 && chars[i + 1] == '*')
				{
					i++;
					lineBuilder.append("  ");
					comment = true;
				}
				else
				{
					lineBuilder.append(chars[i]);
				}
			}
		}

		return lineBuilder.toString().trim();
	}
	
	//--------------------------------------------------------------------------

	public String getSource()
	{
		return source;
	}
	
	//--------------------------------------------------------------------------

	public String getTarget()
	{
		return target;
	}
	
	//--------------------------------------------------------------------------

	public int getOffset()
	{
		return offset;
	}
	
	//--------------------------------------------------------------------------

	public String getWhiteSpace()
	{
		return whiteSpace;
	}
	
	//--------------------------------------------------------------------------

	@Override
	public boolean equals(Object obj)
	{
		if(obj instanceof ImportInfo)
		{
			ImportInfo info = (ImportInfo) obj;

			return info.target.equals(this.target);
		}
		else
			return false;
	}		
	
	//--------------------------------------------------------------------------

	@Override
	public int hashCode()
	{
		int hash = 7;
		hash = 43 * hash + Objects.hashCode(this.target);
		return hash;
	}
	
	//--------------------------------------------------------------------------
}

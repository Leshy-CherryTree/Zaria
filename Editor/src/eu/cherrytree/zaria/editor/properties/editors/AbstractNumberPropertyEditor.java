/****************************************/
/* AbstractNumberPropertyEditor.java 	*/
/* Created on: 06-Apr-2014				*/
/* Copyright Cherry Tree Studio 2014	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.editor.properties.editors;

import eu.cherrytree.zaria.editor.debug.DebugConsole;

import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public abstract class AbstractNumberPropertyEditor extends AbstractPropertyEditor
{
	//--------------------------------------------------------------------------
	
	protected final Class type;
	protected Number lastValidValue;

	//--------------------------------------------------------------------------

	public AbstractNumberPropertyEditor(Class type)
	{
		if(!Number.class.isAssignableFrom(type))
			throw new IllegalArgumentException("Type be a Number but is " + type.getCanonicalName());
		
		this.type = type;
	}

	//--------------------------------------------------------------------------

	@Override
	public Object getValue()
	{
		String text = getValueText();
		
		if(text == null || text.trim().length() == 0)
			return getDefaultValue();

		// allow comma or colon
		text = text.replace(',', '.');

		// collect all numbers from this textfield
		StringBuilder number = new StringBuilder();
		number.ensureCapacity(text.length());
		
		for(int i = 0, c = text.length() ; i < c ; i++)
		{
			char character = text.charAt(i);
			if(	'.' == character || '-' == character || (Double.class.equals(type) && 'E' == character) || 
				(Float.class.equals(type) && 'E' == character) || Character.isDigit(character))
			{
				number.append(character);
			}
			else if(' ' == character)
			{
				continue;
			}
			else
			{
				break;
			}
		}  
				
		try
		{
			Number value = getNumber(number.toString());
			lastValidValue = value;
		}
		catch(NumberFormatException ex)
		{
			DebugConsole.logger.log(Level.SEVERE, null, ex);
		}	
		
		return lastValidValue;
	}

	//--------------------------------------------------------------------------
	
	private Number getNumber(String string)
	{
		if(Byte.class.equals(type) || byte.class.equals(type))
			return Byte.parseByte(string);
		else if(Short.class.equals(type) || short.class.equals(type))
			return Short.parseShort(string);
		else if(Integer.class.equals(type) || int.class.equals(type))
			return Integer.parseInt(string);
		else if(Long.class.equals(type) || long.class.equals(type))
			return Long.parseLong(string);
		else if(Float.class.equals(type) || float.class.equals(type))
			return Float.parseFloat(string);
		else if(Double.class.equals(type) || double.class.equals(type))
			return Double.parseDouble(string);
		else
			return null;
	}

	//--------------------------------------------------------------------------

	protected Object getDefaultValue()
	{
		try
		{
			return type.getConstructor(new Class[] { String.class }).newInstance( new Object[] { "0" });
		}
		catch(NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex)
		{
			DebugConsole.logger.log(Level.SEVERE, null, ex);
		}
		
		return null;
	}

	//--------------------------------------------------------------------------

	@Override
	public boolean supportsCustomEditor()
	{
		return false;
	}
	
	//--------------------------------------------------------------------------
	
	public abstract String getValueText();
	
	//--------------------------------------------------------------------------
}

/****************************************/
/* PropertyEditorFactory.java			*/
/* Created on: 14-Jul-2013				*/
/* Copyright Cherry Tree Studio 2013	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.editor.properties.editors;

import eu.cherrytree.zaria.editor.debug.DebugConsole;
import eu.cherrytree.zaria.editor.document.ZoneDocument;
import eu.cherrytree.zaria.editor.properties.Property;

import eu.cherrytree.zaria.editor.properties.ZoneProperty;
import eu.cherrytree.zaria.serialization.LinkArray;

import java.beans.PropertyEditor;
import java.lang.reflect.InvocationTargetException;

import java.util.HashMap;
import java.util.logging.Level;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public abstract class PropertyEditorFactory
{
	//--------------------------------------------------------------------------
	
	private HashMap<Class,Class> typeToEditor = new HashMap<>();

	//--------------------------------------------------------------------------
	
	private static class FloatPropertyEditor extends NumberPropertyEditor
	{
		FloatPropertyEditor()
		{
			super(Float.class);
		}
	}
	
	//--------------------------------------------------------------------------

	
	private static class DoublePropertyEditor extends NumberPropertyEditor
	{
		DoublePropertyEditor()
		{
			super(Double.class);
		}
	}

	//--------------------------------------------------------------------------
	
	private static class BytePropertyEditor extends NumberPropertyEditor
	{
		BytePropertyEditor()
		{
			super(Byte.class);
		}
	}
	
	//--------------------------------------------------------------------------
	
	private static class ShortPropertyEditor extends NumberPropertyEditor
	{
		ShortPropertyEditor()
		{
			super(Short.class);
		}
	}
	
	//--------------------------------------------------------------------------

	private static class IntegerPropertyEditor extends NumberPropertyEditor
	{
		IntegerPropertyEditor()
		{
			super(Integer.class);
		}
	}

	//--------------------------------------------------------------------------
	
	private static class LongPropertyEditor extends NumberPropertyEditor
	{
		LongPropertyEditor()
		{
			super(Long.class);
		}
	}

	//--------------------------------------------------------------------------
	
	public PropertyEditorFactory()
	{
		registerEditor(String.class, StringPropertyEditor.class);

		registerEditor(double.class, DoublePropertyEditor.class);
		registerEditor(Double.class, DoublePropertyEditor.class);

		registerEditor(float.class, FloatPropertyEditor.class);
		registerEditor(Float.class, FloatPropertyEditor.class);

		registerEditor(int.class, IntegerPropertyEditor.class);
		registerEditor(Integer.class, IntegerPropertyEditor.class);

		registerEditor(long.class, LongPropertyEditor.class);
		registerEditor(Long.class, LongPropertyEditor.class);

		registerEditor(short.class, ShortPropertyEditor.class);
		registerEditor(Short.class, ShortPropertyEditor.class);
		
		registerEditor(byte.class, BytePropertyEditor.class);
		registerEditor(Byte.class, BytePropertyEditor.class);

		registerEditor(boolean.class, BooleanPropertyEditor.class);
		registerEditor(Boolean.class, BooleanPropertyEditor.class);

//		registerEditor(ColorRGBA.class, ColorRGBAPropertyEditor.class);
	}
	
	//--------------------------------------------------------------------------

	protected PropertyEditor loadPropertyEditor(Class clz)
	{				
		PropertyEditor editor = null;
						
		try
		{
			if(Enum.class.isAssignableFrom(clz))
				editor = new EnumPropertyEditor(clz);
			else if(typeToEditor.containsKey(clz))
				editor = (PropertyEditor) typeToEditor.get(clz).newInstance();
		}
		catch(InstantiationException | IllegalAccessException | NoSuchMethodException | IllegalArgumentException | InvocationTargetException ex)
		{
			DebugConsole.logger.log(Level.SEVERE, null, ex);
		}

		return editor;
	}

	//--------------------------------------------------------------------------

	public final synchronized void registerEditor(Class type, Class editorClass)
	{
		typeToEditor.put(type, editorClass);
	}
		
	//--------------------------------------------------------------------------

	public abstract PropertyEditor createPropertyEditor(Property property);

	//--------------------------------------------------------------------------
}

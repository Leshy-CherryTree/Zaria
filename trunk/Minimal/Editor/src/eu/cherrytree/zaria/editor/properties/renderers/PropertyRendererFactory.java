/****************************************/
/* PropertyRendererFactory.java     */
/* Created on: 14-Jul-2013				*/
/* Copyright Cherry Tree Studio 2013	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.editor.properties.renderers;

import eu.cherrytree.zaria.editor.properties.Property;

import java.util.HashMap;

import javax.swing.table.TableCellRenderer;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public abstract class PropertyRendererFactory 
{
	//--------------------------------------------------------------------------
	
	private HashMap<Class,TableCellRenderer> typeToRenderer = new HashMap<>();
	private HashMap<String,ArrayPropertyRenderer> arrayPropertyRenderers = new HashMap<>();

	//--------------------------------------------------------------------------

	public PropertyRendererFactory()
	{				
		// use the default renderer for Object and all primitives
		DefaultCellRenderer default_renderer = new DefaultCellRenderer();
		BooleanCellRenderer boolean_renderer = new BooleanCellRenderer();
		ColorARGBPropertyRenderer color_renderer = new ColorARGBPropertyRenderer();

		registerRenderer(Object.class, default_renderer);
		
		registerRenderer(boolean.class, boolean_renderer);
		registerRenderer(Boolean.class, boolean_renderer);
		
		registerRenderer(byte.class, default_renderer);
		registerRenderer(Byte.class, default_renderer);
		
		registerRenderer(char.class, default_renderer);
		registerRenderer(Character.class, default_renderer);
		
		registerRenderer(double.class, default_renderer);
		registerRenderer(Double.class, default_renderer);
		
		registerRenderer(float.class, default_renderer);
		registerRenderer(Float.class, default_renderer);
		
		registerRenderer(int.class, default_renderer);
		registerRenderer(Integer.class, default_renderer);
		
		registerRenderer(long.class, default_renderer);
		registerRenderer(Long.class, default_renderer);
		
		registerRenderer(short.class, default_renderer);
		registerRenderer(Short.class, default_renderer);
		
//		registerRenderer(ColorRGBA.class, color_renderer);
	}

	//--------------------------------------------------------------------------

	public TableCellRenderer createTableCellRenderer(Property property)
	{
		return getRenderer(property);
	}

	//--------------------------------------------------------------------------

	public TableCellRenderer createTableCellRenderer(Class type)
	{
		return getRenderer(type);
	}

	//--------------------------------------------------------------------------

	public synchronized TableCellRenderer getRenderer(Class type)
	{
		if (type.isArray())
			return getArrayPropertyRenderer(type.getSimpleName());
		else
			return typeToRenderer.get(type);
	}

	//--------------------------------------------------------------------------
	
	protected ArrayPropertyRenderer getArrayPropertyRenderer(String typeName)
	{
		if(arrayPropertyRenderers.containsKey(typeName))
		{
			return arrayPropertyRenderers.get(typeName);
		}
		else
		{
			ArrayPropertyRenderer renderer = new ArrayPropertyRenderer(typeName);
			arrayPropertyRenderers.put(typeName, renderer);
			
			return renderer;
		}
	}
	
	//--------------------------------------------------------------------------

	public final synchronized void registerRenderer(Class type, TableCellRenderer renderer)
	{
		typeToRenderer.put(type, renderer);
	}
		
	//--------------------------------------------------------------------------

	public abstract TableCellRenderer getRenderer(Property property);

	//--------------------------------------------------------------------------
}

/****************************************/
/* DefinitionBooleanCheckbox.java		*/
/* Created on: 01-08-2016				*/
/* Copyright Cherry Tree Studio 2016	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.gdx.particles.editor;

import eu.cherrytree.zaria.serialization.ZariaObjectDefinition;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Field;

import javax.swing.JCheckBox;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public class DefinitionBooleanCheckbox extends JCheckBox implements ActionListener
{
	//--------------------------------------------------------------------------
	
	private ZariaObjectDefinition definition;
	private Field field;
	
	//--------------------------------------------------------------------------

	public DefinitionBooleanCheckbox(ZariaObjectDefinition definition, String fieldName) throws NoSuchFieldException
	{
		this.definition = definition;
		
		field = definition.getClass().getDeclaredField(fieldName);
		field.setAccessible(true);
		
		addActionListener(this);
		
		try
		{
			setSelected((boolean) field.get(definition));
		}
		catch (IllegalArgumentException | IllegalAccessException ex)
		{
			ex.printStackTrace();
		}
	}		
	
	//--------------------------------------------------------------------------

	@Override
	public void actionPerformed(ActionEvent event)
	{
		try
		{
			field.set(definition, isSelected());
		}
		catch (IllegalArgumentException | IllegalAccessException ex)
		{
			ex.printStackTrace();
		}
	}
	
	//--------------------------------------------------------------------------
}

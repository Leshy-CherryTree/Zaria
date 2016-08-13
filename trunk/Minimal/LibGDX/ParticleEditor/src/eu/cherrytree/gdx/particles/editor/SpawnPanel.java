/****************************************/
/* SpawnPanel.java						*/
/* Created on: 27-Jul-2016				*/
/* Copyright Cherry Tree Studio 2016	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.gdx.particles.editor;

import eu.cherrytree.gdx.particles.SpawnEllipseSide;
import eu.cherrytree.gdx.particles.SpawnShape;
import eu.cherrytree.gdx.particles.SpawnShapeValue;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Field;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;


/**
 * 
 * Branched from Particle Editor of libGDX in gdx-tools.
 */
class SpawnPanel extends EditorPanel implements ActionListener
{
	//--------------------------------------------------------------------------

	private JComboBox shapeCombo;
	private JCheckBox edgesCheckbox;
	private JLabel edgesLabel;
	private JComboBox sideCombo;
	private JLabel sideLabel;
	
	private ParticleEditor editor;
	private SpawnShapeValue spawnShapeValue;
	
	private Field shapeField;
	private Field edgesField;
	private Field sideField;
	
	//--------------------------------------------------------------------------

	public SpawnPanel(ParticleEditor editor, SpawnShapeValue spawnShapeValue, String name, String description) throws SecurityException, NoSuchFieldException
	{
		super(null, name, description);

		this.editor = editor;
		this.spawnShapeValue = spawnShapeValue;
		
		shapeField = SpawnShapeValue.class.getDeclaredField("shape");
		edgesField = SpawnShapeValue.class.getDeclaredField("edges");
		sideField = SpawnShapeValue.class.getDeclaredField("side");
		
		shapeField.setAccessible(true);
		edgesField.setAccessible(true);
		sideField.setAccessible(true);
		
		JPanel contentPanel = getContentPanel();
		{
			JLabel label = new JLabel("Shape:");
			contentPanel.add(label, new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 6), 0, 0));
		}
		{
			shapeCombo = new JComboBox();
			shapeCombo.setModel(new DefaultComboBoxModel(SpawnShape.values()));
			contentPanel.add(shapeCombo, new GridBagConstraints(1, 1, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		}
		{
			edgesLabel = new JLabel("Edges:");
			contentPanel.add(edgesLabel, new GridBagConstraints(2, 1, 1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 12, 0, 6), 0, 0));
		}
		{
			edgesCheckbox = new JCheckBox();
			contentPanel.add(edgesCheckbox, new GridBagConstraints(3, 1, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		}
		{
			sideLabel = new JLabel("Side:");
			contentPanel.add(sideLabel, new GridBagConstraints(4, 1, 1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 12, 0, 6), 0, 0));
		}
		{
			sideCombo = new JComboBox();
			sideCombo.setModel(new DefaultComboBoxModel(SpawnEllipseSide.values()));
			contentPanel.add(sideCombo, new GridBagConstraints(5, 1, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		}
		{
			JPanel spacer = new JPanel();
			spacer.setPreferredSize(new Dimension());
			contentPanel.add(spacer, new GridBagConstraints(6, 0, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		}

		edgesCheckbox.setSelected(spawnShapeValue.isEdges());
		sideCombo.setSelectedItem(spawnShapeValue.getShape());

		shapeCombo.addActionListener(this);
		edgesCheckbox.addActionListener(this);
		sideCombo.addActionListener(this);

		shapeCombo.setSelectedItem(spawnShapeValue.getShape());
	}
	
	//--------------------------------------------------------------------------

	@Override
	public void actionPerformed(ActionEvent event)
	{
		try
		{
			if (event.getSource() == shapeCombo)
			{
				SpawnShape shape = (SpawnShape) shapeCombo.getSelectedItem();

				shapeField.set(spawnShapeValue, shape);

				switch (shape)
				{
					case Line:
					case Square:

						setEdgesVisible(false);

						editor.setVisible("Spawn Width", true);
						editor.setVisible("Spawn Height", true);

						break;

					case Ellipse:

						setEdgesVisible(true);

						editor.setVisible("Spawn Width", true);
						editor.setVisible("Spawn Height", true);

						break;

					case Point:

						setEdgesVisible(false);

						editor.setVisible("Spawn Width", false);
						editor.setVisible("Spawn Height", false);

						break;			
				}
			}
			else if (event.getSource() == edgesCheckbox)
			{
				edgesField.set(spawnShapeValue, edgesCheckbox.isSelected());
				setEdgesVisible(true);
			}
			else if (event.getSource() == shapeCombo)
			{
				SpawnEllipseSide side = (SpawnEllipseSide) sideCombo.getSelectedItem();				
				sideField.set(spawnShapeValue, side);
			}
		}
		catch (IllegalArgumentException | IllegalAccessException ex)
		{
			ex.printStackTrace();
		}
	}
	
	//--------------------------------------------------------------------------

	@Override
	public void update(ParticleEditor editor)
	{
		shapeCombo.setSelectedItem(editor.getEmitter().getDefinition().getSpawnShapeValue().getShape());
	}
	
	//--------------------------------------------------------------------------

	private void setEdgesVisible(boolean visible)
	{
		edgesCheckbox.setVisible(visible);
		edgesLabel.setVisible(visible);
		
		visible = visible && edgesCheckbox.isSelected();
		
		sideCombo.setVisible(visible);
		sideLabel.setVisible(visible);
	}
	
	//--------------------------------------------------------------------------
}

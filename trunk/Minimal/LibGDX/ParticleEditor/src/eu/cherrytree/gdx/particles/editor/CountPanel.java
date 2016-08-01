/****************************************/
/* CountPanel.java						*/
/* Created on: 27-Jul-2016				*/
/* Copyright Cherry Tree Studio 2016	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.gdx.particles.editor;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.lang.reflect.Field;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import eu.cherrytree.gdx.particles.ParticleEmitterDefinition;


/**
 * 
 * Branched from Particle Editor of libGDX in gdx-tools.
 */
class CountPanel extends EditorPanel
{
	//--------------------------------------------------------------------------

	private ParticleEditor editor;
	
	private Slider maxSlider;
	private Slider minSlider;
	
	private Field maxParticleCountField;
	private Field minParticleCountField;

	//--------------------------------------------------------------------------
	
	public CountPanel(ParticleEditor editor, String name, String description) throws SecurityException, NoSuchFieldException
	{
		super(null, name, description);
		
		this.editor = editor;
		
		maxParticleCountField = ParticleEmitterDefinition.class.getField("maxParticleCount");
		minParticleCountField = ParticleEmitterDefinition.class.getField("minParticleCount");
		
		maxParticleCountField.setAccessible(true);
		minParticleCountField.setAccessible(true);

		JPanel contentPanel = getContentPanel();
		{
			JLabel label = new JLabel("Min:");
			contentPanel.add(label, new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE,
			new Insets(0, 0, 0, 6), 0, 0));
		}
		{
			minSlider = new Slider(0, 0, 99999, 1, 0, 500);
			contentPanel.add(minSlider, new GridBagConstraints(1, 1, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE,
			new Insets(0, 0, 0, 0), 0, 0));
		}
		
		{
			JLabel label = new JLabel("Max:");
			contentPanel.add(label, new GridBagConstraints(2, 1, 1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE,
			new Insets(0, 12, 0, 6), 0, 0));
		}
		{
			maxSlider = new Slider(0, 0, 99999, 1, 0, 500);
			contentPanel.add(maxSlider, new GridBagConstraints(3, 1, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.NONE,
			new Insets(0, 0, 0, 0), 0, 0));
		}

		maxSlider.setValue(editor.getEmitter().getDefinition().getMaxParticleCount());
		maxSlider.addChangeListener(new ChangeListener()
		{
			@Override
			public void stateChanged(ChangeEvent event)
			{
				try
				{
					maxParticleCountField.set(editor, (int) maxSlider.getValue());
				}
				catch (IllegalArgumentException | IllegalAccessException ex)
				{
					ex.printStackTrace();
				}
			}
		});

		minSlider.setValue(editor.getEmitter().getDefinition().getMinParticleCount());
		minSlider.addChangeListener(new ChangeListener()
		{
			@Override
			public void stateChanged(ChangeEvent event)
			{
				try
				{
					minParticleCountField.set(editor, (int) minSlider.getValue());
				}
				catch (IllegalArgumentException | IllegalAccessException ex)
				{
					ex.printStackTrace();
				}
			}
		});
	}
	
	//--------------------------------------------------------------------------
	
	@Override
	public void update(ParticleEditor editor)
	{
		// Intentionally empty.
	}		
	
	//--------------------------------------------------------------------------
}

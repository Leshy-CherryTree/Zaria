/****************************************/
/* NumericPanel.java					*/
/* Created on: 27-Jul-2016				*/
/* Copyright Cherry Tree Studio 2016	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.gdx.particles.editor;

import java.awt.GridBagConstraints;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import eu.cherrytree.gdx.particles.NumericValue;


/**
 * 
 * Branched from Particle Editor of libGDX in gdx-tools.
 */
class NumericPanel extends EditorPanel
{
	//--------------------------------------------------------------------------

	private NumericValue value;
	private JSpinner valueSpinner;
	
	//--------------------------------------------------------------------------

	public NumericPanel(final NumericValue value, String name, String description)
	{
		super(value, name, description);
		this.value = value;

		JPanel contentPanel = getContentPanel();
		{
			JLabel label = new JLabel("Value:");
			contentPanel.add(label, new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE,
			new Insets(0, 0, 0, 6), 0, 0));
		}
		{
			valueSpinner = new JSpinner(new SpinnerNumberModel(new Float(0), new Float(-99999), new Float(99999), new Float(0.1f)));
			contentPanel.add(valueSpinner, new GridBagConstraints(1, 1, 1, 1, 1, 0, GridBagConstraints.WEST,
			GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		}

		valueSpinner.setValue(value.getValue());

		valueSpinner.addChangeListener(new ChangeListener()
		{
			@Override
			public void stateChanged(ChangeEvent event)
			{
				value.setValue((Float) valueSpinner.getValue());
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

/****************************************/
/* RangedNumericPanel.java				*/
/* Created on: 27-Jul-2016				*/
/* Copyright Cherry Tree Studio 2016	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.gdx.particles.editor;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import eu.cherrytree.gdx.particles.ParticleEmitter.RangedNumericValue;


/**
 * 
 * Branched from Particle Editor of libGDX in gdx-tools.
 */
class RangedNumericPanel extends EditorPanel
{
	//--------------------------------------------------------------------------

	private RangedNumericValue value;
	private Slider minSlider, maxSlider;
	private JButton rangeButton;
	private JLabel label;
	
	//--------------------------------------------------------------------------

	public RangedNumericPanel(final RangedNumericValue value, String name, String description)
	{
		super(value, name, description);
		this.value = value;

		JPanel contentPanel = getContentPanel();
		{
			label = new JLabel("Value:");
			contentPanel.add(label, new GridBagConstraints(2, 2, 1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 6), 0, 0));
		}
		{
			minSlider = new Slider(0, -99999, 99999, 1, -400, 400);
			contentPanel.add(minSlider, new GridBagConstraints(3, 2, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		}
		{
			maxSlider = new Slider(0, -99999, 99999, 1, -400, 400);
			contentPanel.add(maxSlider, new GridBagConstraints(4, 2, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 6, 0, 0), 0, 0));
		}
		{
			rangeButton = new JButton("<");
			rangeButton.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
			contentPanel.add(rangeButton, new GridBagConstraints(5, 2, 1, 1, 1.0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 1, 0, 0), 0, 0));
		}

		minSlider.setValue(value.getLowMin());
		maxSlider.setValue(value.getLowMax());

		minSlider.addChangeListener(new ChangeListener()
		{
			@Override
			public void stateChanged(ChangeEvent event)
			{
				value.setLowMin((Float) minSlider.getValue());
				if (!maxSlider.isVisible())
					value.setLowMax((Float) minSlider.getValue());
			}
		});

		maxSlider.addChangeListener(new ChangeListener()
		{
			@Override
			public void stateChanged(ChangeEvent event)
			{
				value.setLowMax((Float) maxSlider.getValue());
			}
		});

		rangeButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent event)
			{
				boolean visible = !maxSlider.isVisible();
				maxSlider.setVisible(visible);
				rangeButton.setText(visible ? "<" : ">");
				Slider slider = visible ? maxSlider : minSlider;
				value.setLowMax((Float) slider.getValue());
			}
		});

		if (value.getLowMin() == value.getLowMax())
			rangeButton.doClick(0);
	}
	
	//--------------------------------------------------------------------------
	
	@Override
	public void update(ParticleEditor editor)
	{
		// Intentionally empty.
	}		

	//--------------------------------------------------------------------------
}

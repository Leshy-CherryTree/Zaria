/****************************************/
/* PercentagePanel.java					*/
/* Created on: 27-Jul-2016				*/
/* Copyright Cherry Tree Studio 2016	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.gdx.particles.editor;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;

import eu.cherrytree.gdx.particles.ScaledNumericValue;


/**
 * 
 * Branched from Particle Editor of libGDX in gdx-tools.
 */
class PercentagePanel extends EditorPanel
{
	//--------------------------------------------------------------------------
	
	private ScaledNumericValue value;
	private JButton expandButton;
	private Chart chart;
	
	//--------------------------------------------------------------------------

	public PercentagePanel(final ScaledNumericValue value, String chartTitle, String name, String description)
	{
		super(value, name, description);
		this.value = value;

		JPanel contentPanel = getContentPanel();
		{
			chart = new Chart(chartTitle)
			{
				@Override
				public void pointsChanged()
				{
					value.setTimeline(chart.getValuesX());
					value.setScaling(chart.getValuesY());
				}
			};
			chart.setPreferredSize(new Dimension(150, 62));
			contentPanel.add(chart, new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		}
		{
			expandButton = new JButton("+");
			expandButton.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
			contentPanel.add(expandButton, new GridBagConstraints(1, 0, 1, 1, 1, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 6, 0, 0), 0, 0));
		}

		chart.setValues(value.getTimeline(), value.getScaling());

		expandButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent event)
			{
				chart.setExpanded(!chart.isExpanded());
				boolean expanded = chart.isExpanded();
				GridBagLayout layout = (GridBagLayout) getContentPanel().getLayout();
				GridBagConstraints chartConstraints = layout.getConstraints(chart);
				GridBagConstraints expandButtonConstraints = layout.getConstraints(expandButton);
				if (expanded)
				{
					chart.setPreferredSize(new Dimension(150, 200));
					expandButton.setText("-");
					chartConstraints.weightx = 1;
					expandButtonConstraints.weightx = 0;
				}
				else
				{
					chart.setPreferredSize(new Dimension(150, 62));
					expandButton.setText("+");
					chartConstraints.weightx = 0;
					expandButtonConstraints.weightx = 1;
				}
				layout.setConstraints(chart, chartConstraints);
				layout.setConstraints(expandButton, expandButtonConstraints);
				chart.revalidate();
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

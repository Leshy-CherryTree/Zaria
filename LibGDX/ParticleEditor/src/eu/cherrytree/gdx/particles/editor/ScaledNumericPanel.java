/****************************************/
/* ScaledNumericPanel.java				*/
/* Created on: 27-Jul-2016				*/
/* Copyright Cherry Tree Studio 2016	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.gdx.particles.editor;

import eu.cherrytree.gdx.particles.RangedNumericValue;
import eu.cherrytree.gdx.particles.ScaledNumericValue;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Field;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


/**
 * 
 * Branched from Particle Editor of libGDX in gdx-tools.
 */
public class ScaledNumericPanel extends EditorPanel
{
	//--------------------------------------------------------------------------

	private ScaledNumericValue value;
	
	private Slider lowMinSlider, lowMaxSlider;
	private Slider highMinSlider, highMaxSlider;
	private JCheckBox relativeCheckBox;
	private Chart chart;
	private JPanel formPanel;
	private JButton expandButton;
	private JButton lowRangeButton;
	private JButton highRangeButton;
	
	private Field scalingField;
	private Field timelineField;
	private Field highMinField;
	private Field highMaxField;
	private Field lowMinField;
	private Field lowMaxField;
	private Field relativeField; 
	
	//--------------------------------------------------------------------------

	public ScaledNumericPanel(ScaledNumericValue scaledNumericValue, String chartTitle, String name, String description) throws SecurityException, NoSuchFieldException
	{
		super(scaledNumericValue, name, description);
		this.value = scaledNumericValue;
		
		scalingField = ScaledNumericValue.class.getField("scaling");
		timelineField = ScaledNumericValue.class.getField("timeline");
		highMinField = ScaledNumericValue.class.getField("highMin");
		highMaxField = ScaledNumericValue.class.getField("highMax");
		lowMinField = RangedNumericValue.class.getField("lowMin");
		lowMaxField = RangedNumericValue.class.getField("lowMax");
		relativeField = ScaledNumericValue.class.getField("relative");
		
		scalingField.setAccessible(true);
		timelineField.setAccessible(true);
		highMinField.setAccessible(true);
		highMaxField.setAccessible(true);
		lowMinField.setAccessible(true);
		lowMaxField.setAccessible(true);
		relativeField.setAccessible(true);

		JPanel contentPanel = getContentPanel();
		{
			formPanel = new JPanel(new GridBagLayout());
			contentPanel.add(formPanel, new GridBagConstraints(5, 5, 1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 6), 0, 0));
			{
				JLabel label = new JLabel("High:");
				formPanel.add(label, new GridBagConstraints(2, 1, 1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 6), 0, 0));
			}
			{
				highMinSlider = new Slider(0, -99999, 99999, 1f, -400, 400);
				formPanel.add(highMinSlider, new GridBagConstraints(3, 1, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
			}
			{
				highMaxSlider = new Slider(0, -99999, 99999, 1f, -400, 400);
				formPanel.add(highMaxSlider, new GridBagConstraints(4, 1, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 6, 0, 0), 0, 0));
			}
			{
				highRangeButton = new JButton("<");
				highRangeButton.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
				formPanel.add(highRangeButton, new GridBagConstraints(5, 1, 1, 1, 0.0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 1, 0, 0), 0, 0));
			}
			{
				JLabel label = new JLabel("Low:");
				formPanel.add(label, new GridBagConstraints(2, 2, 1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 6), 0, 0));
			}
			{
				lowMinSlider = new Slider(0, -99999, 99999, 1f, -400, 400);
				formPanel.add(lowMinSlider, new GridBagConstraints(3, 2, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
			}
			{
				lowMaxSlider = new Slider(0, -99999, 99999, 1f, -400, 400);
				formPanel.add(lowMaxSlider, new GridBagConstraints(4, 2, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 6, 0, 0), 0, 0));
			}
			{
				lowRangeButton = new JButton("<");
				lowRangeButton.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
				formPanel.add(lowRangeButton, new GridBagConstraints(5, 2, 1, 1, 0.0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 1, 0, 0), 0, 0));
			}
		}
		{
			chart = new Chart(chartTitle)
			{
				@Override
				public void pointsChanged()
				{
					try
					{
						timelineField.set(value, chart.getValuesX());
						scalingField.set(value, chart.getValuesY());
					}
					catch (IllegalArgumentException | IllegalAccessException ex)
					{
						ex.printStackTrace();
					}
				}
			};
			
			contentPanel.add(chart, new GridBagConstraints(6, 5, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
			chart.setPreferredSize(new Dimension(150, 30));
		}
		{
			expandButton = new JButton("+");
			contentPanel.add(expandButton, new GridBagConstraints(7, 5, 1, 1, 1, 0, GridBagConstraints.SOUTHWEST, GridBagConstraints.NONE, new Insets(0, 5, 0, 0), 0, 0));
			expandButton.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
		}
		{
			relativeCheckBox = new JCheckBox("Relative");
			contentPanel.add(relativeCheckBox, new GridBagConstraints(7, 5, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 6, 0, 0), 0, 0));
		}

		lowMinSlider.setValue(value.getLowMin());
		lowMaxSlider.setValue(value.getLowMax());
		highMinSlider.setValue(value.getHighMin());
		highMaxSlider.setValue(value.getHighMax());
		chart.setValues(value.getTimeline(), value.getScaling());
		relativeCheckBox.setSelected(value.isRelative());

		lowMinSlider.addChangeListener(new ChangeListener()
		{
			@Override
			public void stateChanged(ChangeEvent event)
			{
				try
				{
					lowMinField.set(value, (Float) lowMinSlider.getValue());
					
					if (!lowMaxSlider.isVisible())
						lowMaxField.set(value, (Float) lowMinSlider.getValue());
				}
				catch (IllegalArgumentException | IllegalAccessException ex)
				{
					ex.printStackTrace();
				}
			}
		});
		lowMaxSlider.addChangeListener(new ChangeListener()
		{
			@Override
			public void stateChanged(ChangeEvent event)
			{
				try
				{
					lowMaxField.set(value, (Float) lowMaxSlider.getValue());
				}
				catch (IllegalArgumentException | IllegalAccessException ex)
				{
					ex.printStackTrace();
				}
			}
		});
		highMinSlider.addChangeListener(new ChangeListener()
		{
			@Override
			public void stateChanged(ChangeEvent event)
			{
				try
				{
					highMinField.set(value, (Float) highMinSlider.getValue());
					
					if (!highMaxSlider.isVisible())
						highMaxField.set(value, (Float) highMinSlider.getValue());
				}
				catch (IllegalArgumentException | IllegalAccessException ex)
				{
					ex.printStackTrace();
				}
			}
		});
		highMaxSlider.addChangeListener(new ChangeListener()
		{
			@Override
			public void stateChanged(ChangeEvent event)
			{
				try
				{
					highMaxField.set(value, (Float) highMaxSlider.getValue());
				}
				catch (IllegalArgumentException | IllegalAccessException ex)
				{
					ex.printStackTrace();
				}
			}
		});

		relativeCheckBox.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent event)
			{
				try
				{
					relativeField.set(value, relativeCheckBox.isSelected());
				}
				catch (IllegalArgumentException | IllegalAccessException ex)
				{
					ex.printStackTrace();
				}
			}
		});

		lowRangeButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent event)
			{
				boolean visible = !lowMaxSlider.isVisible();
				
				lowMaxSlider.setVisible(visible);
				lowRangeButton.setText(visible ? "<" : ">");
				
				GridBagLayout layout = (GridBagLayout) formPanel.getLayout();
				GridBagConstraints constraints = layout.getConstraints(lowRangeButton);
				
				constraints.gridx = visible ? 5 : 4;
				layout.setConstraints(lowRangeButton, constraints);
				
				Slider slider = visible ? lowMaxSlider : lowMinSlider;
				
				try
				{
					lowMaxField.set(value, (Float) slider.getValue());
				}
				catch (IllegalArgumentException | IllegalAccessException ex)
				{
					ex.printStackTrace();
				}
			}
		});

		highRangeButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent event)
			{
				boolean visible = !highMaxSlider.isVisible();
				
				highMaxSlider.setVisible(visible);
				highRangeButton.setText(visible ? "<" : ">");
				
				GridBagLayout layout = (GridBagLayout) formPanel.getLayout();
				GridBagConstraints constraints = layout.getConstraints(highRangeButton);
				
				constraints.gridx = visible ? 5 : 4;
				layout.setConstraints(highRangeButton, constraints);
				
				Slider slider = visible ? highMaxSlider : highMinSlider;
				
				try
				{
					highMaxField.set(value, (Float) slider.getValue());
				}
				catch (IllegalArgumentException | IllegalAccessException ex)
				{
					ex.printStackTrace();
				}
			}
		});

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
					chart.setPreferredSize(new Dimension(150, 30));
					expandButton.setText("+");
					chartConstraints.weightx = 0;
					expandButtonConstraints.weightx = 1;
				}
				
				layout.setConstraints(chart, chartConstraints);
				layout.setConstraints(expandButton, expandButtonConstraints);
				
				relativeCheckBox.setVisible(!expanded);
				formPanel.setVisible(!expanded);
				chart.revalidate();
			}
		});

		if (value.getLowMin() == value.getLowMax())
			lowRangeButton.doClick(0);
		
		if (value.getHighMin() == value.getHighMax())
			highRangeButton.doClick(0);
	}
	
	//--------------------------------------------------------------------------

	public JPanel getFormPanel()
	{
		return formPanel;
	}

	//--------------------------------------------------------------------------
	
	@Override
	public void update(ParticleEditor editor)
	{
		// Intentionally empty.
	}		
	
	//--------------------------------------------------------------------------
}

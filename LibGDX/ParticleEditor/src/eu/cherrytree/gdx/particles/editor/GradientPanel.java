/****************************************/
/* GradientPanel.java					*/
/* Created on: 27-Jul-2016				*/
/* Copyright Cherry Tree Studio 2016	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.gdx.particles.editor;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JColorChooser;
import javax.swing.JPanel;

import eu.cherrytree.gdx.particles.ParticleEmitter.GradientColorValue;

/**
 * 
 * Branched from Particle Editor of libGDX in gdx-tools.
 */
public class GradientPanel extends EditorPanel
{
	//--------------------------------------------------------------------------

	private GradientColorValue value;
	private GradientEditor gradientEditor;
	private ColorSlider saturationSlider, lightnessSlider;
	private JPanel colorPanel;
	private ColorSlider hueSlider;
	
	//--------------------------------------------------------------------------

	public GradientPanel(GradientColorValue value, String name, String description, boolean hideGradientEditor)
	{
		super(value, name, description);
		this.value = value;

		JPanel contentPanel = getContentPanel();
		{
			gradientEditor = new GradientEditor(this);
			contentPanel.add(gradientEditor, new GridBagConstraints(0, 1, 3, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 6, 0), 0, 10));
		}
		{
			hueSlider = new ColorSlider(new Color[] { Color.red, Color.yellow, Color.green, Color.cyan, Color.blue, Color.magenta, Color.red })
			{
				@Override
				protected void colorPicked()
				{
					saturationSlider.setColors(new Color[]
					{
						new Color(Color.HSBtoRGB(getPercentage(), 1, 1)), Color.white
					});
					
					updateColor();
				}
			};
			contentPanel.add(hueSlider, new GridBagConstraints(1, 2, 2, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 6, 0), 0, 0));
		}
		{
			saturationSlider = new ColorSlider(new Color[]{Color.red, Color.white})
			{
				@Override
				protected void colorPicked()
				{
					updateColor();
				}
			};
			contentPanel.add(saturationSlider, new GridBagConstraints(1, 3, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 6), 0, 0));
		}
		{
			lightnessSlider = new ColorSlider(new Color[0])
			{
				@Override
				protected void colorPicked()
				{
					updateColor();
				}
			};
			contentPanel.add(lightnessSlider, new GridBagConstraints(2, 3, 1, 1, 1, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		}
		{
			colorPanel = new JPanel()
			{
				@Override
				public Dimension getPreferredSize()
				{
					Dimension size = super.getPreferredSize();
					size.width = 52;
					return size;
				}
			};
			contentPanel.add(colorPanel, new GridBagConstraints(0, 2, 1, 2, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(3, 0, 0, 6), 0, 0));
		}

		colorPanel.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				Color color = JColorChooser.showDialog(colorPanel, "Set Color", colorPanel.getBackground());
				if (color != null)
					setColor(color);
			}
		});
		
		colorPanel.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.black));

		if (hideGradientEditor)
			gradientEditor.setVisible(false);

		gradientEditor.getPercentages().clear();
		for (float percent : value.getTimeline())
			gradientEditor.getPercentages().add(percent);

		gradientEditor.getColors().clear();
		float[] colors = value.getColors();
		
		for (int i = 0; i < colors.length;)
		{
			float r = colors[i++];
			float g = colors[i++];
			float b = colors[i++];
			gradientEditor.getColors().add(new Color(r, g, b));
		}
		
		if (gradientEditor.getColors().isEmpty() || gradientEditor.getPercentages().isEmpty())
		{
			gradientEditor.getPercentages().clear();
			gradientEditor.getPercentages().add(0f);
			gradientEditor.getPercentages().add(1f);
			gradientEditor.getColors().clear();
			gradientEditor.getColors().add(Color.white);
		}
		
		setColor(gradientEditor.getColors().get(0));
	}
	
	//--------------------------------------------------------------------------

	@Override
	public Dimension getPreferredSize()
	{
		Dimension size = super.getPreferredSize();
		size.width = 10;
		return size;
	}
	
	//--------------------------------------------------------------------------
	
	@Override
	public void update(ParticleEditor editor)
	{
		// Intentionally empty.
	}		
	
	//--------------------------------------------------------------------------

	public final void setColor(Color color)
	{
		float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
		hueSlider.setPercentage(hsb[0]);
		saturationSlider.setPercentage(1 - hsb[1]);
		lightnessSlider.setPercentage(1 - hsb[2]);
		colorPanel.setBackground(color);
	}
	
	//--------------------------------------------------------------------------

	void updateColor()
	{
		Color color = new Color(Color.HSBtoRGB(hueSlider.getPercentage(), 1 - saturationSlider.getPercentage(), 1));
		lightnessSlider.setColors(new Color[] { color, Color.black });
		color = new Color(Color.HSBtoRGB(hueSlider.getPercentage(), 1 - saturationSlider.getPercentage(),
		1 - lightnessSlider.getPercentage()));
		colorPanel.setBackground(color);
		gradientEditor.setColor(color);

		float[] colors = new float[gradientEditor.getColors().size() * 3];
		int i = 0;
		
		for (Color c : gradientEditor.getColors())
		{
			colors[i++] = c.getRed() / 255f;
			colors[i++] = c.getGreen() / 255f;
			colors[i++] = c.getBlue() / 255f;
		}
		
		float[] percentages = new float[gradientEditor.getPercentages().size()];
		i = 0;
		
		for (Float percent : gradientEditor.getPercentages())
			percentages[i++] = percent;
		
		value.setColors(colors);
		value.setTimeline(percentages);
	}
	
	//--------------------------------------------------------------------------
}

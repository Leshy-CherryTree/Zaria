/****************************************/
/* Slider.java							*/
/* Created on: 27-Jul-2016				*/
/* Copyright Cherry Tree Studio 2016	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.gdx.particles.editor;

import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeListener;


/**
 * 
 * Branched from Particle Editor of libGDX in gdx-tools.
 */
public class Slider extends JPanel
{
	//--------------------------------------------------------------------------
	
	private JSpinner spinner;
	
	//--------------------------------------------------------------------------

	public Slider(float initialValue, float min, float max, float stepSize, float sliderMin, float sliderMax)
	{
		spinner = new JSpinner(new SpinnerNumberModel(initialValue, min, max, stepSize));
		setLayout(new BorderLayout());
		add(spinner);
	}
	
	//--------------------------------------------------------------------------

	public void setValue(float value)
	{
		spinner.setValue((double) value);
	}
	
	//--------------------------------------------------------------------------

	public float getValue()
	{
		return ((Double) spinner.getValue()).floatValue();
	}
	
	//--------------------------------------------------------------------------

	public void addChangeListener(ChangeListener listener)
	{
		spinner.addChangeListener(listener);
	}
	
	//--------------------------------------------------------------------------

	@Override
	public Dimension getPreferredSize()
	{
		Dimension size = super.getPreferredSize();
		size.width = 75;
		size.height = 26;
		return size;
	}
	
	//--------------------------------------------------------------------------
}

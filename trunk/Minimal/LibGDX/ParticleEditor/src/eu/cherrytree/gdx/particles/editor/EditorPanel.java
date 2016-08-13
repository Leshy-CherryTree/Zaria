/****************************************/
/* EditorPanel.java						*/
/* Created on: 27-Jul-2016				*/
/* Copyright Cherry Tree Studio 2016	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.gdx.particles.editor;

import eu.cherrytree.gdx.particles.ParticleValue;

import java.awt.Cursor;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.Field;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;


/**
 * 
 * Branched from Particle Editor of libGDX in gdx-tools.
 */
public abstract class EditorPanel extends JPanel
{
	//--------------------------------------------------------------------------

	private String name;
	private String description;
	private ParticleValue value;
	private JPanel titlePanel;
	private JToggleButton activeButton;
	private JPanel contentPanel;
	private JToggleButton advancedButton;
	private JPanel advancedPanel;
	private boolean hasAdvanced;
	private JLabel descriptionLabel;
	
	//--------------------------------------------------------------------------

	public EditorPanel(ParticleValue value, String name, String description)
	{
		this.name = name;
		this.value = value;
		this.description = description;

		setLayout(new GridBagLayout());
		{
			titlePanel = new JPanel(new GridBagLayout());
			add(titlePanel, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(3, 0, 3, 0), 0, 0));
			titlePanel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			{
				JLabel label = new JLabel(name);
				titlePanel.add(label, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(3, 6, 3, 6), 0, 0));
				label.setFont(label.getFont().deriveFont(Font.BOLD));
			}
			{
				descriptionLabel = new JLabel(description);
				titlePanel.add(descriptionLabel, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(3, 6, 3, 6), 0, 0));
			}
			{
				advancedButton = new JToggleButton("Advanced");
				titlePanel.add(advancedButton, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 6), 0, 0));
				advancedButton.setVisible(false);
			}
			{
				activeButton = new JToggleButton("Active");
				titlePanel.add(activeButton, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 6), 0, 0));
			}
		}
		{
			contentPanel = new JPanel(new GridBagLayout());
			add(contentPanel, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 6, 6, 6), 0, 0));
			contentPanel.setVisible(false);
		}
		{
			advancedPanel = new JPanel(new GridBagLayout());
			add(advancedPanel, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 6, 6, 6), 0, 0));
			advancedPanel.setVisible(false);
		}

		titlePanel.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent event)
			{
				if (!activeButton.isVisible())
					return;
				
				activeButton.setSelected(!activeButton.isSelected());
				
				updateActive();
			}
		});
		activeButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent event)
			{
				updateActive();
			}
		});
		advancedButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent event)
			{
				advancedPanel.setVisible(advancedButton.isSelected());
			}
		});

		if (value != null)
		{
			activeButton.setSelected(value.isActive());
			updateActive();
		}

		boolean alwaysActive = value == null ? true : value.isAlwaysActive();
		activeButton.setVisible(!alwaysActive);
		
		if (value == null || value.isActive())
			contentPanel.setVisible(true);
		
//		if (alwaysActive)
//			titlePanel.setCursor(null);
	}
	
	//--------------------------------------------------------------------------

	private void updateActive()
	{
		contentPanel.setVisible(activeButton.isSelected());
		advancedPanel.setVisible(activeButton.isSelected() && advancedButton.isSelected());
		advancedButton.setVisible(activeButton.isSelected() && hasAdvanced);
		descriptionLabel.setText(activeButton.isSelected() ? description : "");
		
		if (value != null)
		{
			try
			{
				Field active_field = ParticleValue.class.getDeclaredField("active");
				active_field.setAccessible(true);
				
				active_field.set(value, activeButton.isSelected());
			}
			catch(NoSuchFieldException | SecurityException  | IllegalAccessException | IllegalArgumentException ex)
			{
				ex.printStackTrace();
			}
		}
	}
	
	//--------------------------------------------------------------------------

	public void setHasAdvanced(boolean hasAdvanced)
	{
		this.hasAdvanced = hasAdvanced;
		advancedButton.setVisible(hasAdvanced && (value.isActive() || value.isAlwaysActive()));
	}
	
	//--------------------------------------------------------------------------

	public JPanel getContentPanel()
	{
		return contentPanel;
	}
	
	//--------------------------------------------------------------------------

	public JPanel getAdvancedPanel()
	{
		return advancedPanel;
	}
	
	//--------------------------------------------------------------------------

	@Override
	public String getName()
	{
		return name;
	}
	
	//--------------------------------------------------------------------------

	public void setEmbedded()
	{
		GridBagLayout layout = (GridBagLayout) getLayout();
		GridBagConstraints constraints = layout.getConstraints(contentPanel);
		constraints.insets = new Insets(0, 0, 0, 0);
		layout.setConstraints(contentPanel, constraints);

		titlePanel.setVisible(false);
	}
	
	//--------------------------------------------------------------------------

	public abstract void update(ParticleEditor editor);

	//--------------------------------------------------------------------------
}

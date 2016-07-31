/****************************************/
/* OptionsPanel.java						*/
/* Created on: 27-Jul-2016				*/
/* Copyright Cherry Tree Studio 2016	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.gdx.particles.editor;

import eu.cherrytree.gdx.particles.ParticleEmitter;
import java.awt.GridBagConstraints;
import java.awt.Insets;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;


/**
 * 
 * Branched from Particle Editor of libGDX in gdx-tools.
 */
class OptionsPanel extends EditorPanel
{
	//--------------------------------------------------------------------------

	private JCheckBox attachedCheckBox;
	private JCheckBox continuousCheckbox;
	private JCheckBox alignedCheckbox;
	private JCheckBox additiveCheckbox;
	private JCheckBox behindCheckbox;
	private JCheckBox premultipliedAlphaCheckbox;
	
	//--------------------------------------------------------------------------

	public OptionsPanel(final ParticleEditor editor, String name, String description)
	{
		super(null, name, description);

		JPanel contentPanel = getContentPanel();
		{
			JLabel label = new JLabel("Additive:");
			contentPanel.add(label, new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(6, 0, 0, 0), 0, 0));
		}
		{
			additiveCheckbox = new JCheckBox();
			contentPanel.add(additiveCheckbox, new GridBagConstraints(1, 1, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(6, 6, 0, 0), 0, 0));
		}
		{
			JLabel label = new JLabel("Attached:");
			contentPanel.add(label, new GridBagConstraints(0, 2, 1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(6, 0, 0, 0), 0, 0));
		}
		{
			attachedCheckBox = new JCheckBox();
			contentPanel.add(attachedCheckBox, new GridBagConstraints(1, 2, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(6, 6, 0, 0), 0, 0));
		}
		{
			JLabel label = new JLabel("Continuous:");
			contentPanel.add(label, new GridBagConstraints(0, 3, 1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(6, 0, 0, 0), 0, 0));
		}
		{
			continuousCheckbox = new JCheckBox();
			contentPanel.add(continuousCheckbox, new GridBagConstraints(1, 3, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(6, 6, 0, 0), 0, 0));
		}
		{
			JLabel label = new JLabel("Aligned:");
			contentPanel.add(label, new GridBagConstraints(0, 4, 1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(6, 0, 0, 0), 0, 0));
		}
		{
			alignedCheckbox = new JCheckBox();
			contentPanel.add(alignedCheckbox, new GridBagConstraints(1, 4, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(6, 6, 0, 0), 0, 0));
		}
		{
			JLabel label = new JLabel("Behind:");
			contentPanel.add(label, new GridBagConstraints(0, 5, 1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(6, 0, 0, 0), 0, 0));
		}
		{
			behindCheckbox = new JCheckBox();
			contentPanel.add(behindCheckbox, new GridBagConstraints(1, 5, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(6, 6, 0, 0), 0, 0));
		}
		{
			JLabel label = new JLabel("Premultiplied Alpha:");
			contentPanel.add(label, new GridBagConstraints(0, 6, 1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(6, 0, 0, 0), 0, 0));
		}
		{
			premultipliedAlphaCheckbox = new JCheckBox();
			contentPanel.add(premultipliedAlphaCheckbox, new GridBagConstraints(1, 6, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(6, 6, 0, 0), 0, 0));
		}

		attachedCheckBox.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent event)
			{
				editor.getEmitter().setAttached(attachedCheckBox.isSelected());
			}
		});

		continuousCheckbox.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent event)
			{
				editor.getEmitter().setContinuous(continuousCheckbox.isSelected());
			}
		});

		alignedCheckbox.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent event)
			{
				editor.getEmitter().setAligned(alignedCheckbox.isSelected());
			}
		});

		additiveCheckbox.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent event)
			{
				editor.getEmitter().setAdditive(additiveCheckbox.isSelected());
			}
		});

		behindCheckbox.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent event)
			{
				editor.getEmitter().setBehind(behindCheckbox.isSelected());
			}
		});

		premultipliedAlphaCheckbox.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent event)
			{
				editor.getEmitter().setPremultipliedAlpha(premultipliedAlphaCheckbox.isSelected());
			}
		});
		
		// TODO Create a generic checkbox that links to a boolean value in a ZariaObjectDefinition.

		ParticleEmitter emitter = editor.getEmitter();
		attachedCheckBox.setSelected(emitter.isAttached());
		continuousCheckbox.setSelected(emitter.isContinuous());
		alignedCheckbox.setSelected(emitter.isAligned());
		additiveCheckbox.setSelected(emitter.isAdditive());
		behindCheckbox.setSelected(emitter.isBehind());
		premultipliedAlphaCheckbox.setSelected(emitter.isPremultipliedAlpha());
	}
	
	//--------------------------------------------------------------------------
	
	@Override
	public void update(ParticleEditor editor)
	{
		// Intentionally empty.
	}		

	//--------------------------------------------------------------------------
}

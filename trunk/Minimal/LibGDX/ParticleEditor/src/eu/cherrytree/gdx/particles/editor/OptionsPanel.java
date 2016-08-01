/****************************************/
/* OptionsPanel.java						*/
/* Created on: 27-Jul-2016				*/
/* Copyright Cherry Tree Studio 2016	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.gdx.particles.editor;

import java.awt.GridBagConstraints;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;


/**
 * 
 * Branched from Particle Editor of libGDX in gdx-tools.
 */
class OptionsPanel extends EditorPanel
{
	//--------------------------------------------------------------------------

	private DefinitionBooleanCheckbox attachedCheckBox;
	private DefinitionBooleanCheckbox continuousCheckbox;
	private DefinitionBooleanCheckbox alignedCheckbox;
	private DefinitionBooleanCheckbox additiveCheckbox;
	private DefinitionBooleanCheckbox behindCheckbox;
	private DefinitionBooleanCheckbox premultipliedAlphaCheckbox;
	
	//--------------------------------------------------------------------------

	public OptionsPanel(ParticleEditor editor, String name, String description) throws NoSuchFieldException
	{
		super(null, name, description);

		JPanel contentPanel = getContentPanel();
		{
			JLabel label = new JLabel("Additive:");
			contentPanel.add(label, new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(6, 0, 0, 0), 0, 0));
		}
		{
			additiveCheckbox = new DefinitionBooleanCheckbox(editor.getEmitter().getDefinition(), "additive");
			contentPanel.add(additiveCheckbox, new GridBagConstraints(1, 1, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(6, 6, 0, 0), 0, 0));
		}
		
		{
			JLabel label = new JLabel("Attached:");
			contentPanel.add(label, new GridBagConstraints(0, 2, 1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(6, 0, 0, 0), 0, 0));
		}
		{
			attachedCheckBox = new DefinitionBooleanCheckbox(editor.getEmitter().getDefinition(), "attached");
			contentPanel.add(attachedCheckBox, new GridBagConstraints(1, 2, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(6, 6, 0, 0), 0, 0));
		}
		
		{
			JLabel label = new JLabel("Continuous:");
			contentPanel.add(label, new GridBagConstraints(0, 3, 1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(6, 0, 0, 0), 0, 0));
		}
		{
			continuousCheckbox = new DefinitionBooleanCheckbox(editor.getEmitter().getDefinition(), "continuous");
			contentPanel.add(continuousCheckbox, new GridBagConstraints(1, 3, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(6, 6, 0, 0), 0, 0));
		}
		
		{
			JLabel label = new JLabel("Aligned:");
			contentPanel.add(label, new GridBagConstraints(0, 4, 1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(6, 0, 0, 0), 0, 0));
		}
		{
			alignedCheckbox = new DefinitionBooleanCheckbox(editor.getEmitter().getDefinition(), "aligned");
			contentPanel.add(alignedCheckbox, new GridBagConstraints(1, 4, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(6, 6, 0, 0), 0, 0));
		}
		
		{
			JLabel label = new JLabel("Behind:");
			contentPanel.add(label, new GridBagConstraints(0, 5, 1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(6, 0, 0, 0), 0, 0));
		}
		{
			behindCheckbox = new DefinitionBooleanCheckbox(editor.getEmitter().getDefinition(), "behind");
			contentPanel.add(behindCheckbox, new GridBagConstraints(1, 5, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(6, 6, 0, 0), 0, 0));
		}
		
		{
			JLabel label = new JLabel("Premultiplied Alpha:");
			contentPanel.add(label, new GridBagConstraints(0, 6, 1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(6, 0, 0, 0), 0, 0));
		}
		{
			premultipliedAlphaCheckbox = new DefinitionBooleanCheckbox(editor.getEmitter().getDefinition(), "premultipliedAlpha");
			contentPanel.add(premultipliedAlphaCheckbox, new GridBagConstraints(1, 6, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(6, 6, 0, 0), 0, 0));
		}
	}
	
	//--------------------------------------------------------------------------
	
	@Override
	public void update(ParticleEditor editor)
	{
		// Intentionally empty.
	}		

	//--------------------------------------------------------------------------
}

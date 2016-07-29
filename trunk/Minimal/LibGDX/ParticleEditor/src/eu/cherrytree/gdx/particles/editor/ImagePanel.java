/****************************************/
/* ImagePanel.java						*/
/* Created on: 27-Jul-2016				*/
/* Copyright Cherry Tree Studio 2016	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.gdx.particles.editor;

import java.awt.FileDialog;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import eu.cherrytree.gdx.particles.ParticleEmitter;


/**
 * 
 * Branched from Particle Editor of libGDX in gdx-tools.
 */
class ImagePanel extends EditorPanel
{
	//--------------------------------------------------------------------------

	private JLabel imageLabel;
	private JLabel widthLabel;
	private JLabel heightLabel;
	private String lastDir;
	
	//--------------------------------------------------------------------------

	public ImagePanel(final ParticleEditor editor, String name, String description)
	{
		super(null, name, description);
		JPanel contentPanel = getContentPanel();
		{
			JButton openButton = new JButton("Open");
			contentPanel.add(openButton, new GridBagConstraints(2, 1, 1, 1, 0, 0, GridBagConstraints.NORTHWEST,
			GridBagConstraints.NONE, new Insets(0, 0, 0, 6), 0, 0));
			openButton.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent event)
				{
					FileDialog dialog = new FileDialog(editor, "Open Image", FileDialog.LOAD);
					if (lastDir != null)
						dialog.setDirectory(lastDir);
					dialog.setVisible(true);
					final String file = dialog.getFile();
					final String dir = dialog.getDirectory();
					if (dir == null || file == null || file.trim().length() == 0)
						return;
					lastDir = dir;
					try
					{
						ImageIcon icon = new ImageIcon(new File(dir, file).toURI().toURL());
						final ParticleEmitter emitter = editor.getEmitter();
						editor.setIcon(emitter, icon);
						updateIconInfo(icon);
						emitter.setImagePath(new File(dir, file).getAbsolutePath());
						emitter.setSprite(null);
					}
					catch (Exception ex)
					{
						ex.printStackTrace();
					}
				}
			});
		}
		{
			widthLabel = new JLabel();
			contentPanel.add(widthLabel, new GridBagConstraints(2, 3, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 0, 6, 6), 0, 0));
		}
		{
			heightLabel = new JLabel();
			contentPanel.add(heightLabel, new GridBagConstraints(2, 4, 1, 1, 0, 1, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 6), 0, 0));
		}
		{
			imageLabel = new JLabel();
			contentPanel.add(imageLabel, new GridBagConstraints(3, 1, 1, 3, 1, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 6, 0, 0), 0, 0));
		}
		updateIconInfo(editor.getIcon(editor.getEmitter()));
	}
	
	//--------------------------------------------------------------------------

	public final void updateIconInfo(ImageIcon icon)
	{
		if (icon != null)
		{
			imageLabel.setIcon(icon);
			widthLabel.setText("Width: " + icon.getIconWidth());
			heightLabel.setText("Height: " + icon.getIconHeight());
		}
		else
		{
			imageLabel.setIcon(null);
			widthLabel.setText("");
			heightLabel.setText("");
		}
		revalidate();
	}
	
	//--------------------------------------------------------------------------
	
	@Override
	public void update(ParticleEditor editor)
	{
		// Intentionally empty.
	}		

	//--------------------------------------------------------------------------
}

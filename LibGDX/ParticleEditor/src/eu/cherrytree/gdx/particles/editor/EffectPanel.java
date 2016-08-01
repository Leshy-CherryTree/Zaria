/****************************************/
/* EffectPanel.java						*/
/* Created on: 27-Jul-2016				*/
/* Copyright Cherry Tree Studio 2016	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.gdx.particles.editor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.JPanel;
import javax.swing.JTable;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.ListSelectionModel;

import eu.cherrytree.gdx.particles.ParticleEmitter;


/**
 * 
 * Branched from Particle Editor of libGDX in gdx-tools.
 */
public class EffectPanel extends JPanel
{
	//--------------------------------------------------------------------------

	private ParticleEditor editor;
	private JTable emitterTable;
	private DefaultTableModel emitterTableModel;
	private int editIndex;
	private String lastDir;
	
	//--------------------------------------------------------------------------

	public EffectPanel(ParticleEditor editor)
	{
		this.editor = editor;
		initializeComponents();
	}
	
	//--------------------------------------------------------------------------
	
	public int getEditIndex()
	{
		return editIndex;
	}
	
	//--------------------------------------------------------------------------

	public ParticleEmitter createNewEmitter(String name, boolean select)
	{
		ParticleEmitter emitter = ParticleEffectZoneContainer.addParticleEmitter();

		addEmitter(name, select, emitter);
		return emitter;
	}
	
	//--------------------------------------------------------------------------

	private void addEmitter(String name, boolean select, final ParticleEmitter emitter)
	{
		ArrayList<ParticleEmitter> emitters = ParticleEffectZoneContainer.getEffect().getEmitters();
		
		if (emitters.isEmpty())
		{
			emitter.setPosition(editor.getRenderer().getWorldCamera().viewportWidth / 2, editor.getRenderer().getWorldCamera().viewportHeight / 2);
		}
		else
		{
			ParticleEmitter p = emitters.get(0);
			emitter.setPosition(p.getX(), p.getY());
		}
		emitters.add(emitter);

		emitterTableModel.addRow(new Object[]
		{
			name, true
		});
		if (select)
		{
			editor.reloadRows();
			int row = emitterTableModel.getRowCount() - 1;
			emitterTable.getSelectionModel().setSelectionInterval(row, row);
		}
	}
	
	//--------------------------------------------------------------------------

	private void emitterSelected()
	{
		int row = emitterTable.getSelectedRow();
		if (row == -1)
		{
			row = editIndex;
			emitterTable.getSelectionModel().setSelectionInterval(row, row);
		}
		if (row == editIndex)
			return;
		editIndex = row;
		editor.reloadRows();
	}
	
	//--------------------------------------------------------------------------

	private void openEffect(boolean mergeIntoCurrent)
	{
//		FileDialog dialog = new FileDialog(editor, "Open Effect", FileDialog.LOAD);
//		if (lastDir != null)
//			dialog.setDirectory(lastDir);
//		dialog.setVisible(true);
//		final String file = dialog.getFile();
//		final String dir = dialog.getDirectory();
//		if (dir == null || file == null || file.trim().length() == 0)
//			return;
//		lastDir = dir;
//		ParticleEffect effect = new ParticleEffect();
//		try
//		{
//			File effectFile = new File(dir, file);
//			effect.loadEmitters(Gdx.files.absolute(effectFile.getAbsolutePath()));
//			if (mergeIntoCurrent)
//			{
//				for (ParticleEmitter emitter : effect.getEmitters())
//				{
//					addEmitter(emitter.getName(), false, emitter);
//				}
//			}
//			else
//			{
//				editor.effect = effect;
//				editor.effectFile = effectFile;
//			}
//			emitterTableModel.getDataVector().removeAllElements();
//			editor.particleData.clear();
//		}
//		catch (Exception ex)
//		{
//			System.out.println("Error loading effect: " + new File(dir, file).getAbsolutePath());
//			ex.printStackTrace();
//			JOptionPane.showMessageDialog(editor, "Error opening effect.");
//			return;
//		}
//		for (ParticleEmitter emitter : editor.effect.getEmitters())
//		{
//			emitter.setPosition(editor.worldCamera.viewportWidth / 2, editor.worldCamera.viewportHeight / 2);
//			emitterTableModel.addRow(new Object[]
//			{
//				emitter.getName(), true
//			});
//		}
//		editIndex = 0;
//		emitterTable.getSelectionModel().setSelectionInterval(editIndex, editIndex);
//		editor.reloadRows();
	}
	
	//--------------------------------------------------------------------------

	private void saveEffect()
	{
//		FileDialog dialog = new FileDialog(editor, "Save Effect", FileDialog.SAVE);
//		if (lastDir != null)
//			dialog.setDirectory(lastDir);
//		dialog.setVisible(true);
//		String file = dialog.getFile();
//		String dir = dialog.getDirectory();
//		if (dir == null || file == null || file.trim().length() == 0)
//			return;
//		lastDir = dir;
//		int index = 0;
//		File effectFile = new File(dir, file);
//
//		// save each image path as relative path to effect file directory
//		URI effectDirUri = effectFile.getParentFile().toURI();
//		for (ParticleEmitter emitter : editor.effect.getEmitters())
//		{
//			emitter.setName((String) emitterTableModel.getValueAt(index++, 0));
//			String imagePath = emitter.getImagePath();
//			if ((imagePath.contains("/") || imagePath.contains("\\")) && !imagePath.contains(".."))
//			{
//				// it's absolute, make it relative:
//				URI imageUri = new File(emitter.getImagePath()).toURI();
//				emitter.setImagePath(effectDirUri.relativize(imageUri).getPath());
//			}
//		}
//
//		File outputFile = new File(dir, file);
//		Writer fileWriter = null;
//		try
//		{
//			fileWriter = new FileWriter(outputFile);
//			editor.effect.save(fileWriter);
//		}
//		catch (Exception ex)
//		{
//			System.out.println("Error saving effect: " + outputFile.getAbsolutePath());
//			ex.printStackTrace();
//			JOptionPane.showMessageDialog(editor, "Error saving effect.");
//		}
//		finally
//		{
//			StreamUtils.closeQuietly(fileWriter);
//		}
	}
	
	//--------------------------------------------------------------------------

	private void duplicateEmitter()
	{
//		int row = emitterTable.getSelectedRow();
//		if (row == -1)
//			return;
//
//		String name = (String) emitterTableModel.getValueAt(row, 0);
//
//		addEmitter(name, true, new ParticleEmitter(ParticleEffectZoneContainer.getEffect().getEmitters().get(row)));
	}
	
	//--------------------------------------------------------------------------

	private void deleteEmitter()
	{
		if (ParticleEffectZoneContainer.getEffect().getEmitters().size() == 1)
			return;
		
		int row = emitterTable.getSelectedRow();
		
		if (row == -1)
			return;
		
		if (row <= editIndex)
		{
			int oldEditIndex = editIndex;
			editIndex = Math.max(0, editIndex - 1);
			if (oldEditIndex == row)
				editor.reloadRows();
		}
		
		//ParticleEffectZoneContainer.getEffect().getEmitters().remove(row);
		ParticleEffectZoneContainer.removeEmitter(row);
		
		emitterTableModel.removeRow(row);
		emitterTable.getSelectionModel().setSelectionInterval(editIndex, editIndex);
	}
	
	//--------------------------------------------------------------------------

	private void move(int direction)
	{
		if (direction < 0 && editIndex == 0)
			return;
		
		ArrayList<ParticleEmitter> emitters = ParticleEffectZoneContainer.getEffect().getEmitters();
		
		if (direction > 0 && editIndex == emitters.size() - 1)
			return;
		
		int insertIndex = editIndex + direction;
		
		Object name = emitterTableModel.getValueAt(editIndex, 0);
		emitterTableModel.removeRow(editIndex);
		
		ParticleEmitter emitter = emitters.remove(editIndex);
		emitterTableModel.insertRow(insertIndex, new Object[] { name });
		emitters.add(insertIndex, emitter);
		
		editIndex = insertIndex;
		emitterTable.getSelectionModel().setSelectionInterval(editIndex, editIndex);
	}
	
	//--------------------------------------------------------------------------

	private void emitterChecked(int index, boolean checked)
	{
		editor.setEnabled(ParticleEffectZoneContainer.getEffect().getEmitters().get(index), checked);
		ParticleEffectZoneContainer.getEffect().start();
	}
	
	//--------------------------------------------------------------------------

	private void initializeComponents()
	{
		setLayout(new GridBagLayout());
		{
			JPanel sideButtons = new JPanel(new GridBagLayout());
			add(sideButtons, new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
			new Insets(0, 0, 0, 0), 0, 0));
			{
				JButton newButton = new JButton("New");
				sideButtons.add(newButton, new GridBagConstraints(0, -1, 1, 1, 0, 0, GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL, new Insets(0, 0, 6, 0), 0, 0));
				newButton.addActionListener(new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent event)
					{
						createNewEmitter("Untitled", true);
					}
				});
			}
			{
				JButton newButton = new JButton("Duplicate");
				sideButtons.add(newButton, new GridBagConstraints(0, -1, 1, 1, 0, 0, GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL, new Insets(0, 0, 6, 0), 0, 0));
				newButton.addActionListener(new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent event)
					{
						duplicateEmitter();
					}
				});
			}
			{
				JButton deleteButton = new JButton("Delete");
				sideButtons.add(deleteButton, new GridBagConstraints(0, -1, 1, 1, 0, 0, GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL, new Insets(0, 0, 6, 0), 0, 0));
				deleteButton.addActionListener(new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent event)
					{
						deleteEmitter();
					}
				});
			}
			{
				sideButtons.add(new JSeparator(JSeparator.HORIZONTAL), new GridBagConstraints(0, -1, 1, 1, 0, 0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 6, 0), 0, 0));
			}
			{
				JButton saveButton = new JButton("Save");
				sideButtons.add(saveButton, new GridBagConstraints(0, -1, 1, 1, 0, 0, GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL, new Insets(0, 0, 6, 0), 0, 0));
				saveButton.addActionListener(new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent event)
					{
						saveEffect();
					}
				});
			}
			{
				JButton openButton = new JButton("Open");
				sideButtons.add(openButton, new GridBagConstraints(0, -1, 1, 1, 0, 0, GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL, new Insets(0, 0, 6, 0), 0, 0));
				openButton.addActionListener(new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent event)
					{
						openEffect(false);
					}
				});
			}
			{
				JButton mergeButton = new JButton("Merge");
				sideButtons.add(mergeButton, new GridBagConstraints(0, -1, 1, 1, 0, 0, GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL, new Insets(0, 0, 6, 0), 0, 0));
				mergeButton.addActionListener(new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent event)
					{
						openEffect(true);
					}
				});
			}
			{
				JButton upButton = new JButton("Up");
				sideButtons.add(upButton, new GridBagConstraints(0, -1, 1, 1, 0, 1, GridBagConstraints.SOUTH,
				GridBagConstraints.HORIZONTAL, new Insets(0, 0, 6, 0), 0, 0));
				upButton.addActionListener(new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent event)
					{
						move(-1);
					}
				});
			}
			{
				JButton downButton = new JButton("Down");
				sideButtons.add(downButton, new GridBagConstraints(0, -1, 1, 1, 0, 0, GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
				downButton.addActionListener(new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent event)
					{
						move(1);
					}
				});
			}
		}
		{
			JScrollPane scroll = new JScrollPane();
			add(scroll, new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0,
			0, 0, 6), 0, 0));
			{
				emitterTable = new JTable()
				{
					@Override
					public Class getColumnClass(int column)
					{
						return column == 1 ? Boolean.class : super.getColumnClass(column);
					}
				};
				emitterTable.getTableHeader().setReorderingAllowed(false);
				emitterTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
				scroll.setViewportView(emitterTable);
				emitterTableModel = new DefaultTableModel(new String[0][0], new String[]
				{
					"Emitter", ""
				});
				emitterTable.setModel(emitterTableModel);
				emitterTable.getSelectionModel().addListSelectionListener(new ListSelectionListener()
				{
					@Override
					public void valueChanged(ListSelectionEvent event)
					{
						if (event.getValueIsAdjusting())
							return;
						emitterSelected();
					}
				});
				emitterTableModel.addTableModelListener(new TableModelListener()
				{
					@Override
					public void tableChanged(TableModelEvent event)
					{
						if (event.getColumn() != 1)
							return;
						emitterChecked(event.getFirstRow(), (Boolean) emitterTable.getValueAt(event.getFirstRow(), 1));
					}
				});
			}
		}
	}
	
	//--------------------------------------------------------------------------
}

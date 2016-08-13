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
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.ListSelectionModel;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;

import eu.cherrytree.gdx.particles.ParticleEmitter;
import eu.cherrytree.zaria.serialization.ZariaObjectDefinition;
import java.lang.reflect.Field;


/**
 * 
 * Branched from Particle Editor of libGDX in gdx-tools.
 */
public class EffectPanel extends JPanel implements TableModelListener, ListSelectionListener
{
	//--------------------------------------------------------------------------

	private ParticleEditor editor;
	private JTable emitterTable;
	private DefaultTableModel emitterTableModel;
	private int editIndex;

	//--------------------------------------------------------------------------

	public EffectPanel(ParticleEditor editor)
	{
		this.editor = editor;

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
						createNewEmitter();
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
				emitterTableModel = new DefaultTableModel(new String[0][0], new String[] { "Name" , "Visible" });
				emitterTable.setModel(emitterTableModel);
				
				emitterTable.getSelectionModel().addListSelectionListener(this);
				emitterTableModel.addTableModelListener(this);
			}
		}
		
		ParticleEmitter emitter = ParticleEffectZoneContainer.getEmitter(0);

		addEmitter(emitter.getDefinition().getID(), emitter);
	}
	
	//--------------------------------------------------------------------------
	
	public int getEditIndex()
	{
		return editIndex;
	}
	
	//--------------------------------------------------------------------------

	public ParticleEmitter createNewEmitter()
	{
		ParticleEmitter emitter = ParticleEffectZoneContainer.addParticleEmitter();

		addEmitter(emitter.getDefinition().getID(), emitter);
		return emitter;
	}
	
	//--------------------------------------------------------------------------

	private void addEmitter(String name, ParticleEmitter emitter)
	{
		assert emitter != null;
		
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

		emitterTableModel.addRow(new Object[] { name, true });
		
		editor.reloadRows();
		
		int row = emitterTableModel.getRowCount() - 1;
		emitterTable.getSelectionModel().setSelectionInterval(row, row);
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

	public void openEffect()
	{
		ParticleEffectZoneContainer.openEffect(editor);
		
		for (int i = emitterTableModel.getRowCount() - 1 ; i >= 0 ; i--)
			emitterTableModel.removeRow(i);
		
		for (ParticleEmitter emitter : ParticleEffectZoneContainer.getEffect().getEmitters())
		{
			emitter.setPosition(editor.getRenderer().getWorldCamera().viewportWidth / 2, editor.getRenderer().getWorldCamera().viewportHeight / 2);
			emitterTableModel.addRow(new Object[] { emitter.getDefinition().getID(), true });
		}
		
		editIndex = 0;
		emitterTable.getSelectionModel().setSelectionInterval(editIndex, editIndex);
		editor.reloadRows();
		
		ParticleEffectZoneContainer.getEffect().start();
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

	@Override
	public void tableChanged(TableModelEvent event)
	{
		if (event.getColumn() == 0)
		{
			try
			{
				Field id_field = ZariaObjectDefinition.class.getDeclaredField("id");
				id_field.setAccessible(true);
				id_field.set(editor.getEmitter().getDefinition(), (String) emitterTable.getValueAt(event.getFirstRow(), 0));
			}
			catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException ex)
			{
				ex.printStackTrace();
			}
		}
		else if (event.getColumn() == 1)
		{
			emitterChecked(event.getFirstRow(), (Boolean) emitterTable.getValueAt(event.getFirstRow(), 1));
		}
	}
	
	//--------------------------------------------------------------------------

	@Override
	public void valueChanged(ListSelectionEvent event)
	{
		if (event.getValueIsAdjusting())
			return;

		emitterSelected();
	}
	
	//--------------------------------------------------------------------------
}

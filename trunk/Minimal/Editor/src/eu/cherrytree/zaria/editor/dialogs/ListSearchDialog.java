/****************************************/
/* ListSearchDialog.java					*/
/* Created on: 26-Jun-2014				*/
/* Copyright Cherry Tree Studio 2014	*/
/* Released under EUPL v1.1				*/
/****************************************/
package eu.cherrytree.zaria.editor.dialogs;

import java.awt.Frame;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import java.util.ArrayList;

import javax.swing.AbstractListModel;
import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;
import javax.swing.WindowConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public class ListSearchDialog extends JDialog implements ActionListener, KeyListener, ListSelectionListener
{
	//--------------------------------------------------------------------------
	
	private class FilteredModel extends AbstractListModel<Object>
	{
		Object[] objects;
		ArrayList<Object> currentArray = new ArrayList<>();
				
		public FilteredModel(Object[] objects)
		{
			this.objects = objects;
			setFilter("");
		}

		@Override
		public int getSize()
		{
			return currentArray.size();
		}
			
		@Override
		public Object getElementAt(int index)
		{
			return currentArray.get(index);
		}
		
		public final void setFilter(String filter)
		{
			currentArray.clear();
			
			for(Object obj : objects)
			{
				if (obj.toString().contains(filter))
					currentArray.add(obj);
			}
		}
	}
	
	//--------------------------------------------------------------------------

	private JButton okButton = new JButton();
	private JButton cancelButton = new JButton();
	private JList objectList = new JList();
	private JPanel panel = new JPanel();
	private JScrollPane scrollPane = new JScrollPane();
	private JTextField searchField = new JTextField();
	
	private FilteredModel model;
	
	private Object selected;
	
	//--------------------------------------------------------------------------

	public ListSearchDialog(Frame parent, String type, Object[] objects)
	{
		super(parent, true);

		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setTitle("Choose " + type);

		panel.setBorder(BorderFactory.createTitledBorder(type));

		model = new FilteredModel(objects);
		objectList.setModel(model);
		scrollPane.setViewportView(objectList);				

		GroupLayout jPanel1Layout = new GroupLayout(panel);
		panel.setLayout(jPanel1Layout);
		jPanel1Layout.setHorizontalGroup(
				jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(jPanel1Layout.createSequentialGroup()
				.addContainerGap()
				.addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addComponent(searchField)
				.addComponent(scrollPane))
				.addContainerGap()));
		jPanel1Layout.setVerticalGroup(
				jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(jPanel1Layout.createSequentialGroup()
				.addContainerGap()
				.addComponent(searchField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
				.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 396, Short.MAX_VALUE)
				.addContainerGap()));

		okButton.setText("Ok");
		cancelButton.setText("Cancel");
		
		okButton.addActionListener(this);
		cancelButton.addActionListener(this);
		searchField.addKeyListener(this);
		objectList.addListSelectionListener(this);

		GroupLayout layout = new GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(
				layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addComponent(panel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
				.addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
				.addContainerGap()
				.addComponent(cancelButton)
				.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 319, Short.MAX_VALUE)
				.addComponent(okButton)
				.addContainerGap()));
		layout.setVerticalGroup(
				layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(layout.createSequentialGroup()
				.addComponent(panel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
				.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
				.addComponent(okButton)
				.addComponent(cancelButton))
				.addContainerGap()));

		pack();
		
		Rectangle framebounds = parent.getBounds();
		Rectangle dialogbounds = getBounds();

		Rectangle newbounds = new Rectangle(framebounds.x + framebounds.width/2 - dialogbounds.width/2, 
											framebounds.y + framebounds.height/2 - dialogbounds.height/2, 
											dialogbounds.width, dialogbounds.height);

		setBounds(newbounds);
	}
	
	//--------------------------------------------------------------------------
	
	@Override
	public void actionPerformed(ActionEvent evt)
	{
		if(evt.getSource() == okButton)
		{
			selected = objectList.getSelectedValue();
			setVisible(false);
		}
		else if(evt.getSource() == cancelButton)
		{
			setVisible(false);
		}
	}
	
	//--------------------------------------------------------------------------
	
	@Override
	public void valueChanged(ListSelectionEvent evt)
	{
		if(evt.getSource() == objectList)
			searchField.setText(objectList.getSelectedValue().toString());
	}
	
	//--------------------------------------------------------------------------
	
	@Override
	public void keyTyped(KeyEvent evt)
	{
		if(evt.getSource() == searchField)
		{
			model.setFilter(searchField.getText());
			objectList.repaint();
		}
	}
	
	//--------------------------------------------------------------------------

	@Override
	public void keyPressed(KeyEvent evt)
	{
		// Intentionally empty.
	}
	
	//--------------------------------------------------------------------------

	@Override
	public void keyReleased(KeyEvent evt)
	{
		// Intentionally empty.
	}
	
	//--------------------------------------------------------------------------
	
	public Object getSelected()
	{
		return selected;
	}
	
	//--------------------------------------------------------------------------
}

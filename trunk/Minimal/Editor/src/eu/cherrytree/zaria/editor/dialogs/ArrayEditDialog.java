/****************************************/
/* ArrayEditDialog.java 				*/
/* Created on: 21-May-2014				*/
/* Copyright Cherry Tree Studio 2014	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.editor.dialogs;

import eu.cherrytree.zaria.editor.components.ArrayListRenderer;
import eu.cherrytree.zaria.editor.datamodels.ArrayListModel;
import eu.cherrytree.zaria.editor.debug.DebugConsole;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.logging.Level;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.LayoutStyle;
import javax.swing.WindowConstants;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public class ArrayEditDialog extends JDialog implements ActionListener
{
	//--------------------------------------------------------------------------

	private JPanel buttonPanel = new JPanel();
	private JButton addButton = new JButton();	
	private JButton cancelButton = new JButton();
	private JButton downButton = new JButton();
	private JButton okButton = new JButton();
	private JButton removeButton = new JButton();
	private JButton upButton = new JButton();
	
	private JPanel listPanel = new JPanel();
	private JScrollPane listScrollPane = new JScrollPane();
	private JList list = new JList();
	
	private ArrayListModel listModel;	
	private Object array;
	
	//--------------------------------------------------------------------------

	public ArrayEditDialog(JFrame parent, String arrayName, Object array, boolean showingLinks)
	{
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setMinimumSize(new Dimension(443, 462));
		setModal(true);
		setTitle("Array Editor");
		
		addButton.setIcon(new ImageIcon(getClass().getResource("/eu/cherrytree/zaria/editor/res/icons/big/add_icon.png")));
		removeButton.setIcon(new ImageIcon(getClass().getResource("/eu/cherrytree/zaria/editor/res/icons/big/remove_icon.png")));
		upButton.setIcon(new ImageIcon(getClass().getResource("/eu/cherrytree/zaria/editor/res/icons/big/up_icon.png")));
		downButton.setIcon(new ImageIcon(getClass().getResource("/eu/cherrytree/zaria/editor/res/icons/big/down_icon.png")));
		cancelButton.setText("Cancel");
		okButton.setText("Ok");
		
		addButton.addActionListener(this);
		removeButton.addActionListener(this);
		upButton.addActionListener(this);
		downButton.addActionListener(this);
		cancelButton.addActionListener(this);
		okButton.addActionListener(this);
		
		GroupLayout buttonPanelLayout = new GroupLayout(buttonPanel);
		buttonPanel.setLayout(buttonPanelLayout);
		buttonPanelLayout.setHorizontalGroup(
				buttonPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(buttonPanelLayout.createSequentialGroup()
				.addComponent(addButton, GroupLayout.PREFERRED_SIZE, 48, GroupLayout.PREFERRED_SIZE)
				.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
				.addComponent(removeButton, GroupLayout.PREFERRED_SIZE, 48, GroupLayout.PREFERRED_SIZE)
				.addGap(18, 18, 18)
				.addComponent(upButton, GroupLayout.PREFERRED_SIZE, 48, GroupLayout.PREFERRED_SIZE)
				.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
				.addComponent(downButton, GroupLayout.PREFERRED_SIZE, 48, GroupLayout.PREFERRED_SIZE)
				.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 94, Short.MAX_VALUE)
				.addComponent(cancelButton)
				.addGap(18, 18, 18)
				.addComponent(okButton)));
		buttonPanelLayout.setVerticalGroup(
				buttonPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(buttonPanelLayout.createSequentialGroup()
				.addGap(0, 0, Short.MAX_VALUE)
				.addGroup(buttonPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addComponent(addButton, GroupLayout.Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, 31, GroupLayout.PREFERRED_SIZE)
				.addComponent(upButton, GroupLayout.Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, 31, GroupLayout.PREFERRED_SIZE)
				.addComponent(downButton, GroupLayout.Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, 31, GroupLayout.PREFERRED_SIZE)
				.addComponent(removeButton, GroupLayout.Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, 31, GroupLayout.PREFERRED_SIZE)
				.addGroup(GroupLayout.Alignment.TRAILING, buttonPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
				.addComponent(cancelButton)
				.addComponent(okButton)))));

		listPanel.setBorder(BorderFactory.createTitledBorder(arrayName));

		listModel = new ArrayListModel(array);
		list.setModel(listModel);
		list.setFixedCellHeight(14);	
		list.setFont(new Font("Monospaced", Font.PLAIN, 12));
		list.setCellRenderer(new ArrayListRenderer(showingLinks));
		
		listScrollPane.setViewportView(list);

		GroupLayout jPanel2Layout = new GroupLayout(listPanel);
		listPanel.setLayout(jPanel2Layout);
		jPanel2Layout.setHorizontalGroup(
				jPanel2Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addComponent(listScrollPane));
		jPanel2Layout.setVerticalGroup(
				jPanel2Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addComponent(listScrollPane, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 352, Short.MAX_VALUE));

		GroupLayout layout = new GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(
				layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
				.addContainerGap()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
				.addComponent(buttonPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
				.addComponent(listPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
				.addContainerGap()));
		layout.setVerticalGroup(
				layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(layout.createSequentialGroup()
				.addContainerGap()
				.addComponent(listPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
				.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
				.addComponent(buttonPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				.addContainerGap()));

		pack();
		
		Rectangle framebounds = parent.getBounds();
		Rectangle dialogbounds = getBounds();

		Rectangle newbounds = new Rectangle(framebounds.x + framebounds.width/2 - dialogbounds.width/2, 
											framebounds.y + framebounds.height/2 - dialogbounds.height/2, 
											dialogbounds.width, dialogbounds.height);
		
		setBounds(newbounds);	
		
		setVisible(true);
	}
	
	//--------------------------------------------------------------------------
	
	@Override
	public void actionPerformed(ActionEvent evt)
	{
		if(evt.getSource() == okButton)
		{
			array = listModel.getArray();
			setVisible(false);
		}
		else if(evt.getSource() == cancelButton)
		{
			setVisible(false);
		}
		else
		{
			int index = list.getSelectedIndex();
			
			if(evt.getSource() == upButton)
			{				
				listModel.moveUp(index);
				
				if(index > 0)
					list.setSelectedIndex(index-1);
			}
			else if(evt.getSource() == downButton)
			{
				listModel.moveDown(index);
				
				if(index >= 0 && index < listModel.getSize() - 1)
					list.setSelectedIndex(index+1);
			}
			else if(evt.getSource() == addButton)
			{
				try
				{
					listModel.add(index);
				}
				catch (InstantiationException | IllegalAccessException ex)
				{
					DebugConsole.logger.log(Level.SEVERE, null, ex);
				}
			}
			else if(evt.getSource() == removeButton)
			{
				listModel.remove(index);
				
				if(index == listModel.getSize())
					list.setSelectedIndex(index-1);
			}
		}
		
	}
	
	//--------------------------------------------------------------------------
	
	public Object getArray()
	{
		return array;
	}
	
	//--------------------------------------------------------------------------
}

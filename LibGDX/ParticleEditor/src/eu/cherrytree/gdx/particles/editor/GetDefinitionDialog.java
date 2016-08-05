/****************************************/
/* GetDefinitionDialog.java				*/
/* Created on: 01-Aug-2016				*/
/* Copyright Cherry Tree Studio 2016	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.gdx.particles.editor;

import eu.cherrytree.gdx.particles.editor.datamodel.DefinitionListModel;
import eu.cherrytree.gdx.particles.editor.datamodel.DefinitionWrapper;
import eu.cherrytree.gdx.particles.editor.datamodel.FileTree;
import eu.cherrytree.gdx.particles.editor.datamodel.FileWrapper;

import eu.cherrytree.zaria.serialization.ZariaObjectDefinition;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.LayoutStyle;
import javax.swing.WindowConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public class GetDefinitionDialog<Definition extends ZariaObjectDefinition> extends JDialog implements ActionListener, TreeSelectionListener, ListSelectionListener
{
	//--------------------------------------------------------------------------

	private JTree assetTree;
	private JButton cancelButton;
	private JList<DefinitionWrapper> definitionList;
	private JPanel buttonPanel;
	private JScrollPane treeScrollPane;
	private JScrollPane listScrollPane;
	private JSplitPane splitPane;
	private JButton openButton;
	
	private File file;
	private Definition definition;
	private Class<Definition> requestedType;
	
	private DefinitionListModel listModel = new DefinitionListModel();
	
	//--------------------------------------------------------------------------

	public GetDefinitionDialog(JFrame parent, Class<Definition> cls)
	{
		super(parent, true);
		
		requestedType = cls;
		
		setTitle("Looking for " + cls.getSimpleName());

		splitPane = new JSplitPane();
		treeScrollPane = new JScrollPane();
		assetTree = new JTree();
		listScrollPane = new JScrollPane();
		definitionList = new JList<>();
		buttonPanel = new JPanel();
		openButton = new JButton();
		cancelButton = new JButton();

		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		treeScrollPane.setViewportView(assetTree);
		
		assetTree.setModel(new FileTree(ParticleEffectZoneContainer.getAssetPath()));
		assetTree.addTreeSelectionListener(this);

		splitPane.setLeftComponent(treeScrollPane);

		definitionList.setModel(listModel);
		definitionList.addListSelectionListener(this);
		listScrollPane.setViewportView(definitionList);

		splitPane.setRightComponent(listScrollPane);

		openButton.setText("Open");
		openButton.addActionListener(this);
		openButton.setEnabled(false);
		
		cancelButton.setText("Cancel");
		cancelButton.addActionListener(this);

		GroupLayout button_panel_layout = new GroupLayout(buttonPanel);
		buttonPanel.setLayout(button_panel_layout);
		button_panel_layout.setHorizontalGroup(
			button_panel_layout.createParallelGroup(GroupLayout.Alignment.LEADING)
			.addGroup(GroupLayout.Alignment.TRAILING, button_panel_layout.createSequentialGroup()
				.addContainerGap()
				.addComponent(cancelButton)
				.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
				.addComponent(openButton)
				.addContainerGap())
		);
		button_panel_layout.setVerticalGroup(
			button_panel_layout.createParallelGroup(GroupLayout.Alignment.LEADING)
			.addGroup(button_panel_layout.createSequentialGroup()
				.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
				.addGroup(button_panel_layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
					.addComponent(cancelButton)
					.addComponent(openButton))
				.addContainerGap())
		);

		GroupLayout layout = new GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(
			layout.createParallelGroup(GroupLayout.Alignment.LEADING)
			.addComponent(splitPane, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
			.addComponent(buttonPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
		);
		layout.setVerticalGroup(
			layout.createParallelGroup(GroupLayout.Alignment.LEADING)
			.addGroup(layout.createSequentialGroup()
				.addComponent(splitPane, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
				.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
				.addComponent(buttonPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
		);

		pack();
	}
	
	//--------------------------------------------------------------------------

	public File getFile()
	{
		return file;
	}
	
	//--------------------------------------------------------------------------

	public Definition getDefinition()
	{
		return definition;
	}
	
	//--------------------------------------------------------------------------

	@Override
	public void actionPerformed(ActionEvent event)
	{
		if (event.getSource() == openButton)
		{
			assert file != null;
			assert definition != null;
			
			setVisible(false);
		}
		else if (event.getSource() == cancelButton)
		{
			file = null;
			definition = null;
			
			setVisible(false);
		}
	}
	
	//--------------------------------------------------------------------------

	@Override
	public void valueChanged(TreeSelectionEvent event)
	{
		if (event.getSource() == assetTree)
		{
			FileWrapper wrapper = (FileWrapper) assetTree.getSelectionPath().getLastPathComponent();
			File selected_file = wrapper.getFile();
			
			if (selected_file.isFile())
			{
				try
				{
					ZariaObjectDefinition[] definitions = EditorApplication.getSerializer().readValue(new FileInputStream(selected_file), ZariaObjectDefinition[].class);

					openButton.setEnabled(false);
					definitionList.clearSelection();
					listModel.clear();

					for (ZariaObjectDefinition def : definitions)
					{
						if (requestedType.isAssignableFrom(def.getClass()))
							listModel.addDefinition(def);
					}

					listModel.update();

					file = selected_file;
				}
				catch (IOException ex)
				{
					ex.printStackTrace();
				}
			}
		}
	}
	
	//--------------------------------------------------------------------------

	@Override
	public void valueChanged(ListSelectionEvent event)
	{
		if (event.getSource() == definitionList)
		{
			DefinitionWrapper wrapper = definitionList.getSelectedValue();
			
			if (wrapper != null)
			{
				definition = (Definition) wrapper.getDefinition();
				openButton.setEnabled(true);
			}
			else
			{
				openButton.setEnabled(false);
			}
		}
	}
}

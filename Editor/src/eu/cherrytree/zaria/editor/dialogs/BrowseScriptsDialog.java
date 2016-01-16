/****************************************/
/* BrowseScriptsDialog.java 			*/
/* Created on: 02-Jun-2014				*/
/* Copyright Cherry Tree Studio 2014	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.editor.dialogs;

import eu.cherrytree.zaria.editor.EditorApplication;
import eu.cherrytree.zaria.editor.datamodels.file.DirectoryTreeModel;
import eu.cherrytree.zaria.editor.datamodels.file.FileNameWrapper;
import eu.cherrytree.zaria.editor.document.ZoneDocument;
import java.awt.Component;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.LayoutStyle;
import javax.swing.WindowConstants;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeCellRenderer;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public class BrowseScriptsDialog extends JDialog implements ActionListener, TreeSelectionListener
{
	//--------------------------------------------------------------------------
	
	private class ScriptsFilenameFilter implements FilenameFilter
	{
		@Override
		public boolean accept(File dir, String name)
		{
			return ZoneDocument.isScript(name.toLowerCase());
		}
	}
	
	//--------------------------------------------------------------------------
	
	private class ScriptsDirectoryFilter implements FileFilter
	{
		private ScriptsFilenameFilter filenameFilter = new ScriptsFilenameFilter();

		@Override
		public boolean accept(File file)
		{
			if(file.isHidden())
			{
				return false;
			}
			else if(file.isFile())
			{
				return true;
			}
			else
			{
				String[] list = file.list(filenameFilter);													
				return list != null && list.length > 0;
			}			
		}		
	}
	
	//--------------------------------------------------------------------------
	
	private class ScriptTreeCellRenderer extends DefaultTreeCellRenderer
	{
		@Override
		public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus)
		{
			super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

			if(leaf)
				setIcon(EditorApplication.icons[6]);

			return this;
		}
	}
	
	//--------------------------------------------------------------------------

	private JButton cancelButton = new JButton();
	private JScrollPane scriptsTreeScrollPane = new JScrollPane();
	private JButton okButton = new JButton();
	private JTree scriptsTree = new JTree();
	private JPanel scriptsTreePanel = new JPanel();
	private String chosenScript = new String();
	
	//--------------------------------------------------------------------------

	public BrowseScriptsDialog(JFrame parent, String path)
	{
		super(parent, true);
		
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setTitle("Browse Scripts");
		setModal(true);
		setType(Window.Type.UTILITY);

		cancelButton.setText("Cancel");
		cancelButton.addActionListener(this);

		okButton.setText("Ok");
		okButton.addActionListener(this);
		okButton.setEnabled(false);
		
		scriptsTreePanel.setBorder(BorderFactory.createTitledBorder("Scripts"));

		scriptsTreeScrollPane.setViewportView(scriptsTree);
		
		scriptsTree.addTreeSelectionListener(this);
		scriptsTree.setModel(new DirectoryTreeModel(new ScriptsDirectoryFilter(), path));
		scriptsTree.setRootVisible(false);
		scriptsTree.setCellRenderer(new ScriptTreeCellRenderer());

		GroupLayout scriptsTreePanelLayout = new GroupLayout(scriptsTreePanel);
		scriptsTreePanel.setLayout(scriptsTreePanelLayout);
		scriptsTreePanelLayout.setHorizontalGroup(
				scriptsTreePanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addComponent(scriptsTreeScrollPane));
		scriptsTreePanelLayout.setVerticalGroup(
				scriptsTreePanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(scriptsTreePanelLayout.createSequentialGroup()
				.addComponent(scriptsTreeScrollPane, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				.addGap(0, 0, Short.MAX_VALUE)));

		GroupLayout layout = new GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(
				layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
				.addContainerGap()
				.addComponent(cancelButton)
				.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 291, Short.MAX_VALUE)
				.addComponent(okButton)
				.addContainerGap())
				.addComponent(scriptsTreePanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));
		layout.setVerticalGroup(
				layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(layout.createSequentialGroup()
				.addComponent(scriptsTreePanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
				.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
				.addComponent(cancelButton)
				.addComponent(okButton))
				.addGap(10, 10, 10)));

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
	
	public String getScript()
	{
		return chosenScript;
	}
	
	//--------------------------------------------------------------------------

	@Override
	public void actionPerformed(ActionEvent evt)
	{
		if(evt.getSource() == okButton)
		{
			setVisible(false);
		}
		else if(evt.getSource() == cancelButton)
		{
			chosenScript = "";
			setVisible(false);
		}
	}
	
	//--------------------------------------------------------------------------

	@Override
	public void valueChanged(TreeSelectionEvent evt)
	{
		FileNameWrapper wrapper = (FileNameWrapper) evt.getPath().getLastPathComponent();
		
		if(wrapper.getFile().isFile())
		{
			okButton.setEnabled(true);
			chosenScript = wrapper.getFile().getAbsolutePath().replace(EditorApplication.getScriptsLocation(), "");
		}
		else
		{
			okButton.setEnabled(false);
			chosenScript = "";
		}
	}
	
	//--------------------------------------------------------------------------
}

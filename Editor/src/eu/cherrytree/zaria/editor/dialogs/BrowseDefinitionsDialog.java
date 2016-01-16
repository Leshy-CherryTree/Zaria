/****************************************/
/* BrowseDefinitionsDialog.java			*/
/* Created on: 27-Apr-2014				*/
/* Copyright Cherry Tree Studio 2014	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.editor.dialogs;

import eu.cherrytree.zaria.editor.components.ZoneFileListCellRenderer;
import eu.cherrytree.zaria.editor.components.ObjectDefinitionCellRenderer;
import eu.cherrytree.zaria.editor.datamodels.definitions.ObjectDefinitionWrapper;
import eu.cherrytree.zaria.editor.datamodels.definitions.ObjectDefinitonTreeModel;
import eu.cherrytree.zaria.editor.datamodels.file.DirectoryFileFilter;
import eu.cherrytree.zaria.editor.datamodels.file.DirectoryTreeModel;
import eu.cherrytree.zaria.editor.datamodels.file.ZoneFileListModel;
import eu.cherrytree.zaria.editor.datamodels.file.ZoneFileNameFilter;
import eu.cherrytree.zaria.editor.listeners.FileListSelectionListener;
import eu.cherrytree.zaria.editor.listeners.FileViewController;
import eu.cherrytree.zaria.serialization.ZariaObjectDefinition;

import java.awt.Frame;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.UUID;
import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.LayoutStyle;
import javax.swing.WindowConstants;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;


/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public class BrowseDefinitionsDialog extends JDialog implements ActionListener
{
	//--------------------------------------------------------------------------
	
    private JPanel buttonPanel = new JPanel();
    private JButton cancelButton = new JButton();
    private JButton chooseButton = new JButton();
    private JTree directoryTree = new JTree();
    private JList fileList = new JList();
    private JPanel fileViewPanel = new JPanel();
    private JScrollPane directoryTreeScrollPane = new JScrollPane();
    private JScrollPane fileListScorllPane = new JScrollPane();
    private JScrollPane objectsViewScrollPane = new JScrollPane();
    private JSplitPane filewViewSplitPane = new JSplitPane();
    private JTree objectTree = new JTree();
    private JPanel objectViewPanel = new JPanel();
	
	private UUID chosenID;
	private UUID selectedID;
	
	//--------------------------------------------------------------------------

	public BrowseDefinitionsDialog(Frame parent, Class<? extends ZariaObjectDefinition> baseClass, String ... paths)
	{
		super(parent, true);

		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setTitle("Browse Defintions");
		setResizable(false);

		fileViewPanel.setBorder(BorderFactory.createTitledBorder("Zone Files"));

		filewViewSplitPane.setDividerLocation(250);
		filewViewSplitPane.setDividerSize(0);

		directoryTreeScrollPane.setViewportView(directoryTree);

		filewViewSplitPane.setLeftComponent(directoryTreeScrollPane);
		fileListScorllPane.setViewportView(fileList);

		filewViewSplitPane.setRightComponent(fileListScorllPane);

		GroupLayout fileViewPanelLayout = new GroupLayout(fileViewPanel);
		fileViewPanel.setLayout(fileViewPanelLayout);
		fileViewPanelLayout.setHorizontalGroup(
				fileViewPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addComponent(filewViewSplitPane, GroupLayout.DEFAULT_SIZE, 600, Short.MAX_VALUE));
		fileViewPanelLayout.setVerticalGroup(
				fileViewPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addComponent(filewViewSplitPane, GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE));

		chooseButton.setText("Choose");
		chooseButton.setEnabled(false);	
		chooseButton.addActionListener(this);
		
		cancelButton.setText("Cancel");
		cancelButton.addActionListener(this);

		GroupLayout buttonPanelLayout = new GroupLayout(buttonPanel);
		buttonPanel.setLayout(buttonPanelLayout);
		buttonPanelLayout.setHorizontalGroup(
				buttonPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(GroupLayout.Alignment.TRAILING, buttonPanelLayout.createSequentialGroup()
				.addContainerGap()
				.addComponent(cancelButton)
				.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
				.addComponent(chooseButton)
				.addContainerGap()));
		buttonPanelLayout.setVerticalGroup(
				buttonPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(buttonPanelLayout.createSequentialGroup()
				.addContainerGap()
				.addGroup(buttonPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
				.addComponent(chooseButton)
				.addComponent(cancelButton))
				.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));

		objectViewPanel.setBorder(BorderFactory.createTitledBorder("Object Definitions"));

		objectsViewScrollPane.setViewportView(objectTree);

		GroupLayout objectViewPanelLayout = new GroupLayout(objectViewPanel);
		objectViewPanel.setLayout(objectViewPanelLayout);
		objectViewPanelLayout.setHorizontalGroup(
				objectViewPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addComponent(objectsViewScrollPane, GroupLayout.DEFAULT_SIZE, 428, Short.MAX_VALUE));
		objectViewPanelLayout.setVerticalGroup(
				objectViewPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addComponent(objectsViewScrollPane, GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE));

		GroupLayout layout = new GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(
				layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(layout.createSequentialGroup()
				.addContainerGap()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(layout.createSequentialGroup()
				.addComponent(fileViewPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
				.addComponent(objectViewPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
				.addComponent(buttonPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
				.addContainerGap()));
		layout.setVerticalGroup(
				layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
				.addGroup(GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
				.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
				.addComponent(fileViewPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
				.addComponent(objectViewPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
				.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
				.addComponent(buttonPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				.addGap(10, 10, 10)));

		ZoneFileNameFilter fileFilter = new ZoneFileNameFilter();
		DirectoryFileFilter directoryFilter = new DirectoryFileFilter(false, fileFilter);
		
		directoryTree.setModel(new DirectoryTreeModel(directoryFilter, paths));
		directoryTree.setRootVisible(false);
		
		ZoneFileListModel fileListModel = new ZoneFileListModel(baseClass, fileFilter, false);
		
		fileList.setModel(fileListModel);		
		fileList.setCellRenderer(new ZoneFileListCellRenderer());
				
		directoryTree.addTreeSelectionListener(new FileViewController(fileListModel));
		
		ObjectDefinitonTreeModel objectDefinitonTreeModel = new ObjectDefinitonTreeModel(baseClass, objectTree);
		
		objectTree.setModel(objectDefinitonTreeModel);
		objectTree.setCellRenderer(new ObjectDefinitionCellRenderer());
		objectTree.setRootVisible(false);
		
		objectTree.addTreeSelectionListener(new TreeSelectionListener() {

			@Override
			public void valueChanged(TreeSelectionEvent e)
			{
				Object selected = objectTree.getLastSelectedPathComponent();
				
				if(selected != null && selected instanceof ObjectDefinitionWrapper)
				{
					ObjectDefinitionWrapper wrapper = (ObjectDefinitionWrapper) selected;
					selectedID = wrapper.getUUID();
					
					chooseButton.setEnabled(true);
				}
				else
				{
					selectedID = null;
					chooseButton.setEnabled(false);
				}				
			}
		});
		
		fileList.addListSelectionListener(new FileListSelectionListener(objectDefinitonTreeModel));
		
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
		if(evt.getSource() == cancelButton)
		{
			setVisible(false);
		}
		else if(evt.getSource() == chooseButton)
		{
			chosenID = selectedID;
			setVisible(false);
		}			
	}

	//--------------------------------------------------------------------------
	
	public UUID getChosenID()
	{
		return chosenID;
	}
	
	//--------------------------------------------------------------------------
}

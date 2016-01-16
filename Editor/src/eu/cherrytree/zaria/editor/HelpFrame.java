/****************************************/
/* HelpFrame.java						*/
/* Created on: 01-Jul-2014				*/
/* Copyright Cherry Tree Studio 2014	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.editor;

import eu.cherrytree.zaria.editor.classlist.ZoneClassList;
import eu.cherrytree.zaria.editor.datamodels.help.HelpTopic;
import eu.cherrytree.zaria.editor.datamodels.help.HelpTopicsTreeModel;
import eu.cherrytree.zaria.editor.debug.DebugConsole;
import eu.cherrytree.zaria.editor.help.HelpFileList;

import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.logging.Level;

import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.WindowConstants;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeCellRenderer;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public class HelpFrame extends JFrame implements HyperlinkListener, TreeSelectionListener, PropertyChangeListener
{
	//--------------------------------------------------------------------------

	private JEditorPane textArea = new JEditorPane();
	private JScrollPane treeScrollPane = new JScrollPane();
	private JScrollPane textAreaScrollPane = new JScrollPane();
	private JSplitPane splitPane = new JSplitPane();
	private JTree helpTopicsTree = new JTree();
	
	//--------------------------------------------------------------------------

	public HelpFrame(ZoneClassList classList)
	{
		setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		setTitle("Zone Editor Help");

		treeScrollPane.setViewportView(helpTopicsTree);
		splitPane.setLeftComponent(treeScrollPane);
		textAreaScrollPane.setViewportView(textArea);
		splitPane.setRightComponent(textAreaScrollPane);
		
		splitPane.setDividerLocation(200);

		GroupLayout layout = new GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(
				layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addComponent(splitPane));
		layout.setVerticalGroup(
				layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addComponent(splitPane, GroupLayout.DEFAULT_SIZE, 576, Short.MAX_VALUE));

		textArea.addPropertyChangeListener(this);
		
		try
		{
			textArea.setEditable(false);
			textArea.setOpaque(false);
			textArea.setEditorKit(JEditorPane.createEditorKitForContentType("text/html"));			
			textArea.addHyperlinkListener(this);				
			
			textArea.setPage(HelpFrame.class.getResource("/eu/cherrytree/zaria/editor/res/help/index.html"));
		}
		catch (IOException ex)
		{
			DebugConsole.logger.log(Level.SEVERE, null, ex);
		}
		
		helpTopicsTree.setModel(new HelpTopicsTreeModel(HelpFileList.helpfiles, "/eu/cherrytree/zaria/editor/res/", classList.getScriptFunctions()));
		helpTopicsTree.setRootVisible(false);
		helpTopicsTree.addTreeSelectionListener(this);

		DefaultTreeCellRenderer renderer = (DefaultTreeCellRenderer) helpTopicsTree.getCellRenderer();
		
		renderer.setClosedIcon(new ImageIcon(HelpFrame.class.getResource("/eu/cherrytree/zaria/editor/res/icons/small/bookmark_book.png")));
		renderer.setOpenIcon(new ImageIcon(HelpFrame.class.getResource("/eu/cherrytree/zaria/editor/res/icons/small/bookmark_book_open.png")));
		renderer.setLeafIcon(new ImageIcon(HelpFrame.class.getResource("/eu/cherrytree/zaria/editor/res/icons/small/bookmark_document.png")));
		
		pack();
		
		setSize(1024, 768);
		
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation((dim.width-getWidth())/2, (dim.height-getHeight())/2);	
	}
	
	//--------------------------------------------------------------------------
	
	private boolean isLocal(String url)
	{
		for(String string : HelpFileList.helpfiles)
		{
			if(url.endsWith(string))
				return true;
		}
		
		return false;
	}

	//--------------------------------------------------------------------------
	
	private boolean isGenerated(String url)
	{
		return url.startsWith("file:/generated/");
	}
	
	//--------------------------------------------------------------------------

	@Override
	public void hyperlinkUpdate(HyperlinkEvent e)
	{
		if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED)
		{				
			try
			{
				if(isLocal(e.getURL().toString()))
					textArea.setPage(e.getURL());
				else if(isGenerated(e.getURL().toString()))
				{
					// TODO Implement generation and showing of generated help content.
				}
				else					
					Desktop.getDesktop().browse(e.getURL().toURI());
			}
			catch (IOException | URISyntaxException ex)
			{
				DebugConsole.logger.log(Level.SEVERE, null, ex);
			}								
		}
	}
	
	//--------------------------------------------------------------------------

	@Override
	public void valueChanged(TreeSelectionEvent e)
	{		
		try
		{
			if(HelpTopic.class.isAssignableFrom(e.getPath().getLastPathComponent().getClass()))
			{
				HelpTopic topic = ((HelpTopic) e.getPath().getLastPathComponent());
				
				if(topic.getUrl() != null)
					textArea.setPage(topic.getUrl());
				else if(topic.getContent() != null)
					textArea.setText(topic.getContent());
			}
		}
		catch (IOException ex)
		{
			DebugConsole.logger.log(Level.SEVERE, null, ex);
		}
	}
	
	//--------------------------------------------------------------------------

	@Override
	public void propertyChange(PropertyChangeEvent evt)
	{
		if(evt.getSource() == textArea && evt.getPropertyName().equals("page"))
			setTitle("Zone Editor Help - " + textArea.getDocument().getProperty("title"));		
	}
	
	//--------------------------------------------------------------------------
}

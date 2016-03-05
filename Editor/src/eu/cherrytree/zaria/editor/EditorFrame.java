/****************************************/
/* EditorFrame.java						*/
/* Created on: 02-May-2013				*/
/* Copyright Cherry Tree Studio 2013		*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.editor;

import eu.cherrytree.zaria.debug.DebugManager;

import eu.cherrytree.zaria.editor.classlist.ZoneClassList;
import eu.cherrytree.zaria.editor.components.ZoneFileListCellRenderer;
import eu.cherrytree.zaria.editor.listeners.FileViewController;
import eu.cherrytree.zaria.editor.datamodels.file.FileNameWrapper;
import eu.cherrytree.zaria.editor.datamodels.file.DirectoryTreeModel;
import eu.cherrytree.zaria.editor.datamodels.file.FileListModel;
import eu.cherrytree.zaria.editor.dialogs.AboutDialog;
import eu.cherrytree.zaria.editor.dialogs.FindDialog;
import eu.cherrytree.zaria.editor.dialogs.GoToLineDialog;
import eu.cherrytree.zaria.editor.document.DocumentManager;
import eu.cherrytree.zaria.editor.document.ZoneDocument;
import eu.cherrytree.zaria.editor.listeners.CursorPositionListener;
import eu.cherrytree.zaria.editor.listeners.DebugConsoleParentListener;
import eu.cherrytree.zaria.editor.listeners.ZoneDocumentListener;
import eu.cherrytree.zaria.editor.components.PaletteCellRenderer;
import eu.cherrytree.zaria.editor.database.DataBase;
import eu.cherrytree.zaria.editor.datamodels.file.AssetFileNameFilter;
import eu.cherrytree.zaria.editor.datamodels.file.DirectoryFileFilter;
import eu.cherrytree.zaria.editor.datamodels.file.FileTransferHandler;
import eu.cherrytree.zaria.editor.datamodels.file.ScriptImportTransferHandler;
import eu.cherrytree.zaria.editor.debug.DebugConsole;
import eu.cherrytree.zaria.editor.document.TextEditorState;
import eu.cherrytree.zaria.editor.datamodels.palette.PaletteTransferHandler;
import eu.cherrytree.zaria.editor.datamodels.palette.ScriptPaletteDataModel;
import eu.cherrytree.zaria.editor.datamodels.palette.ZonePaletteDataModel;
import eu.cherrytree.zaria.editor.dialogs.ExtendedInformationDialog;
import eu.cherrytree.zaria.editor.dialogs.TextureAtlasDialog;
import eu.cherrytree.zaria.editor.document.GraphEditorState;
import eu.cherrytree.zaria.editor.scripting.JarCreator;
import eu.cherrytree.zaria.editor.properties.Property;
import eu.cherrytree.zaria.editor.properties.editors.ZonePropertyEditorFactory;
import eu.cherrytree.zaria.editor.properties.propertysheet.PropertySheetPanel;
import eu.cherrytree.zaria.editor.properties.renderers.ZonePropertyRendererFactory;
import eu.cherrytree.zaria.editor.serialization.Serializer;
import eu.cherrytree.zaria.serialization.ValidationException;
import eu.cherrytree.zaria.serialization.ZariaObjectDefinitionLibrary;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.DecimalFormat;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import javax.imageio.ImageIO;

import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.LayoutStyle;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.WindowConstants;
import javax.swing.plaf.basic.BasicSplitPaneUI;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public class EditorFrame extends JFrame implements DebugConsoleParentListener, ActionListener, CursorPositionListener, ZoneDocumentListener
{	
	//--------------------------------------------------------------------------
	
	private static final long serialVersionUID = 1L;
	
	//--------------------------------------------------------------------------
	
	private class UpdateMemoryLabel extends TimerTask
	{
		private DecimalFormat format = new DecimalFormat("#.00");
		
		@Override
		public void run()
		{			
			float freemem = Runtime.getRuntime().freeMemory();
			freemem /= 1024.0f * 1024.0f;
			
			float totalmem = Runtime.getRuntime().totalMemory();
			totalmem /= 1024.0f * 1024.0f;
			
			float percent = 100.0f * (freemem / totalmem);
					
			memoryLabel.setText("Free memory: " + format.format(percent) + "% (" + format.format(freemem) + "MB / " + format.format(totalmem) + "MB)");
			
			if (percent < 25.0f)
				memoryLabel.setForeground(Color.RED);
			else
				memoryLabel.setForeground(Color.LIGHT_GRAY);
		}
	}
	
	//--------------------------------------------------------------------------
	
	private class HideLeftPaneMouseAdapter extends MouseAdapter
	{
		@Override
		public void mouseClicked(MouseEvent e)
		{
			super.mouseClicked(e);
			
			if (splitPaneMain.getLeftComponent() == null)
			{
				ZoneDocument.EditType edit_type = ZoneDocument.EditType.TEXT_EDIT;
				ZoneDocument.DocumentType doc_type = ZoneDocument.DocumentType.ZONE;
				
				if (documentManager.getCurrentDocument() != null)
				{
					edit_type = documentManager.getCurrentDocument().getEditType();
					doc_type = documentManager.getCurrentDocument().getDocumentType();
				}
				
				showLeftPane(doc_type, edit_type);
				setLeftDivider();
			}
			else
				hideLeftPane();
		}	
	}
	
	//--------------------------------------------------------------------------
	
	private class HideRightPaneMouseAdapter extends MouseAdapter
	{
		@Override
		public void mouseClicked(MouseEvent e)
		{
			super.mouseClicked(e);
			
			if (splitPaneRight.getRightComponent() == null)
			{
				if (	documentManager.getCurrentDocument().getDocumentType() != ZoneDocument.DocumentType.ZONE_LIBRARY)
				{
					ZoneDocument.EditType edit_type = ZoneDocument.EditType.TEXT_EDIT;
					ZoneDocument.DocumentType doc_type = ZoneDocument.DocumentType.ZONE;

					if (documentManager.getCurrentDocument() != null)
					{
						edit_type = documentManager.getCurrentDocument().getEditType();
						doc_type = documentManager.getCurrentDocument().getDocumentType();
					}

					showRightPane(doc_type, edit_type);
					setRightDivider();
				}
			}
			else
				hideRightPane();
		}	
	}
	
	//--------------------------------------------------------------------------
	
	private class FileListMouseAdapter extends MouseAdapter
	{
		@Override
		public void mouseClicked(MouseEvent evt)
		{
			JList list = (JList) evt.getSource();

			if (evt.getButton() == MouseEvent.BUTTON1 && evt.getClickCount() == 2 && list.getSelectedIndex() >= 0)
			{
				int index = list.locationToIndex(evt.getPoint());
				FileNameWrapper filewrap = (FileNameWrapper) list.getModel().getElementAt(index);
				String path = filewrap.getFile().getAbsolutePath();

				if (ZoneDocument.isZone(path) || ZoneDocument.isScript(path))
					documentManager.openDocument(filewrap.getFile());
			}
		}

		private void openPopupMenu(JList list, int x, int y, int index)
		{
			FileNameWrapper filewrap = (FileNameWrapper) list.getModel().getElementAt(index);
			String path = filewrap.getFile().getAbsolutePath();

			popupMenu.removeAll();		

			if (ZoneDocument.isZone(path))
			{
				if (!documentManager.isFileOpened(path))
				{
					popupMenu.add(new OpenFileMenuItem(filewrap.getFile()));																		
					popupMenu.add(new DeleteFileMenuItem(filewrap.getFile()));						
					popupMenu.show(list, x, y);
				}
			}
			else if (ZoneDocument.isScript(path))
			{
				if (!documentManager.isFileOpened(path))
				{
					popupMenu.add(new OpenFileMenuItem(filewrap.getFile()));																		
					popupMenu.add(new DeleteFileMenuItem(filewrap.getFile()));
				}
			}
			else
			{
				popupMenu.add(new DeleteFileMenuItem(filewrap.getFile()));
				popupMenu.show(list, x, y);
			}

			list.setSelectedIndex(index);
		}

		@Override
		public void mousePressed(MouseEvent evt)
		{
			JList list = (JList) evt.getSource();

			if (evt.isPopupTrigger())
				openPopupMenu(list, evt.getX(), evt.getY(), list.locationToIndex(evt.getPoint()));
		}

		@Override
		public void mouseReleased(MouseEvent evt)
		{
			JList list = (JList) evt.getSource();

			if (evt.isPopupTrigger())
				openPopupMenu(list, evt.getX(), evt.getY(), list.locationToIndex(evt.getPoint()));
		}
	}
	
	//--------------------------------------------------------------------------	
	
	private class DeleteFileMenuItem extends JMenuItem
	{		
		public DeleteFileMenuItem(final File file)
		{
			setAction(new AbstractAction("Delete")
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					file.delete();
					fileListModel.refresh();					
				}	
			});
		}				
	}
	
	//--------------------------------------------------------------------------
	
	private class OpenFileMenuItem extends JMenuItem
	{		
		public OpenFileMenuItem(final File file)
		{
			setAction(new AbstractAction("Open")
			{								
				@Override
				public void actionPerformed(ActionEvent e)
				{
					documentManager.openDocument(file);					
				}	
			});
			
			setFont(getFont().deriveFont(Font.BOLD));
		}				
	}
	
	//--------------------------------------------------------------------------
	
	private class CopyToClipBoardMenuItem extends JMenuItem
	{
		public CopyToClipBoardMenuItem(String title, final String textToCopy)
		{
			setAction(new AbstractAction(title)					
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					Toolkit.getDefaultToolkit ().getSystemClipboard().setContents(new StringSelection (textToCopy), null);
				}
			});
		}	
	}
	
	//--------------------------------------------------------------------------
	
	private ZoneClassList zoneClassList = new ZoneClassList();
	
	// Main areas
	private JSplitPane fileSplitPaneLeft = new JSplitPane();
	private JSplitPane objectsSplitPaneLeft = new JSplitPane();
	private JSplitPane splitPaneMain = new JSplitPane();
	private JSplitPane splitPaneRight = new JSplitPane();
	private JSplitPane splitPaneFarRight = new JSplitPane();
	private JPanel bottomPanel = new JPanel();
	private JMenuBar menuBar = new JMenuBar();
	
	// Main compomnents
	private JTree directoryTree = new JTree();
	private JScrollPane directoryTreePane = new JScrollPane();
	private JList<FileNameWrapper> fileList = new JList();
	private JScrollPane fileListScrollPane = new JScrollPane();	
	
	private JTree paletteTree = new JTree();
	private JScrollPane paletteScrollPane = new JScrollPane();
	private JPanel palettePanel = new JPanel();
	private JButton paletteSearchButton = new JButton();
	private JTextField paletteSearchTextField = new JTextField();
	
	private JTabbedPane mainTabPane = new JTabbedPane();
	private JLabel bottomInfoLabel = new JLabel();
	private JLabel memoryLabel = new JLabel();
	private PropertySheetPanel propertySheetPanel = new PropertySheetPanel(new ZonePropertyRendererFactory(), new ZonePropertyEditorFactory());	
	
	// Data models
	private FileListModel fileListModel = new FileListModel(new AssetFileNameFilter(), true);
	private DirectoryTreeModel directoryTreeModel = new DirectoryTreeModel(new DirectoryFileFilter(), EditorApplication.getAssetsLocation(), EditorApplication.getScriptsLocation());
	private ZonePaletteDataModel zonePaletteDataModel = new ZonePaletteDataModel(zoneClassList);
	private ScriptPaletteDataModel scriptPaletteDataModel = new ScriptPaletteDataModel(zoneClassList);
	
	private PaletteCellRenderer paletteCellRenderer = new PaletteCellRenderer();
	private PaletteTransferHandler paletteTransferHandler = new PaletteTransferHandler();
	
	//Popup menu
	private JPopupMenu popupMenu = new JPopupMenu();	
			
	// File menu
	private JMenu fileMenu = new JMenu();	
	private JMenu newGameMenu = new JMenu();		
	private JMenuItem newGameZoneMenuItem;	
	private JMenuItem newGameLibraryMenuItem;	
	private JMenu newEditorMenu = new JMenu();	
	private JMenuItem newScriptMenuItem;
	private JMenuItem openMenuItem;
	private JMenu recentMenu = new JMenu();
	private JMenuItem saveMenuItem;
	private JMenuItem saveAsMenuItem;
	private JMenuItem saveAllMenuItem;		
	private JMenuItem closeMenuItem;
	private JMenuItem closeAllMenuItem;
	private JMenuItem exitMenuItem;
	
	// Edit menu
	private JMenu editMenu = new JMenu();
	private JMenuItem undoMenuItem;
	private JMenuItem redoMenuItem;
	private JMenuItem cutMenuItem;
	private JMenuItem copyMenuItem;
	private JMenuItem pasteMenuItem;
	private JMenuItem selectAllMenuItem;
	private JMenuItem findMenuItem;
	private JMenuItem goToLineMenuItem;
	private JMenuItem refreshMenuItem;
	
	// Tools menu
	private JMenu toolsMenu = new JMenu();
	private JMenuItem textureAtlasBuilderMenuItem;
	private JMenuItem libraryValidationMenuItem;	
	private JPopupMenu.Separator packageScriptsMenuSeparator = new JPopupMenu.Separator();
	private JMenuItem packageScriptsMenuItem;	
	private JPopupMenu.Separator rebuildDataBaseMenuSeparator = new JPopupMenu.Separator();
	private JMenuItem rebuildDataBaseMenuItem;
	
	// View menu
	private JMenu viewMenu = new JMenu();
	private ButtonGroup viewButtonGroup = new ButtonGroup();
	private JRadioButtonMenuItem textViewMenuItem = new JRadioButtonMenuItem();
	private JRadioButtonMenuItem graphViewMenuItem = new JRadioButtonMenuItem();
	private JPopupMenu.Separator resetZoomViewMenuSeparator = new JPopupMenu.Separator();
	private JMenuItem resetZoomViewMenuItem;
	
	// Options menu
	private JMenu optionsMenu = new JMenu();
	private JCheckBoxMenuItem debugConsoleMenuItem = new JCheckBoxMenuItem();
	private JCheckBoxMenuItem largeUIMenuItem = new JCheckBoxMenuItem();
	private JPopupMenu.Separator textRenderingSeparator = new JPopupMenu.Separator();
	private JMenu textSizeMenu = new JMenu();
	private ButtonGroup textSizeButtonGroup = new ButtonGroup();
	private JRadioButtonMenuItem textSizeButton_8 = new JRadioButtonMenuItem();
	private JRadioButtonMenuItem textSizeButton_10 = new JRadioButtonMenuItem();
	private JRadioButtonMenuItem textSizeButton_12 = new JRadioButtonMenuItem();
	private JRadioButtonMenuItem textSizeButton_14 = new JRadioButtonMenuItem();
	private JRadioButtonMenuItem textSizeButton_16 = new JRadioButtonMenuItem();
	private JRadioButtonMenuItem textSizeButton_18 = new JRadioButtonMenuItem();
	private JRadioButtonMenuItem textSizeButton_20 = new JRadioButtonMenuItem();
	private JRadioButtonMenuItem textSizeButton_22 = new JRadioButtonMenuItem();
	private JRadioButtonMenuItem textSizeButton_24 = new JRadioButtonMenuItem();
	private JCheckBoxMenuItem textAntialiasingMenuItem = new JCheckBoxMenuItem();
	private JCheckBoxMenuItem fractionalFontMetricsMenuItem = new JCheckBoxMenuItem();
	
	// Help menu
	private JMenu helpMenu = new JMenu();
	private JMenuItem helpMenuItem;	
	private JMenuItem blogMenuItem;	
	private JMenuItem aboutMenuItem;	
	
	// Tool bar
	private JToolBar toolBar = new JToolBar();
	private JButton newGameToolBarButton;
	private JButton newScriptToolBarButton;
	private JButton openToolBarButton;
	private JButton saveToolBarButton;
	private JButton closeToolBarButton;	
	private JButton undoToolBarButton;
	private JButton redoToolBarButton;
	private JButton cutToolBarButton;
	private JButton copyToolBarButton;
	private JButton pasteToolBarButton;
	private JButton findToolBarButton;
	private JToolBar.Separator refreshSeparator = new JToolBar.Separator();
	private JButton refreshToolBarButton;	
	private JButton editToolBarButton;
	private JButton validateLibraryToolBarButton;
	private JButton packageScriptsToolBarButton;
	
	// Dialogs
	private AboutDialog aboutDialog;
	private GoToLineDialog goToLineDialog;
	private FindDialog findDialog;
	private HelpFrame helpFrame;			
	private TextureAtlasDialog textureAtlasDialog;
	
	// Managers
	private DocumentManager documentManager;
	private RecentManager recentManager;	
	
	// Timers
	private Timer memoryLabelTimer = new Timer();
	
	//--------------------------------------------------------------------------

	public EditorFrame(String projectName)
	{
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setTitle("Zone Editor - " + projectName);
		
		try
		{
			setIconImage(ImageIO.read(getClass().getResourceAsStream("/eu/cherrytree/zaria/editor/res/icons/zone32.png")));
		}
		catch(IOException ex)
		{
			DebugConsole.logger.log(Level.SEVERE, null, ex);
		}
				
		addWindowListener(new WindowAdapter() {

			@Override
			public void windowOpened(WindowEvent e)
			{
				super.windowOpened(e);
				
				Rectangle bounds = new Rectangle();

				bounds.x = Settings.getWindowPosX();
				bounds.y = Settings.getWindowPosY();
				bounds.width = Settings.getWindowWidth();
				bounds.height = Settings.getWindowHeight();
				
				boolean maximized = Settings.getWindowMaximized();
				
				if (maximized)
				{
					bounds.x = 0;
					bounds.y = 0;					
				}				
				
				setBounds(bounds);
				
				if (maximized)
					setExtendedState(getExtendedState() | JFrame.MAXIMIZED_BOTH);
			}			

			@Override
			public void windowClosing(WindowEvent e)
			{
				super.windowClosing(e);
				
				Rectangle bounds = getBounds();
				
				boolean maximized = (getExtendedState() & JFrame.MAXIMIZED_BOTH) != 0;
				
				if (!maximized)
				{
					Settings.setWindowPosX(bounds.x);
					Settings.setWindowPosY(bounds.y);
				}
				else
				{
					Settings.setWindowPosX(0);
					Settings.setWindowPosY(0);
				}
				
				Settings.setWindowWidth(bounds.width);
				Settings.setWindowHeight(bounds.height);
				Settings.setWindowMaximized(maximized);
				
				documentManager.closeAllDocuments();
				recentManager.save();

				TextEditorState.saveSettings();
			}
									
			@Override
			public void windowClosed(WindowEvent e)
			{								
				super.windowClosed(e);

				DataBase.close(true);
				
				System.exit(0);
			}	
		});
		
		initMainPanes();
		
		documentManager = new DocumentManager(mainTabPane, this, propertySheetPanel, popupMenu, zoneClassList);
		
		initFileView();
		initPalleteView();							
		initToolBar();

		bottomInfoLabel.setText("0 | 0");
		bottomInfoLabel.setVisible(false);
		
		GroupLayout bottomPanelLayout = new GroupLayout(bottomPanel);
		bottomPanel.setLayout(bottomPanelLayout);
		
		bottomPanelLayout.setHorizontalGroup(
				bottomPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(GroupLayout.Alignment.TRAILING, bottomPanelLayout.createSequentialGroup()
				.addGap(5)
				.addComponent(memoryLabel)
				.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
				.addComponent(bottomInfoLabel)				
				.addGap(5)
				.addContainerGap()));
		
		bottomPanelLayout.setVerticalGroup(
				bottomPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addComponent(bottomInfoLabel)
				.addComponent(memoryLabel));

	
		initFileMenu();
		initEditMenu();
		initToolsMenu();
		initViewMenu();
		initOptionsMenu();
		initHelpMenu();
		
		recentManager = new RecentManager(recentMenu, this, documentManager, 6);
				
		setJMenuBar(menuBar);

		GroupLayout layout = new GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		
		layout.setHorizontalGroup(
				layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addComponent(splitPaneMain)
				.addComponent(bottomPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));
		
		layout.setVerticalGroup(
				layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
				.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
				.addComponent(splitPaneMain, GroupLayout.DEFAULT_SIZE, 734, Short.MAX_VALUE)
				.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
				.addComponent(bottomPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)));
		
		aboutDialog = new AboutDialog(this);
		goToLineDialog = new GoToLineDialog(this, documentManager);
		findDialog = new FindDialog(this, documentManager);
		textureAtlasDialog = new TextureAtlasDialog(this, true);
		
		initEditorSettings();
		
		memoryLabelTimer.scheduleAtFixedRate(new UpdateMemoryLabel(), 1000, 1000);
				
		setEditMode(ZoneDocument.DocumentType.ZONE, ZoneDocument.EditType.TEXT_EDIT);

		KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new KeyEventDispatcher()
		{					
			@Override
			public boolean dispatchKeyEvent(KeyEvent e)
			{
				if (e.getID() == KeyEvent.KEY_PRESSED)
				{
					switch (e.getKeyCode())
					{
						case KeyEvent.VK_CONTROL:
							documentManager.setCtrlButton(true);
							break;
							
						case KeyEvent.VK_ALT:	
							documentManager.setAltButton(true);
							break;
							
						case KeyEvent.VK_SHIFT:
							documentManager.setShiftButton(true);
							break;
							
						default:
							break;
					}
				}
				else if (e.getID() == KeyEvent.KEY_RELEASED)
				{
					switch (e.getKeyCode())
					{
						case KeyEvent.VK_CONTROL:
							documentManager.setCtrlButton(false);
							break;
							
						case KeyEvent.VK_ALT:	
							documentManager.setAltButton(false);
							break;
							
						case KeyEvent.VK_SHIFT:
							documentManager.setShiftButton(false);
							break;
							
						default:
							break;
					}
						
				}
				return false;
			}
		});
		
		pack();
	}
	
	//--------------------------------------------------------------------------
	
	private void initMainPanes()
	{
		JPanel centerPanel = new JPanel();
		centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.PAGE_AXIS));
		centerPanel.add(toolBar);
		centerPanel.add(mainTabPane);
		
		propertySheetPanel.setDescriptionVisible(true);
		
		splitPaneFarRight.setOrientation(JSplitPane.VERTICAL_SPLIT);
		
		splitPaneRight.setResizeWeight(1.0);
		splitPaneRight.setLeftComponent(centerPanel);
		splitPaneRight.setRightComponent(palettePanel);
		
		splitPaneMain.setRightComponent(splitPaneRight);
		fileSplitPaneLeft.setOrientation(JSplitPane.VERTICAL_SPLIT);	
				
		fileSplitPaneLeft.setBottomComponent(fileListScrollPane);
		fileSplitPaneLeft.setTopComponent(directoryTreePane);
		splitPaneMain.setLeftComponent(fileSplitPaneLeft);
						
		((BasicSplitPaneUI)splitPaneMain.getUI()).getDivider().addMouseListener(new HideLeftPaneMouseAdapter());
		((BasicSplitPaneUI)splitPaneRight.getUI()).getDivider().addMouseListener(new HideRightPaneMouseAdapter());				
	}
	
	//--------------------------------------------------------------------------
	
	private void initFileView()
	{
		fileList.setModel(fileListModel);		
		fileList.setCellRenderer(new ZoneFileListCellRenderer(documentManager));		
		fileListScrollPane.setViewportView(fileList);		
					
		directoryTree.setModel(directoryTreeModel);
		directoryTreePane.setViewportView(directoryTree);	
		directoryTree.setRootVisible(false);
				
		fileList.addMouseListener(new FileListMouseAdapter());
		
		fileList.addKeyListener(new KeyAdapter() {

			@Override
			public void keyPressed(KeyEvent e)
			{
				if (e.getKeyCode() == KeyEvent.VK_ENTER && fileList.getSelectedIndex() >= 0)
				{
					FileNameWrapper filewrap = (FileNameWrapper) fileList.getSelectedValue();
					documentManager.openDocument(filewrap.getFile());
				}
			}						
		});
								
		directoryTree.addTreeSelectionListener(new FileViewController(fileListModel));
	}
	
	//--------------------------------------------------------------------------
	
	private void initPalleteView()
	{		
		paletteScrollPane.setViewportView(paletteTree);
				
		paletteSearchButton.setIcon(new ImageIcon(getClass().getResource("/eu/cherrytree/zaria/editor/res/icons/tools/search.png")));
		paletteSearchButton.setToolTipText("Filter");
		paletteSearchButton.setFocusable(false);
		paletteSearchButton.setHorizontalTextPosition(SwingConstants.CENTER);
		paletteSearchButton.setVerticalTextPosition(SwingConstants.BOTTOM);
		paletteSearchButton.addActionListener(this);		
		
		paletteSearchTextField.addActionListener(this);
		
        GroupLayout palette_layout = new GroupLayout(palettePanel);
        palettePanel.setLayout(palette_layout);
        palette_layout.setHorizontalGroup(
            palette_layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(palette_layout.createSequentialGroup()
                .addComponent(paletteSearchTextField, GroupLayout.DEFAULT_SIZE, 358, Short.MAX_VALUE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(paletteSearchButton))
            .addComponent(paletteScrollPane)
        );
        palette_layout.setVerticalGroup(
            palette_layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(palette_layout.createSequentialGroup()
                .addGroup(palette_layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                    .addComponent(paletteSearchTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(paletteSearchButton,32,32,32))
                .addComponent(paletteScrollPane))
        );
		
		
		paletteTree.setRootVisible(false);				
		paletteTree.setDragEnabled(true);
		paletteTree.setShowsRootHandles(false);
						
		ToolTipManager.sharedInstance().registerComponent(paletteTree);
	}
	
	//--------------------------------------------------------------------------
	
	private void initObjectDefinitionPalette()
	{
		if (paletteTree.getModel() != zonePaletteDataModel)
		{
			paletteTree.setModel(zonePaletteDataModel);				
			paletteTree.setCellRenderer(paletteCellRenderer);
			paletteTree.setTransferHandler(paletteTransferHandler);
		}
		
		if (documentManager.getCurrentDocument() != null && zonePaletteDataModel.getCurrentType() != documentManager.getCurrentDocument().getDocumentType())
			zonePaletteDataModel.populate(documentManager.getCurrentDocument().getDocumentType());
	}
		
	//--------------------------------------------------------------------------
	
	private void initScriptPalette()
	{
		if (paletteTree.getModel() != scriptPaletteDataModel)
		{
			paletteTree.setModel(scriptPaletteDataModel);	
			paletteTree.setCellRenderer(paletteCellRenderer);
			paletteTree.setTransferHandler(paletteTransferHandler);
		}
		
		scriptPaletteDataModel.populate();
	}
	
	//--------------------------------------------------------------------------
	
	private void initFileMenu()
	{	
		// Initialzing the menu elements.
		fileMenu.setText("File");
		fileMenu.setMnemonic('f');

		newGameMenu.setText("New Game File");
		newGameMenu.setIcon(new ImageIcon(getClass().getResource("/eu/cherrytree/zaria/editor/res/icons/small/document-new.png")));
		
		newGameZoneMenuItem = initMenuItem("New Zone", "document-new-zone.png", null, newGameMenu);
		newGameLibraryMenuItem = initMenuItem("New Zone Library", "document-new-library.png", null, newGameMenu);
		
		fileMenu.add(newGameMenu);
		
		newEditorMenu.setText("New Work File");
		newEditorMenu.setIcon(new ImageIcon(getClass().getResource("/eu/cherrytree/zaria/editor/res/icons/small/document-new.png")));
																		
		fileMenu.add(newEditorMenu);
		
		newScriptMenuItem = initMenuItem("New Zone Script", "document-new-script.png", null, fileMenu);
		
		fileMenu.add(new JPopupMenu.Separator());
		
		openMenuItem = initMenuItem("Open", "document-open.png", KeyStroke.getKeyStroke(KeyEvent.VK_O, getToolkit().getMenuShortcutKeyMask()), fileMenu);
				
		recentMenu.setText("Recent");
		fileMenu.add(recentMenu);
		fileMenu.add(new JPopupMenu.Separator());

		saveMenuItem = initMenuItem("Save", "document-save.png", KeyStroke.getKeyStroke(KeyEvent.VK_S, getToolkit().getMenuShortcutKeyMask()), fileMenu);
		saveAsMenuItem = initMenuItem("Save As", "document-save-as.png", null, fileMenu);
		saveAllMenuItem = initMenuItem("Save All", null, KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.SHIFT_MASK | getToolkit().getMenuShortcutKeyMask()), fileMenu);

		fileMenu.add(new JPopupMenu.Separator());

		closeMenuItem = initMenuItem("Close", "document-close.png", null, fileMenu);
		closeAllMenuItem = initMenuItem("Close All", null, null, fileMenu);

		fileMenu.add(new JPopupMenu.Separator());

		exitMenuItem = initMenuItem("Exit", "exit.png", null, fileMenu);

		menuBar.add(fileMenu);
	}
	
	//--------------------------------------------------------------------------
	
	private void initEditMenu()
	{
		// Initialzing the menu elements.
		editMenu.setText("Edit");
		editMenu.setMnemonic('e');
		
		undoMenuItem = initMenuItem("Undo", "edit-undo.png", KeyStroke.getKeyStroke(KeyEvent.VK_Z, getToolkit().getMenuShortcutKeyMask()), editMenu);
		redoMenuItem = initMenuItem("Redo", "edit-redo.png", KeyStroke.getKeyStroke(KeyEvent.VK_Y, getToolkit().getMenuShortcutKeyMask()), editMenu);

		editMenu.add(new JPopupMenu.Separator());

		cutMenuItem = initMenuItem("Cut", "edit-cut.png", KeyStroke.getKeyStroke(KeyEvent.VK_X, getToolkit().getMenuShortcutKeyMask()), editMenu);
		copyMenuItem = initMenuItem("Copy", "edit-copy.png", KeyStroke.getKeyStroke(KeyEvent.VK_C, getToolkit().getMenuShortcutKeyMask()), editMenu);
		pasteMenuItem = initMenuItem("Paste", "edit-paste.png", KeyStroke.getKeyStroke(KeyEvent.VK_V, getToolkit().getMenuShortcutKeyMask()), editMenu);

		editMenu.add(new JPopupMenu.Separator());

		selectAllMenuItem = initMenuItem("Select All", "edit-select-all.png", KeyStroke.getKeyStroke(KeyEvent.VK_A, getToolkit().getMenuShortcutKeyMask()), editMenu);
		findMenuItem = initMenuItem("Find / Replace", "edit-find-replace.png", KeyStroke.getKeyStroke(KeyEvent.VK_F, getToolkit().getMenuShortcutKeyMask()), editMenu);
		goToLineMenuItem = initMenuItem("Go to Line", "go-to.png", KeyStroke.getKeyStroke(KeyEvent.VK_G, getToolkit().getMenuShortcutKeyMask()), editMenu);

		editMenu.add(new JPopupMenu.Separator());

		refreshMenuItem = initMenuItem("Refresh Zone Data", "view-refresh.png", null, editMenu);

		menuBar.add(editMenu);
	}
	
	//--------------------------------------------------------------------------
	
	private void initToolsMenu()
	{
		toolsMenu = new JMenu();
		toolsMenu.setText("Tools");
		toolsMenu.setMnemonic('t');
		
		libraryValidationMenuItem = initMenuItem("Validate library", "validate.png", null, toolsMenu);
		textureAtlasBuilderMenuItem = initMenuItem("Texture Atlas Builder", null, null, toolsMenu);
		
		toolsMenu.add(packageScriptsMenuSeparator);
		packageScriptsMenuItem = initMenuItem("Package scripts", "application-x-jar.png", null, toolsMenu);
		
		toolsMenu.add(rebuildDataBaseMenuSeparator);
		rebuildDataBaseMenuItem = initMenuItem("Rebuild database", null, null, toolsMenu);
		
		menuBar.add(toolsMenu);
	}
		
	//--------------------------------------------------------------------------
	
	private void initViewMenu()
	{
		// Initialzing the menu elements.
		viewMenu.setText("View");
		viewMenu.setMnemonic('v');
		
		textViewMenuItem.setText("Text");
		textViewMenuItem.setSelected(true);
		textViewMenuItem.addActionListener(this);
		textViewMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, getToolkit().getMenuShortcutKeyMask()));
		viewButtonGroup.add(textViewMenuItem);
		viewMenu.add(textViewMenuItem);
		
		graphViewMenuItem.setText("Graph");
		graphViewMenuItem.setSelected(false);		
		graphViewMenuItem.addActionListener(this);
		graphViewMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_2, getToolkit().getMenuShortcutKeyMask()));
		viewButtonGroup.add(graphViewMenuItem);
		viewMenu.add(graphViewMenuItem);
				
		viewMenu.add(resetZoomViewMenuSeparator);
		resetZoomViewMenuItem = initMenuItem("Reset zoom", null, null, viewMenu);
		
		menuBar.add(viewMenu);
		
		textViewMenuItem.setEnabled(false);
		graphViewMenuItem.setEnabled(false);
	}
	
	//--------------------------------------------------------------------------
	
	private void initOptionsMenu()
	{
		optionsMenu.setText("Options");
		optionsMenu.setMnemonic('o');
		
		debugConsoleMenuItem.setSelected(false);
        debugConsoleMenuItem.setText("Show Console");
		debugConsoleMenuItem.addActionListener(this);
        optionsMenu.add(debugConsoleMenuItem);
	
		largeUIMenuItem.setText("Large UI");
		largeUIMenuItem.setSelected(Settings.getLargeUI());
		largeUIMenuItem.addActionListener(this);
        optionsMenu.add(largeUIMenuItem);
				
		textSizeMenu.setText("Editor Text Size");
		
		textSizeButton_8.setText("8");
		textSizeButton_8.addActionListener(this);		
		textSizeButtonGroup.add(textSizeButton_8);
		textSizeMenu.add(textSizeButton_8);
		
		textSizeButton_10.setText("10");
		textSizeButton_10.addActionListener(this);		
		textSizeButtonGroup.add(textSizeButton_10);
		textSizeMenu.add(textSizeButton_10);
		
		textSizeButton_12.setText("12");
		textSizeButton_12.addActionListener(this);
		textSizeButtonGroup.add(textSizeButton_12);
		textSizeMenu.add(textSizeButton_12);
		
		textSizeButton_14.setText("14");
		textSizeButton_14.addActionListener(this);		
		textSizeButtonGroup.add(textSizeButton_14);
		textSizeMenu.add(textSizeButton_14);
		
		textSizeButton_16.setText("16");
		textSizeButton_16.addActionListener(this);		
		textSizeButtonGroup.add(textSizeButton_16);
		textSizeMenu.add(textSizeButton_16);
		
		textSizeButton_18.setText("18");
		textSizeButton_18.addActionListener(this);		
		textSizeButtonGroup.add(textSizeButton_18);
		textSizeMenu.add(textSizeButton_18);
		
		textSizeButton_20.setText("20");
		textSizeButton_20.addActionListener(this);		
		textSizeButtonGroup.add(textSizeButton_20);
		textSizeMenu.add(textSizeButton_20);
		
		textSizeButton_22.setText("22");
		textSizeButton_22.addActionListener(this);		
		textSizeButtonGroup.add(textSizeButton_22);
		textSizeMenu.add(textSizeButton_22);
	
		textSizeButton_24.setText("24");
		textSizeButton_24.addActionListener(this);		
		textSizeButtonGroup.add(textSizeButton_24);
		textSizeMenu.add(textSizeButton_24);
		
		optionsMenu.add(textSizeMenu);
		optionsMenu.add(textRenderingSeparator);
		
		textAntialiasingMenuItem.setText("Text Antialiasing");		
		textAntialiasingMenuItem.addActionListener(this);
		optionsMenu.add(textAntialiasingMenuItem);
		
		fractionalFontMetricsMenuItem.setText("Fractional Font Metrics");		
		fractionalFontMetricsMenuItem.addActionListener(this);
		optionsMenu.add(fractionalFontMetricsMenuItem);
		
		menuBar.add(optionsMenu);
	}
			
	//--------------------------------------------------------------------------
	
	private void initHelpMenu()
	{
		// Initialzing the menu elements.
		helpMenu.setText("Help");
		helpMenu.setMnemonic('h');

		helpMenuItem = initMenuItem("Help", "help.png", KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0), helpMenu);	
		blogMenuItem = initMenuItem("Dev Blog", null, null, helpMenu);	
				
		helpMenu.add(new JPopupMenu.Separator());
		
		aboutMenuItem = initMenuItem("About", null, null, helpMenu);

		menuBar.add(helpMenu);
	}
	
	//--------------------------------------------------------------------------
	
	private JMenuItem initMenuItem(String text, String icon, KeyStroke keyStroke, JMenu menu)
	{
		JMenuItem item = new JMenuItem();
		
		if (keyStroke != null)
			item.setAccelerator(keyStroke);
		
		if (icon != null)
			item.setIcon(new ImageIcon(getClass().getResource("/eu/cherrytree/zaria/editor/res/icons/small/" + icon)));
		
		item.setText(text);
		item.addActionListener(this);
		
		menu.add(item);
		
		return item;
	}

	//--------------------------------------------------------------------------
	
	private void initToolBar()
	{
		toolBar.setFloatable(false);
		
		toolBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));  
		toolBar.setMinimumSize(new Dimension(10, 32)); 
		
		toolBar.setRollover(true);

		newGameToolBarButton = initToolBarButton("document-new.png", "New Game Zone File");
		newScriptToolBarButton = initToolBarButton("document-new-script.png", "New Zone Script");
		openToolBarButton = initToolBarButton("document-open.png", "Open File");
		saveToolBarButton = initToolBarButton("document-save.png", "Save File");
		closeToolBarButton = initToolBarButton("document-close.png", "Close Current");
		toolBar.add(new JToolBar.Separator());
		
		undoToolBarButton = initToolBarButton("edit-undo.png", "Undo");
		redoToolBarButton = initToolBarButton("edit-redo.png", "Redo");
		toolBar.add(new JToolBar.Separator());
		
		cutToolBarButton = initToolBarButton("edit-cut.png", "Cut");
		copyToolBarButton = initToolBarButton("edit-copy.png", "Copy");
		pasteToolBarButton = initToolBarButton("edit-paste.png", "Paste");
		findToolBarButton = initToolBarButton("edit-find.png", "Find / Replace");
		
		toolBar.add(refreshSeparator);
		refreshToolBarButton = initToolBarButton("view-refresh.png", "Refresh Zone Data");

		editToolBarButton = initToolBarButton("edit.png", "Edit Object");	
		editToolBarButton.setVisible(false);
		
		validateLibraryToolBarButton = initToolBarButton("validate.png", "Validate library");
		packageScriptsToolBarButton = initToolBarButton("application-x-jar.png", "Package scripts");
	}
	
	//--------------------------------------------------------------------------
	
	private JButton initToolBarButton(String icon, String text)
	{
		JButton button = new JButton();
		button.setIcon(new ImageIcon(getClass().getResource("/eu/cherrytree/zaria/editor/res/icons/big/" + icon)));
		button.setToolTipText(text);
		button.setFocusable(false);
		button.setHorizontalTextPosition(SwingConstants.CENTER);
		button.setVerticalTextPosition(SwingConstants.BOTTOM);
		button.addActionListener(this);
		
		toolBar.add(button);
		
		return button;
	}
	
	//--------------------------------------------------------------------------
	
	private void initEditorSettings()
	{
		TextEditorState.loadSettings();
		
		textSizeButton_8.setSelected(Settings.getEditorFontSize() == 8);
		textSizeButton_10.setSelected(Settings.getEditorFontSize() == 10);
		textSizeButton_12.setSelected(Settings.getEditorFontSize() == 12);
		textSizeButton_14.setSelected(Settings.getEditorFontSize() == 14);
		textSizeButton_16.setSelected(Settings.getEditorFontSize() == 16);
		textSizeButton_18.setSelected(Settings.getEditorFontSize() == 18);
		textSizeButton_20.setSelected(Settings.getEditorFontSize() == 20);
		textSizeButton_22.setSelected(Settings.getEditorFontSize() == 22);
		textSizeButton_24.setSelected(Settings.getEditorFontSize() == 24);
		
		fractionalFontMetricsMenuItem.setSelected(TextEditorState.isFractionalFontMetricsEnabled());
		textAntialiasingMenuItem.setSelected(TextEditorState.isAntialiasingEnabled());		
	}

	//--------------------------------------------------------------------------

	@Override
	public void actionPerformed(ActionEvent event)
	{				
		if (event.getSource() == newGameZoneMenuItem || event.getSource() == newGameToolBarButton)
		{
			documentManager.newDocument(ZoneDocument.DocumentType.ZONE);
		}
		else if (event.getSource() == newGameLibraryMenuItem)
		{
			documentManager.newDocument(ZoneDocument.DocumentType.ZONE_LIBRARY);
		}
		else if (event.getSource() == newScriptMenuItem || event.getSource() == newScriptToolBarButton)
		{
			documentManager.newDocument(ZoneDocument.DocumentType.ZONE_SCRIPT);
		}
		else if (event.getSource() == openMenuItem || event.getSource() == openToolBarButton)
		{						
			documentManager.openDocument();
		}
		else if (event.getSource() == saveMenuItem || event.getSource() == saveToolBarButton)
		{
			documentManager.saveDocument();
		}
		else if (event.getSource() == saveAsMenuItem)
		{
			documentManager.saveDocumentAs();
		}
		else if (event.getSource() == saveAllMenuItem)
		{
			documentManager.saveAllDocuments();
		}
		else if (event.getSource() == closeMenuItem || event.getSource() == closeToolBarButton)
		{
			documentManager.closeDocument();
		}
		else if (event.getSource() == closeAllMenuItem)
		{
			documentManager.closeAllDocuments();
		}
		else if (event.getSource() == exitMenuItem)
		{			
			dispose();
		}
		else if (event.getSource() == undoMenuItem || event.getSource() == undoToolBarButton)
		{
			documentManager.undo();
		}
		else if (event.getSource() == redoMenuItem || event.getSource() == redoToolBarButton)
		{
			documentManager.redo();
		}
		else if (event.getSource() == cutMenuItem || event.getSource() == cutToolBarButton)
		{
			documentManager.cut();
		}
		else if (event.getSource() == copyMenuItem || event.getSource() == copyToolBarButton)
		{
			documentManager.copy();
		}
		else if (event.getSource() == pasteMenuItem || event.getSource() == pasteToolBarButton)
		{
			documentManager.paste();
		}
		else if (event.getSource() == selectAllMenuItem)
		{
			documentManager.selectAll();
		}
		else if (event.getSource() == findMenuItem || event.getSource() == findToolBarButton)
		{
			Rectangle framebounds = getBounds();
			Rectangle dialogbounds = findDialog.getBounds();
			
			Rectangle newbounds = new Rectangle(framebounds.x + framebounds.width - dialogbounds.width - Math.min(100, framebounds.width/3), 
												framebounds.y + Math.min(100, framebounds.height/3), 
												dialogbounds.width, dialogbounds.height);
			
			findDialog.setBounds(newbounds);
			findDialog.setSearched(documentManager.getCurrentDocument().getSelected());
			findDialog.setVisible(true);						
		}
		else if (event.getSource() == textAntialiasingMenuItem)
		{
			TextEditorState.setAntialiasing(textAntialiasingMenuItem.isSelected());
			documentManager.updateSettings();
		}
		else if (event.getSource() == fractionalFontMetricsMenuItem)
		{
			TextEditorState.setFractionalFontMetrics(fractionalFontMetricsMenuItem.isSelected());
			documentManager.updateSettings();
		}		
		else if (event.getSource() == goToLineMenuItem)
		{
			Rectangle framebounds = getBounds();
			Rectangle dialogbounds = goToLineDialog.getBounds();
			
			Rectangle newbounds = new Rectangle(framebounds.x + Math.min(100, framebounds.width/3), 
												framebounds.y + Math.min(100, framebounds.height/3), 
												dialogbounds.width, dialogbounds.height);
			
			goToLineDialog.setBounds(newbounds);
			
			goToLineDialog.setVisible(true);
		}
		else if (event.getSource() == refreshMenuItem || event.getSource() == refreshToolBarButton)
		{
			try
			{
				Serializer.init();
				zoneClassList.load();
				
				if (documentManager.getCurrentDocument() != null)
					zonePaletteDataModel.populate(documentManager.getCurrentDocument().getDocumentType());
				
				//TODO Refresh currently loaded stuff.
			}
			catch(IOException | ClassNotFoundException ex)
			{
				JOptionPane.showMessageDialog(this, "Loading of class information failed: " + ex, "Refresh failed.", JOptionPane.ERROR_MESSAGE);
				DebugConsole.logger.log(Level.SEVERE, null, ex);
			}
		}
		else if (event.getSource() == aboutMenuItem)
		{
			Rectangle framebounds = getBounds();
			Rectangle dialogbounds = aboutDialog.getBounds();
			
			Rectangle newbounds = new Rectangle(framebounds.x + framebounds.width/2 - dialogbounds.width/2, 
												framebounds.y + framebounds.height/2 - dialogbounds.height/2, 
												dialogbounds.width, dialogbounds.height);
			
			aboutDialog.setBounds(newbounds);
			aboutDialog.setVisible(true);
		}
		else if (event.getSource() == helpMenuItem)
		{
			openHelpWindow();
		}
		else if (event.getSource() == blogMenuItem)
		{
			try
			{
				Desktop.getDesktop().browse(new URI(EditorApplication.getBlogUrl()));
			}
			catch (URISyntaxException | IOException ex)
			{
				DebugConsole.logger.log(Level.SEVERE, null, ex);
			}
		}
		else if (event.getSource() == debugConsoleMenuItem)
		{
			EditorApplication.getDebugConsole().setVisible(debugConsoleMenuItem.isSelected());
		}
		else if (event.getSource() == largeUIMenuItem)
		{
			Settings.setLargeUI(largeUIMenuItem.getState());
			EditorApplication.updateLookAndFeel();
			
			SwingUtilities.updateComponentTreeUI(this);						
			SwingUtilities.updateComponentTreeUI(EditorApplication.getDebugConsole());
			SwingUtilities.updateComponentTreeUI(aboutDialog);
			SwingUtilities.updateComponentTreeUI(goToLineDialog);
			SwingUtilities.updateComponentTreeUI(findDialog);
			
			if(helpFrame != null)
				SwingUtilities.updateComponentTreeUI(helpFrame);
			
			SwingUtilities.updateComponentTreeUI(textureAtlasDialog);
	
			if (EditorApplication.getDebugConsole().isVisible())
				EditorApplication.getDebugConsole().pack();
			
			if (aboutDialog.isVisible())
				aboutDialog.pack();
			
			if (goToLineDialog.isVisible())
				goToLineDialog.pack();
			
			if (findDialog.isVisible())
				findDialog.pack();
			
			if (helpFrame != null && helpFrame.isVisible())
				helpFrame.pack();
			
			if (textureAtlasDialog.isVisible())
				textureAtlasDialog.pack();
			
			pack();
		}
		else if (event.getSource() == textViewMenuItem)
		{
			switchEditType(ZoneDocument.EditType.TEXT_EDIT);
		}
		else if (event.getSource() == graphViewMenuItem)
		{
			switchEditType(ZoneDocument.EditType.GRAPH_EDIT);
		}	
		else if (event.getSource() == paletteSearchButton || event.getSource() == paletteSearchTextField)
		{
			zonePaletteDataModel.setFilter(paletteSearchTextField.getText());
		}
		else if (event.getSource() == libraryValidationMenuItem || event.getSource() == validateLibraryToolBarButton)
		{
			boolean validated_ok = true;
			ZariaObjectDefinitionLibrary library = null;
			
			try
			{
				library = ZoneDocument.validateLibrary(documentManager.getCurrentDocument().getText());
			}
			catch (ValidationException ex)
			{
				ExtendedInformationDialog.show(this, "Validation failed", DebugManager.getValidationExceptionString("", ex).trim());				
				validated_ok = false;
			}
			catch (IOException ex)
			{
				ExtendedInformationDialog.show(this, "Validation failed", DebugManager.getThrowableText("", ex).trim());	
				validated_ok = false;
			}			
						
			if (validated_ok)
			{
				assert library != null;
				
				JOptionPane.showMessageDialog(this, "Validation of library " + library.getID() + " completed successfully.", "Library validated", JOptionPane.INFORMATION_MESSAGE);
			}
		}
		else if (event.getSource() == textureAtlasBuilderMenuItem)
		{
			Rectangle framebounds = getBounds();
			Rectangle dialogbounds = textureAtlasDialog.getBounds();
			
			Rectangle newbounds = new Rectangle(framebounds.x + framebounds.width/2 - dialogbounds.width/2, 
												framebounds.y + framebounds.height/2 - dialogbounds.height/2, 
												dialogbounds.width, dialogbounds.height);
			
			textureAtlasDialog.setBounds(newbounds);
			textureAtlasDialog.setVisible(true);
		}
		else if (event.getSource() == packageScriptsMenuItem || event.getSource() == packageScriptsToolBarButton)
		{
			documentManager.saveDocument();
			
			boolean created_ok = false;
			
			try
			{
				JarCreator.createJar(EditorApplication.getScriptsLocation(), EditorApplication.getScriptJarLocation());
				created_ok = true;
			}
			catch(JarCreator.ScriptJarCreationException ex)
			{
				ExtendedInformationDialog.show(this, "Script packaging failed", ex.getMessage());
			}
			catch (IOException ex)
			{
				DebugConsole.logger.log(Level.SEVERE, null, ex);
			}
			
			if (created_ok)
				JOptionPane.showMessageDialog(this, "Scripts packaged successfully.", "Scripts jar file created", JOptionPane.INFORMATION_MESSAGE);
		}
		else if (event.getSource() == editToolBarButton)
		{
			documentManager.editSelected();
		}
		else if (event.getSource() == resetZoomViewMenuItem)
		{
			((GraphEditorState) documentManager.getCurrentDocument().getEditorState(ZoneDocument.EditType.GRAPH_EDIT)).resetZoom();
		}
		else if (event.getSource() == rebuildDataBaseMenuItem)
		{
			DataBase.rebuildDataBase();
		}
		else if (event.getSource() == textSizeButton_8)
		{
			Settings.setEditorFontSize(8);
			TextEditorState.setFontSize(8);
			documentManager.updateSettings();
		}
		else if (event.getSource() == textSizeButton_10)
		{
			Settings.setEditorFontSize(10);
			TextEditorState.setFontSize(10);
			documentManager.updateSettings();
		}
		else if (event.getSource() == textSizeButton_12)
		{
			Settings.setEditorFontSize(12);
			TextEditorState.setFontSize(12);
			documentManager.updateSettings();
		}
		else if (event.getSource() == textSizeButton_14)
		{
			Settings.setEditorFontSize(14);
			TextEditorState.setFontSize(14);
			documentManager.updateSettings();
		}
		else if (event.getSource() == textSizeButton_16)
		{
			Settings.setEditorFontSize(16);
			TextEditorState.setFontSize(16);
			documentManager.updateSettings();
		}
		else if (event.getSource() == textSizeButton_18)
		{
			Settings.setEditorFontSize(18);
			TextEditorState.setFontSize(18);
			documentManager.updateSettings();
		}
		else if (event.getSource() == textSizeButton_20)
		{
			Settings.setEditorFontSize(20);
			TextEditorState.setFontSize(20);
			documentManager.updateSettings();
		}	
		else if (event.getSource() == textSizeButton_22)
		{
			Settings.setEditorFontSize(22);
			TextEditorState.setFontSize(22);
			documentManager.updateSettings();
		}
		else if (event.getSource() == textSizeButton_24)
		{
			Settings.setEditorFontSize(24);
			TextEditorState.setFontSize(24);
			documentManager.updateSettings();
		}
	}
	
	//--------------------------------------------------------------------------

	@Override
	public void onConsoleShown()
	{
		debugConsoleMenuItem.setSelected(true);
	}

	//--------------------------------------------------------------------------	

	@Override
	public void onConsoleHidden()
	{
		debugConsoleMenuItem.setSelected(false);
	}
	
	//--------------------------------------------------------------------------

	@Override
	public void onCursorPostion(int row, int column)
	{
		bottomInfoLabel.setText(row + " | " + column);
	}

	//--------------------------------------------------------------------------

	@Override
	public void onDocumentOpened(ZoneDocument document)
	{
		fileList.repaint();
		
		if (document.getPath() != null)
			recentManager.addDocument(document);
		
		propertySheetPanel.setProperties(new Property[0]);
		
		setEditMode(document.getDocumentType(), document.getEditType());
	}

	//--------------------------------------------------------------------------
	
	@Override
	public void onDocumentSavedAs(ZoneDocument document, String oldPath)
	{
		fileList.repaint();
		recentManager.documentUpdated(document, oldPath);
		fileListModel.refresh();
	}
	
	//--------------------------------------------------------------------------

	@Override
	public void onDocumentClosed(ZoneDocument document)
	{
		fileList.repaint();
	}
	
	//--------------------------------------------------------------------------
	
	public void switchEditType(ZoneDocument.EditType editType)
	{
		setEditMode(documentManager.getCurrentDocument() != null ? documentManager.getCurrentDocument().getDocumentType() : ZoneDocument.DocumentType.ZONE, editType);
		documentManager.setEditType(editType);
	}
	
	//--------------------------------------------------------------------------
	
	private void setEditMode(ZoneDocument.DocumentType documentType, ZoneDocument.EditType editType)
	{
		switch(documentType)
		{
			case ZONE:
							
				textViewMenuItem.setEnabled(true);
				graphViewMenuItem.setEnabled(true);
					
				libraryValidationMenuItem.setVisible(false);
				validateLibraryToolBarButton.setVisible(false);
				
				packageScriptsMenuSeparator.setVisible(false);
				packageScriptsMenuItem.setVisible(false);
				packageScriptsToolBarButton.setVisible(false);

				break;
				
			case ZONE_LIBRARY:

				textViewMenuItem.setEnabled(true);
				graphViewMenuItem.setEnabled(false);
	
				libraryValidationMenuItem.setVisible(true);
				validateLibraryToolBarButton.setVisible(true);
				
				packageScriptsMenuSeparator.setVisible(false);
				packageScriptsMenuItem.setVisible(false);
				packageScriptsToolBarButton.setVisible(false);
				
				break;

			case ZONE_SCRIPT:
				
				textViewMenuItem.setEnabled(true);
				graphViewMenuItem.setEnabled(false);
					
				libraryValidationMenuItem.setVisible(false);
				validateLibraryToolBarButton.setVisible(false);
				
				packageScriptsMenuSeparator.setVisible(true);
				packageScriptsMenuItem.setVisible(true);
				packageScriptsToolBarButton.setVisible(true);
				
				break;
		}
		
		switch(editType)
		{
			case TEXT_EDIT:
				
				goToLineMenuItem.setVisible(true);
				findMenuItem.setVisible(true);
				findToolBarButton.setVisible(true);
				refreshSeparator.setVisible(true);
				refreshToolBarButton.setVisible(true);
				bottomInfoLabel.setVisible(true);
				
				textViewMenuItem.setSelected(true);
				graphViewMenuItem.setSelected(false);
				
				textAntialiasingMenuItem.setVisible(true);
				fractionalFontMetricsMenuItem.setVisible(true);
				textRenderingSeparator.setVisible(true);
				textSizeMenu.setVisible(true);

				resetZoomViewMenuSeparator.setVisible(false);
				resetZoomViewMenuItem.setVisible(false);
				
				fileList.setDragEnabled(true);
				
				if (documentType == ZoneDocument.DocumentType.ZONE_SCRIPT)
					fileList.setTransferHandler(new ScriptImportTransferHandler());
				else
					fileList.setTransferHandler(new FileTransferHandler(documentType.getLocationType() == ZoneDocument.DocumentLocationType.ASSETS ? EditorApplication.getAssetsLocation() : ""));
				
				break;
				
			case GRAPH_EDIT:
				
				goToLineMenuItem.setVisible(false);
				findMenuItem.setVisible(false);
				findToolBarButton.setVisible(false);
				refreshSeparator.setVisible(true);
				refreshToolBarButton.setVisible(true);
				bottomInfoLabel.setVisible(false);
				
				textViewMenuItem.setSelected(false);
				graphViewMenuItem.setSelected(true);
				
				textAntialiasingMenuItem.setVisible(false);
				fractionalFontMetricsMenuItem.setVisible(false);
				textRenderingSeparator.setVisible(false);
				textSizeMenu.setVisible(false);
		
				resetZoomViewMenuSeparator.setVisible(true);
				resetZoomViewMenuItem.setVisible(true);
				
				fileList.setDragEnabled(false);
				fileList.setTransferHandler(null);
												
				break;
		}		
		
		editToolBarButton.setVisible(false);

		propertySheetPanel.setProperties(new Property[0]);
						
		if (documentType == ZoneDocument.DocumentType.ZONE_LIBRARY)
			hideRightPane();		
		else if (splitPaneRight.getRightComponent() != null)
			showRightPane(documentType, editType);
		
		if (splitPaneMain.getLeftComponent() != null)
			showLeftPane(documentType, editType);
		
		if (documentManager.getCurrentDocument() == null)
		{
			textViewMenuItem.setEnabled(false);
			graphViewMenuItem.setEnabled(false);
		}
	}
	
	//--------------------------------------------------------------------------
	
	private void showLeftPane(ZoneDocument.DocumentType documentType, ZoneDocument.EditType type)
	{
		int div_location = splitPaneMain.getDividerLocation();
		
		switch(type)
		{
			case TEXT_EDIT:				
				splitPaneMain.setLeftComponent(fileSplitPaneLeft);
				break;
				
			case GRAPH_EDIT:
				splitPaneMain.setLeftComponent(fileSplitPaneLeft);
				break;
		}
		
		splitPaneMain.setDividerLocation(div_location);
	}
	
	//--------------------------------------------------------------------------
	
	private void hideLeftPane()
	{
		splitPaneMain.setLeftComponent(null);
	}
	
	//--------------------------------------------------------------------------
	
	private void showRightPane(ZoneDocument.DocumentType documentType, ZoneDocument.EditType type)
	{
		int div_location = splitPaneRight.getDividerLocation();
		
		switch(type)
		{
			case TEXT_EDIT:
				
				switch (documentType)
				{
					case ZONE_SCRIPT:
						initScriptPalette();
						splitPaneRight.setRightComponent(palettePanel);
						break;
						
					case ZONE_LIBRARY:
						// Nothing to do here.
						break;
							
					default:
						initObjectDefinitionPalette();
						splitPaneRight.setRightComponent(palettePanel);
						break;
					
				}
				
				break;
				
			case GRAPH_EDIT:
				initObjectDefinitionPalette();
				splitPaneRight.setRightComponent(splitPaneFarRight);		
				splitPaneFarRight.setTopComponent(palettePanel);
				splitPaneFarRight.setBottomComponent(propertySheetPanel);
				break;
		}	
		
		splitPaneRight.setDividerLocation(div_location);
	}
	
	//--------------------------------------------------------------------------
	
	private void hideRightPane()
	{
		splitPaneRight.setRightComponent(null);
	}
	
	//--------------------------------------------------------------------------
	
	private void setLeftDivider()
	{
		splitPaneMain.setDividerLocation(200);
	}
	
	//--------------------------------------------------------------------------
	
	private void setRightDivider()
	{
		splitPaneRight.setDividerLocation(splitPaneRight.getWidth() - 200);
	}
	
	//--------------------------------------------------------------------------
	
	@Override
	public void onDocumentSwitched(ZoneDocument document)
	{
		setEditMode(document.getDocumentType(), document.getEditType());
		
		propertySheetPanel.setProperties(new Property[0]);
		
		findDialog.setVisible(false);
	}
	
	//--------------------------------------------------------------------------
	
	private void openHelpWindow()
	{
		if (helpFrame == null)
			helpFrame = new HelpFrame(zoneClassList);
		
		helpFrame.setVisible(true);
	}
	
	//--------------------------------------------------------------------------

	public synchronized void goToEditMode(final ZoneDocument.EditType type)
	{
		if (!SwingUtilities.isEventDispatchThread())
		{
			SwingUtilities.invokeLater(new Runnable()
			{
				@Override
				public void run()
				{
					switchEditType(type);
				}
			});
		}
		else
		{
			switchEditType(type);
		}				
	}
	
	//--------------------------------------------------------------------------

	public void setEditingObjectEnabled(boolean enabled)
	{
		if (enabled)
		{
			editToolBarButton.setVisible(true);
		}
		else 
		{
			editToolBarButton.setVisible(false);
		}
	}
	
	//--------------------------------------------------------------------------
}

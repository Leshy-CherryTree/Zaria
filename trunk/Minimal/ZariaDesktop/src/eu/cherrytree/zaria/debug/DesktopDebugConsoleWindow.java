/****************************************/
/* DebugConsole.java						*/
/* Created on: 13-Jun-2014				*/
/* Copyright Cherry Tree Studio 2014		*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.debug;

import eu.cherrytree.zaria.base.ApplicationInstance;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.prefs.Preferences;
import javax.imageio.ImageIO;
import javax.swing.DefaultComboBoxModel;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextPane;
import javax.swing.JToggleButton;
import javax.swing.LayoutStyle;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.filechooser.FileFilter;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public class DesktopDebugConsoleWindow extends JFrame implements ActionListener, MouseListener, ItemListener, DebugUI
{	
	//--------------------------------------------------------------------------
	
	private class ConsoleEntry
	{
		private DebugManager.TraceLevel level;
		private String text;

		public ConsoleEntry(DebugManager.TraceLevel level, String text)
		{
			this.level = level;
			this.text = text;
		}

		public String getText()
		{
			return text;
		}

		public DebugManager.TraceLevel getLevel()
		{
			return level;
		}				
	}
	
	private class LogHandler extends Handler
	{
		private class LogRunnable implements Runnable
		{
			private LogRecord logRecord;

			public LogRunnable(LogRecord logRecord)
			{
				this.logRecord = logRecord;
			}

			@Override
			public void run()
			{
				String str =	"[LOG " + logRecord.getLevel() + "]: " + 
				logRecord.getSourceClassName() + "." +
				logRecord.getSourceMethodName() + ": " + 
				logRecord.getMessage() + "\n";

				consoleWindow.addLog(str);

				Throwable throwable = logRecord.getThrown();

				if (throwable != null)
				{
					consoleWindow.addLog(DebugManager.getThrowableText("", throwable).trim() + "\n");
					JOptionPane.showMessageDialog(null, 
							throwable.getClass().getSimpleName() + " occured!\nMessage: " + throwable.getMessage() + "\nSee the debug console for more information.",
							"Error!",JOptionPane.ERROR_MESSAGE);
				}
			}		
		}

		private DesktopDebugConsoleWindow consoleWindow;

		public LogHandler(DesktopDebugConsoleWindow consoleWindow)
		{
			this.consoleWindow = consoleWindow;
		}

		@Override
		public void publish(LogRecord logRecord)
		{
			SwingUtilities.invokeLater(new LogRunnable(logRecord));	
		}

		@Override
		public void flush()
		{
			// Intentionally empty.
		}

		@Override
		public void close() throws SecurityException
		{
			// Intentionally empty.
		}
	}
	
	//--------------------------------------------------------------------------
	
	private JToggleButton activateButton = new JToggleButton();
	private JPanel buttonPanel = new JPanel();
	private JButton clearButton = new JButton();
	private JTextPane consoleTextPane = new JTextPane();
	private JPanel conolePanel = new JPanel();
	private JPanel loggerPanel = new JPanel();
	private JScrollPane consoleScrollPane = new JScrollPane();
	private JScrollPane loggerScrollPane = new JScrollPane();
	private JButton killButon = new JButton();
	private JTextPane loggerTextPane = new JTextPane();
	private JPanel memoryPanel = new DrawMemoryPanel();
	private JButton saveButon = new JButton();
	private JTabbedPane tabPane = new JTabbedPane();
	private JPopupMenu popupMenu = new JPopupMenu();
    private JMenuItem copyConsolePopupMenu = new JMenuItem();
	private JMenuItem copyLoggerPopupMenu = new JMenuItem();
	private JComboBox levelComboBox;
	
	private Style consoleInfoStyle;
	private Style consoleDetailsStyle;
	private Style consoleWarningStyle;
	private Style consoleErrorStyle;
	
	private Style loggerStyle;
	
	private Preferences preferences;
	
	private DebugManager.TraceLevel currentLevel = DebugManager.TraceLevel.INFO;
	private ArrayList<ConsoleEntry> entries = new ArrayList<>();
	
	private LogHandler handler = new LogHandler(this);

	//--------------------------------------------------------------------------
	
	public DesktopDebugConsoleWindow(String title)
	{						
		setTitle(title);
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		getRootPane().setWindowDecorationStyle(JRootPane.NONE);
		
		setMinimumSize(new Dimension(750, 500));

		try
		{
			Image im = ImageIO.read(getClass().getResource("/eu/cherrytree/zaria/debug/res/debugicon.png"));
			setIconImage(im);
		}
		catch (IOException ex)
		{
			ex.printStackTrace();
		}
		
		killButon.setText("Kill app");
		saveButon.setText("Save to File");
		clearButton.setText("Clear");
		activateButton.setText("Debug active");
		
		killButon.addActionListener(this);
		saveButon.addActionListener(this);
		clearButton.addActionListener(this);
		activateButton.addActionListener(this);
		
		copyConsolePopupMenu.setText("Copy");
        copyConsolePopupMenu.addActionListener(this);		
		
		copyLoggerPopupMenu.setText("Copy");
        copyLoggerPopupMenu.addActionListener(this);
		
		levelComboBox = new JComboBox();

        levelComboBox.setModel(new DefaultComboBoxModel(DebugManager.TraceLevel.values()));
		levelComboBox.setSelectedItem(DebugManager.TraceLevel.INFO);
		
		levelComboBox.addItemListener(this);

		GroupLayout memoryPanelLayout = new GroupLayout(memoryPanel);
		memoryPanel.setLayout(memoryPanelLayout);
		memoryPanelLayout.setHorizontalGroup(
				memoryPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGap(0, 247, Short.MAX_VALUE));
		memoryPanelLayout.setVerticalGroup(
				memoryPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGap(0, 31, Short.MAX_VALUE));
		
		GroupLayout buttonPanelLayout = new GroupLayout(buttonPanel);
		buttonPanel.setLayout(buttonPanelLayout);
		buttonPanelLayout.setHorizontalGroup(
				buttonPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(buttonPanelLayout.createSequentialGroup()
				.addContainerGap()
				.addComponent(activateButton)
				.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
				.addComponent(memoryPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
				.addComponent(levelComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
				.addComponent(clearButton)
				.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
				.addComponent(killButon)
				.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
				.addComponent(saveButon)
				.addContainerGap()));
		buttonPanelLayout.setVerticalGroup(
				buttonPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(buttonPanelLayout.createSequentialGroup()
				.addGroup(buttonPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(buttonPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
				.addComponent(clearButton)
				.addComponent(killButon)
				.addComponent(saveButon)
				.addComponent(levelComboBox))
				.addComponent(memoryPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				.addComponent(activateButton))
				.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));

		consoleScrollPane.setViewportView(consoleTextPane);

		GroupLayout jPanel2Layout = new GroupLayout(conolePanel);
		conolePanel.setLayout(jPanel2Layout);
		jPanel2Layout.setHorizontalGroup(
				jPanel2Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addComponent(consoleScrollPane, GroupLayout.DEFAULT_SIZE, 833, Short.MAX_VALUE));
		jPanel2Layout.setVerticalGroup(
				jPanel2Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addComponent(consoleScrollPane, GroupLayout.DEFAULT_SIZE, 599, Short.MAX_VALUE));

		tabPane.addTab("Debug Console", conolePanel);

		loggerScrollPane.setViewportView(loggerTextPane);

		GroupLayout jPanel3Layout = new GroupLayout(loggerPanel);
		loggerPanel.setLayout(jPanel3Layout);
		jPanel3Layout.setHorizontalGroup(
				jPanel3Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addComponent(loggerScrollPane, GroupLayout.DEFAULT_SIZE, 833, Short.MAX_VALUE));
		jPanel3Layout.setVerticalGroup(
				jPanel3Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addComponent(loggerScrollPane, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 599, Short.MAX_VALUE));

		tabPane.addTab("Logger", loggerPanel);

		GroupLayout layout = new GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(
				layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addComponent(buttonPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
				.addComponent(tabPane, GroupLayout.DEFAULT_SIZE, 853, Short.MAX_VALUE));
		layout.setVerticalGroup(
				layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
				.addComponent(tabPane)
				.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
				.addComponent(buttonPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)));

		consoleTextPane.setEditable(false);
		loggerTextPane.setEditable(false);
		
		consoleTextPane.addMouseListener(this);
		loggerTextPane.addMouseListener(this);
		
		
		consoleTextPane.setFont(new Font("Monospaced", Font.PLAIN, 12));
		
		
		consoleDetailsStyle = consoleTextPane.addStyle("Details Style", null);
		StyleConstants.setForeground(consoleDetailsStyle, Color.gray);
		
		consoleInfoStyle = consoleTextPane.addStyle("Info Style", null);
		StyleConstants.setForeground(consoleInfoStyle, Color.black);
						
		consoleWarningStyle = consoleTextPane.addStyle("Warning Style", null);
		StyleConstants.setForeground(consoleWarningStyle, Color.orange);
		
		consoleErrorStyle = consoleTextPane.addStyle("Error Style", null);
		StyleConstants.setForeground(consoleErrorStyle, Color.red);
		
		loggerStyle = loggerTextPane.addStyle("Logger Style", null);
		StyleConstants.setForeground(loggerStyle, Color.black);
		
		preferences = Preferences.userNodeForPackage(ApplicationInstance.class);				
	}
	
	//--------------------------------------------------------------------------
	
	@Override
	public void init()		
	{
		pack();
		
		Rectangle bounds = new Rectangle();

		bounds.x = preferences.getInt("debug console posx", 0);
		bounds.y = preferences.getInt("debug console posy", 0);
		bounds.width = preferences.getInt("debug console width", 100);
		bounds.height = preferences.getInt("debug console height", 200);
		
		currentLevel = DebugManager.TraceLevel.valueOf(preferences.get("trace level", DebugManager.TraceLevel.INFO.name()));
		
		setBounds(bounds);
		
		setVisible(true);				
	}
	
	//--------------------------------------------------------------------------
	
	@Override
	public void deinit()
	{
		Rectangle bounds = getBounds();

		preferences.putInt("debug console posx", bounds.x);
		preferences.putInt("debug console posy", bounds.y);
		preferences.putInt("debug console width", bounds.width);
		preferences.putInt("debug console height", bounds.height);
		preferences.put("trace level", getTraceLevel().name());
		
		setVisible(false);
		dispose();
	}
	
	//--------------------------------------------------------------------------
	
	public void setTraceLevel(DebugManager.TraceLevel level)
	{
		if(currentLevel != level)
		{
			currentLevel = level;
		
			try
			{
				consoleTextPane.setText("");

				Document document = consoleTextPane.getDocument();

				for(ConsoleEntry entry : entries)
				{
					if(currentLevel.ordinal() <= entry.getLevel().ordinal())
					{
						switch(entry.getLevel())
						{
							case DETAILS:
								document.insertString(document.getLength(), entry.getText(), consoleDetailsStyle);
								break;

							case INFO:
								document.insertString(document.getLength(), entry.getText(), consoleInfoStyle);
								break;

							case WARNING:
								document.insertString(document.getLength(), entry.getText(), consoleWarningStyle);
								break;

							case ERROR:
								document.insertString(document.getLength(), entry.getText(), consoleErrorStyle);
								break;
						}
					}
				}

				consoleTextPane.setCaretPosition(consoleTextPane.getText().length());
			}
			catch (BadLocationException ex)
			{
				ex.printStackTrace();
			}
		}
		
		if(levelComboBox.getSelectedIndex() != currentLevel.ordinal())
			levelComboBox.setSelectedIndex(currentLevel.ordinal());
	}
	
	//--------------------------------------------------------------------------

	public DebugManager.TraceLevel getTraceLevel()
	{
		return currentLevel;
	}
	
	//--------------------------------------------------------------------------
	
	@Override
	public synchronized void addText(String text, DebugManager.TraceLevel level)
	{
		try
		{
			Document document = consoleTextPane.getDocument();
						
			if(level.ordinal() >= currentLevel.ordinal())
			{
				switch(level)
				{
					case DETAILS:
						document.insertString(document.getLength(), text, consoleDetailsStyle);
						break;
						
					case INFO:
						document.insertString(document.getLength(), text, consoleInfoStyle);
						break;
					
					case WARNING:
						document.insertString(document.getLength(), text, consoleWarningStyle);
						break;

					case ERROR:
						document.insertString(document.getLength(), text, consoleErrorStyle);
						break;
				}
				
				consoleTextPane.setCaretPosition(consoleTextPane.getDocument().getLength());
			}
			
			entries.add(new ConsoleEntry(level, text));						
		}
		catch (BadLocationException ex)
		{
			ex.printStackTrace();
		}
	}
	
	//--------------------------------------------------------------------------
	
	void addLog(String text)
	{	
		try
		{
			Document document = loggerTextPane.getDocument();
			document.insertString(document.getLength(), text, loggerStyle);
		}
		catch (BadLocationException ex)
		{
			ex.printStackTrace();
		}
	}
	
	//--------------------------------------------------------------------------
	
	private String getEntryText(DebugManager.TraceLevel level)
	{
		StringBuilder builder = new StringBuilder();
		
		for(ConsoleEntry entry : entries)
		{
			if(level.ordinal() <= entry.getLevel().ordinal())
				builder.append(entry.getText());
		}
		
		return builder.toString();
	}
	
	//--------------------------------------------------------------------------

	@Override
	public void actionPerformed(ActionEvent evt)
	{
		if(evt.getSource() == killButon)
		{
			System.exit(-1);
		}
		else if(evt.getSource() == saveButon)
		{
			if(tabPane.getSelectedIndex() == 0)
				openSaveDialog(getEntryText(DebugManager.TraceLevel.INFO));
			else if(tabPane.getSelectedIndex() == 1)
				openSaveDialog(loggerTextPane.getText());
		}
		else if(evt.getSource() == clearButton)
		{
			if(tabPane.getSelectedIndex() == 0)
			{
				consoleTextPane.setText("");
				entries.clear();
			}
			else if(tabPane.getSelectedIndex() == 1)
				loggerTextPane.setText("");
		}
		else if(evt.getSource() == activateButton)
		{
			if(!activateButton.isSelected())
			{
				activateButton.setText("Debug Active");
				DebugManager.setTextOutPause(false);
			}
			else
			{
				activateButton.setText("Debug Inactive");
				DebugManager.setTextOutPause(true);
			}
		}
		else if(evt.getSource() == copyConsolePopupMenu)
		{
			Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(consoleTextPane.getSelectedText()), null);
		}
		else if(evt.getSource() == copyLoggerPopupMenu)
		{
			Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(loggerTextPane.getSelectedText()), null);
		}
	}
	
	//--------------------------------------------------------------------------
	
	private void openSaveDialog(String text)
	{
		JFileChooser filechooser = new JFileChooser();

        filechooser.setFileFilter(new FileFilter()
		{
			@Override
			public boolean accept(File f)
			{
				return  f.isDirectory() || f.getName().toLowerCase().endsWith(".txt");
			}

			@Override
			public String getDescription()
			{
				return "(.txt) UTF-8 text";
			}
		});

        if(filechooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION)
        {
			saveLog(filechooser.getSelectedFile().getAbsolutePath(), text);			            
        }
	}
	
	//--------------------------------------------------------------------------
	
	@Override
	public void saveLog(String path)
	{
		saveLog(path, consoleTextPane.getText());
	}
	
    //--------------------------------------------------------------------------
	
	private void saveLog(String path, String text)
	{
		try
		{
			if(!path.toLowerCase().endsWith(".txt"))
				path += ".txt";

			try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path),"UTF-8")))
			{
				writer.write(text);
			}

		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	//--------------------------------------------------------------------------
	
	@Override
	public void itemStateChanged(ItemEvent evt)
	{
		if(evt.getSource() == levelComboBox)
			setTraceLevel((DebugManager.TraceLevel) evt.getItem());
	}
	
	//--------------------------------------------------------------------------
	
	@Override
	public void mousePressed(MouseEvent evt)
	{
		if(evt.getSource() == consoleTextPane && evt.isPopupTrigger())
		{
			popupMenu.removeAll();
			popupMenu.add(copyConsolePopupMenu);
			popupMenu.show(consoleTextPane, evt.getX(), evt.getY());
		}
		else if(evt.getSource() == loggerTextPane && evt.isPopupTrigger())
		{
			popupMenu.removeAll();
			popupMenu.add(copyLoggerPopupMenu);
			popupMenu.show(loggerTextPane, evt.getX(), evt.getY());
		}
	}
	
	//--------------------------------------------------------------------------
	
	@Override
	public void showErrorDialog(String title, String message)
	{
		JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
	}
	
    //--------------------------------------------------------------------------
	
	@Override
	public void showWarningDialog(String title, String message)
	{
		JOptionPane.showMessageDialog(null, message, title, JOptionPane.WARNING_MESSAGE);
	}
	
	//--------------------------------------------------------------------------
	
	@Override
	public boolean showConfirmDialog(String title, String message)
	{
		int option = JOptionPane.showConfirmDialog(null, message, title, JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);		
		return option == JOptionPane.YES_OPTION;
	}
	
	//--------------------------------------------------------------------------
	
	@Override
	public Handler getLogHandler()
	{
		return handler;
	}
	
	//--------------------------------------------------------------------------

	@Override
	public void mouseClicked(MouseEvent evt)
	{
		// Intentionally empty.
	}
	
	//--------------------------------------------------------------------------
	
	@Override
	public void mouseReleased(MouseEvent evt)
	{
		// Intentionally empty.
	}
	
	//--------------------------------------------------------------------------

	@Override
	public void mouseEntered(MouseEvent evt)
	{
		// Intentionally empty.
	}
	
	//--------------------------------------------------------------------------

	@Override
	public void mouseExited(MouseEvent evt)
	{
		// Intentionally empty.
	}
	
	//--------------------------------------------------------------------------
}
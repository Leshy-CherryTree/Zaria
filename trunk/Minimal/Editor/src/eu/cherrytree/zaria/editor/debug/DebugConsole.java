/****************************************/
/* DebugConsole.java					*/
/* Created on: 04-May-2013				*/
/* Copyright Cherry Tree Studio 2013	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.editor.debug;

import eu.cherrytree.zaria.debug.DebugManager;
import eu.cherrytree.zaria.editor.EditorFrame;
import eu.cherrytree.zaria.editor.Settings;
import eu.cherrytree.zaria.editor.listeners.DebugConsoleParentListener;

import java.awt.Rectangle;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;

import javax.swing.GroupLayout;
import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public class DebugConsole extends JDialog
{
	//--------------------------------------------------------------------------
	
	private static final long serialVersionUID = 1L;
	
	//--------------------------------------------------------------------------
	
	public static final Logger logger = Logger.getLogger(DebugConsole.class.getCanonicalName());
	
	//--------------------------------------------------------------------------
	
	private DebugConsoleParentListener listener;
	private JTextArea consoleTextArea;
	private SimpleDateFormat dateFormat;

	//--------------------------------------------------------------------------

	public DebugConsole(EditorFrame parent)
	{
		super(parent, false);
				
		listener = parent;

		setTitle("Zone Editor - Debug Console");
		
        JScrollPane scrollPane = new javax.swing.JScrollPane();
        consoleTextArea = new javax.swing.JTextArea();

        setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		
		addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e)
			{
				super.windowClosing(e);
				listener.onConsoleHidden();
				
				//TODO This is not triggered when the application is closing.
				
				Rectangle bounds = getBounds();
				
				Settings.setConsolePosX(bounds.x);
				Settings.setConsolePosY(bounds.y);
				Settings.setConsoleWidth(bounds.width);
				Settings.setConsoleHeight(bounds.height);
			}

			@Override
			public void windowOpened(WindowEvent e)
			{
				super.windowOpened(e);
				listener.onConsoleShown();
				
				Rectangle bounds = new Rectangle();

				bounds.x = Settings.getConsolePosX();
				bounds.y = Settings.getConsolePosY();
				bounds.width = Settings.getConsoleWidth();
				bounds.height = Settings.getConsoleHeight();

				setBounds(bounds);
			}
		});

        consoleTextArea.setEditable(false);
        consoleTextArea.setTabSize(4);
        scrollPane.setViewportView(consoleTextArea);

        GroupLayout layout = new GroupLayout(getContentPane());
		
        getContentPane().setLayout(layout);
		
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 642, Short.MAX_VALUE)
        );
		
        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 511, Short.MAX_VALUE)
        );
		
		dateFormat = new SimpleDateFormat("HH:mm:ss");

        pack();
		
		logger.addHandler(new DebugLogHandler(this));
    }   
	
	//--------------------------------------------------------------------------
	
	public synchronized void addLine(String text)
	{				
		consoleTextArea.append("[" + dateFormat.format(new Date()) + "]: " + text + "\n");
	}
	
	//--------------------------------------------------------------------------		
	
	public synchronized void addThrown(Throwable throwable)
	{
		String throwntext = "\n" + throwable.getClass().getName() + " thrown at " + dateFormat.format(new Date()) + "\n\n";

		throwntext += DebugManager.getThrowableText("", throwable).trim() + "\n";	
		
		consoleTextArea.append(throwntext);
	}
	
	//--------------------------------------------------------------------------
}

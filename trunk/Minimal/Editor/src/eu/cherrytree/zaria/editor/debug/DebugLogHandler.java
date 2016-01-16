/****************************************/
/* DebugLogHandler.java					*/
/* Created on: 15-Oct-2013				*/
/* Copyright Cherry Tree Studio 2013	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.editor.debug;

import java.util.logging.Handler;
import java.util.logging.LogRecord;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public class DebugLogHandler extends Handler
{
	//--------------------------------------------------------------------------
	
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
			logRecord.getMessage();

			console.addLine(str);

			Throwable throwable = logRecord.getThrown();
			
			if (throwable != null)
			{
				console.addThrown(throwable);
				JOptionPane.showMessageDialog(null, 
						throwable.getClass().getSimpleName() + " occured!\nMessage: " + throwable.getMessage() + "\nSee the debug console for more information.",
						"Error!",JOptionPane.ERROR_MESSAGE);
			}
		}		
	}
	
	//--------------------------------------------------------------------------
	
	private DebugConsole console;
	
	//--------------------------------------------------------------------------
	
	public DebugLogHandler(DebugConsole console)
	{
		this.console = console;
	}
	
	//--------------------------------------------------------------------------
	
	@Override
	public void publish(LogRecord logRecord)
	{
		SwingUtilities.invokeLater(new LogRunnable(logRecord));	
	}
	
	//--------------------------------------------------------------------------

	@Override
	public void flush()
	{
		// Intentionally empty.
	}
	
	//--------------------------------------------------------------------------

	@Override
	public void close() throws SecurityException
	{
		// Intentionally empty.
	}
	
	//--------------------------------------------------------------------------
}

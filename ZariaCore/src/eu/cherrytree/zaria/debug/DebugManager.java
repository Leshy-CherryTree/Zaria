/****************************************/
/* DebugManager.java                    */
/* Created on: Jan 26, 2013             */
/* Copyright Cherry Tree Studio 2013	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.debug;

import eu.cherrytree.zaria.base.ApplicationInstance;
import eu.cherrytree.zaria.base.ApplicationRuntimeError;
import eu.cherrytree.zaria.base.SystemProperties;
import eu.cherrytree.zaria.base.ZariaApplication;
import eu.cherrytree.zaria.serialization.DefinitionValidation;
import eu.cherrytree.zaria.serialization.ValidationException;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Date;
import java.util.prefs.Preferences;
import java.text.SimpleDateFormat;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public class DebugManager extends SecurityManager
{
    //--------------------------------------------------------------------------
		
	public enum TraceLevel
	{
		DETAILS,
		INFO,
		WARNING,
		ERROR;
	}
	
	//--------------------------------------------------------------------------
	
	private static final boolean active = Boolean.getBoolean("debug");

    private static DebugManager instance;

    private static final Logger logger = Logger.getLogger(DebugManager.class.getName());

    //--------------------------------------------------------------------------

    private boolean textPaused = false;

    private DebugUI debugUI;

    private SimpleDateFormat dateFormat;
			
	// TODO Change all complex string operations to use the StringBuilder.

    //--------------------------------------------------------------------------

    public static synchronized void init(DebugUI debugUI)
    {
		assert active;

        if (instance == null)
        {
			Logger rootLogger = Logger.getLogger(""); 
									
            instance = new DebugManager();

            instance.debugUI = debugUI;	
			
            instance.dateFormat = new SimpleDateFormat ("HH:mm:ss");            
			
			instance.debugUI.init();       
			
			rootLogger.setLevel(Level.INFO); 
			rootLogger.addHandler(instance.debugUI.getLogHandler());

            RuntimeMXBean RuntimemxBean = ManagementFactory.getRuntimeMXBean();
            List<String> arguments = RuntimemxBean.getInputArguments();

            String vm_args = "";

            for(String arg : arguments)
                vm_args += arg + " ";

            trace("Started " + ApplicationInstance.getFullApplicationName());
            trace("VM arguments: " + vm_args);
        }
    }

    //--------------------------------------------------------------------------

    public static synchronized void deinit()
    {
        if (instance != null)
        {
			instance.debugUI.deinit();

            instance.debugUI = null;           
            instance.dateFormat = null;

            instance = null;
        }
    }

    //--------------------------------------------------------------------------

    public static synchronized boolean isActive()
    {
        return active;
    }

	//--------------------------------------------------------------------------

    static synchronized void setTextOutPause(boolean pause)
    {
        instance.textPaused = pause;
    }

	
	//--------------------------------------------------------------------------

    public synchronized static void trace(String text)
    {
		if (active)
			trace(instance.getClassContext()[1].getSimpleName(), text, TraceLevel.INFO);
    }

	//--------------------------------------------------------------------------

    public synchronized static void trace(String text, TraceLevel level)
    {
        if (active)
            trace(instance.getClassContext()[1].getSimpleName(), text, level);
    }

    //--------------------------------------------------------------------------
	
	private static String getThread()
	{
		long threadId = Thread.currentThread().getId();
				
		if (threadId == 1)
			return "System Thread";
		else if (threadId == ApplicationInstance.getMainThreadId())
			return "Main Thread";
		else
			return "Thread " + threadId;
	}
	
	//--------------------------------------------------------------------------
	
    private synchronized static void trace(String callerClassName, String text, TraceLevel level)
    {
        if (!instance.textPaused)
            instance.debugUI.addText("[" + instance.dateFormat.format(new Date()) + " ; " + getThread() + " ; " + callerClassName + "]: " + text + "\n", level);
    }

    //--------------------------------------------------------------------------

    public synchronized static void trace(Throwable throwable)
    {
        trace(null, throwable);
    }

    //--------------------------------------------------------------------------
	
	public static String getThrowableText(String indent, Throwable throwable)
	{
		String throwntext;
		
		if (throwable instanceof ValidationException)
		{
			throwntext = getValidationExceptionString(indent, (ValidationException) throwable);
		}
		else
		{		
			throwntext = indent + throwable.getClass().getCanonicalName() + "\n";
			
			if (throwable instanceof ApplicationRuntimeError)
				throwntext += ((ApplicationRuntimeError)throwable).getInfo().replace("\n", "\n" + indent);
			else 
				throwntext += throwable.getMessage() != null ? indent + throwable.getMessage().trim() + "\n" : "";

			StackTraceElement[] stackElements = throwable.getStackTrace();
						
			for(int i = 0 ; i < stackElements.length ; i++)
				throwntext += indent + i + ".\t" + stackElements[i].toString() + "\n";
			
			Throwable cause = throwable.getCause();
			
			if (cause != null)
			{
				throwntext += "\n" + indent + "Cause: " + cause.getClass().getName() + "\n";
				throwntext += getThrowableText(indent + "\t", cause);
			}
		}
		
		return throwntext;
	}
	
	//--------------------------------------------------------------------------
	
	private static ValidationException findValidationException(Exception exception)
	{
		if (ValidationException.class.isAssignableFrom(exception.getClass()))
			return (ValidationException) exception;
		else if (exception.getCause() != null && Exception.class.isAssignableFrom(exception.getCause().getClass()))
			return findValidationException((Exception) exception.getCause());
		else
			return null;
	}
	
	//--------------------------------------------------------------------------
	
	public static String getDefinitionValidationText(String indent, DefinitionValidation validation)
	{
		String ret = "";
		
		ret += indent + "Validation failed for: " + validation.getClassName() + " [" + validation.getID() + "]\n";			
		ret += indent + validation.getInfo().replace("\n", "\n" + indent);
						
		for(DefinitionValidation val : validation.getSubValidations())
			ret += getDefinitionValidationText(indent + "\t", val) + "\n";
		
		for(Exception exception : validation.getExceptions())
		{
			ValidationException validation_exception = findValidationException(exception);
			
			if (validation_exception != null)
				ret += "\n" + getValidationExceptionString(indent, validation_exception);
			else
				ret += "Occurred Exception: \n" + getThrowableText(indent, exception) + "\n";
		}

		return ret;
	}
	
	//--------------------------------------------------------------------------
	
	public static String getValidationExceptionString(String indent, ValidationException exception)
	{
		ArrayList<DefinitionValidation> validations = exception.getValidation();
	
		String ret = "";
		
		for(DefinitionValidation validation : validations)
			ret += getDefinitionValidationText(indent, validation) + "\n";
		
		return ret;
	}
	
	//--------------------------------------------------------------------------

    public synchronized static void trace(String message, Throwable throwable)
    {
		String throwntext = "";

		if (active)
		{
			if (!instance.textPaused)
			{
				String type = "Throwable";
				
				if (Exception.class.isAssignableFrom(throwable.getClass()))
					type = "Exception";
				else if (Error.class.isAssignableFrom(throwable.getClass()))
					type = "Error";

				throwntext = "[" + type + " occurred at " + instance.dateFormat.format(new Date()) + " ; " + getThread() + "]:\n";
				throwntext += throwable.getClass().getName() + "\n";

				if (message != null)
					throwntext += "Message: " + message + "\n";
				
				throwntext += getThrowableText("", throwable);	
				throwntext = throwntext.trim() + "\n";
												
				instance.debugUI.addText(throwntext, TraceLevel.ERROR);
			}
		}

		if (throwntext.isEmpty())
			logger.log(Level.SEVERE, message, throwable);
		else
			logger.log(Level.SEVERE, throwntext);
    }
	
	//--------------------------------------------------------------------------

	public synchronized static void traceStack(TraceLevel level)
	{
		Class[] stack = instance.getClassContext();
		
		for (int i = 1 ; i < stack.length ; i++)
			instance.debugUI.addText("[" + instance.dateFormat.format(new Date()) + " ; " + getThread() + " ; STACK " + String.format("%02d", i) + "]: " + stack[i].getSimpleName() + "\n", level);
	}
	
    //--------------------------------------------------------------------------

    public synchronized static void alert(String message)
    {
        if (active)
            showAlert(instance.getClassContext()[1].getSimpleName(), "Alert at " + instance.getClassContext()[1].getSimpleName(), message, false);
    }

    //--------------------------------------------------------------------------

    public synchronized static void alert(String title, String message)
    {
        if (active)
            showAlert(instance.getClassContext()[1].getSimpleName(), title, message, false);
    }

    //--------------------------------------------------------------------------

    public synchronized static void fatalAlert(String message)
    {
        if (active)
            showAlert(instance.getClassContext()[1].getSimpleName(), "Alert at " + instance.getClassContext()[1].getSimpleName(), message, true);
    }

    //--------------------------------------------------------------------------

    public synchronized static void fatalAlert(String title, String message)
    {
        if (active)
            showAlert(instance.getClassContext()[1].getSimpleName(), title, message, true);
    }

    //--------------------------------------------------------------------------

    public synchronized static void alertIf(boolean condition, String message)
    {
        if (condition)
			alert(message);
    }

    //--------------------------------------------------------------------------

    public synchronized static void alertIf(boolean condition, String title, String message)
    {
        if (condition)
			alert(title, message);
    }

    //--------------------------------------------------------------------------

    public synchronized static void fatalAlertIf(boolean condition, String message)
    {
        if (condition)
			fatalAlert(message);
    }

    //--------------------------------------------------------------------------

    public synchronized static void fatalAlertIf(boolean condition, String title, String message)
    {
        if (condition)
			fatalAlert(title, message);
    }

    //--------------------------------------------------------------------------
	
	public synchronized static void showErrorDialog(String title, String message)
	{
		if (active)
			instance.debugUI.showErrorDialog(title, message);
	}
	
    //--------------------------------------------------------------------------
	
	public synchronized static void showWarningDialog(String title, String message)
	{
		if (active)
			instance.debugUI.showWarningDialog(title, message);
	}
	
	//--------------------------------------------------------------------------

    private synchronized static void showAlert(String callerClassName, String title, String message, boolean fatal)
    {
        trace(callerClassName, "Alert: " + message, fatal ? TraceLevel.ERROR : TraceLevel.WARNING);
        boolean cont = true;

        if (fatal)
            showErrorDialog(title, message +"\n\nCan't recover from this alert, exiting.");
        else
            cont = instance.debugUI.showConfirmDialog(title, message + "\n\nContinue?");

        if (fatal || !cont)
            System.exit(-1);
    }

    //--------------------------------------------------------------------------
	
	public static synchronized void dumpLog()
	{
		if (active)
		{
			SimpleDateFormat date_format = new SimpleDateFormat("dd-MM-yyy_HH-mm");
			instance.debugUI.saveLog(ApplicationInstance.getUserDataLocation() + "/log_" + date_format.format(new Date()) + ".txt"); 
		}
	}
	
	//--------------------------------------------------------------------------
}

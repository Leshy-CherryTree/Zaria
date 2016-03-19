/****************************************/
/* EditorApplication.java				*/
/* Created on: 02-May-2013				*/
/* Copyright Cherry Tree Studio 2013	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.editor;

import com.jtattoo.plaf.hifi.HiFiLookAndFeel;
import eu.cherrytree.zaria.editor.components.ButtonTabComponent;
import eu.cherrytree.zaria.editor.database.DataBase;
import eu.cherrytree.zaria.editor.debug.DebugConsole;
import eu.cherrytree.zaria.editor.document.DocumentManager;
import eu.cherrytree.zaria.editor.serialization.Serializer;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.SplashScreen;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.xml.sax.SAXException;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public class EditorApplication
{
	//--------------------------------------------------------------------------
	
	private static class ApplicationStartupException extends Exception
	{
		public ApplicationStartupException(String message)
		{
			super(message);
		}

		public ApplicationStartupException(String message, Throwable cause)
		{
			super(message, cause);
		}		
	}
	
	//--------------------------------------------------------------------------
	
	public final static ImageIcon [] icons = {
						new ImageIcon(ButtonTabComponent.class.getResource("/eu/cherrytree/zaria/editor/res/icons/zone/opened.png")) , 
						new ImageIcon(ButtonTabComponent.class.getResource("/eu/cherrytree/zaria/editor/res/icons/zone/edited.png")) ,
						new ImageIcon(ButtonTabComponent.class.getResource("/eu/cherrytree/zaria/editor/res/icons/zone/notopened.png")),
						new ImageIcon(ButtonTabComponent.class.getResource("/eu/cherrytree/zaria/editor/res/icons/zone/unknown.png")),
						new ImageIcon(ButtonTabComponent.class.getResource("/eu/cherrytree/zaria/editor/res/icons/zone/script_opened.png")) , 
						new ImageIcon(ButtonTabComponent.class.getResource("/eu/cherrytree/zaria/editor/res/icons/zone/script_edited.png")) ,
						new ImageIcon(ButtonTabComponent.class.getResource("/eu/cherrytree/zaria/editor/res/icons/zone/script_notopened.png"))
										};
	
	private static EditorFrame frame;
	private static DocumentManager documentManager;
	private static DebugConsole debugConsole;
	
	private static String assetsLocation = null;
	private static String workFilesLocation = null;
	private static String dataBaseLocation = null;
	private static String editorScriptsLocation = null;
	private static String scriptsLocation = null;
	private static String scriptJarLocation = null;
	private static String projectName = "unknown project";
	
	private static ArrayList<String> jarPaths = new ArrayList<>();
	
	private static Properties properties = new Properties();
	
	//--------------------------------------------------------------------------

	public static void main(String args[])
	{	
		try
		{
			System.setProperty("file.encoding", "UTF-8");

			try
			{
				properties.load(EditorApplication.class.getResourceAsStream("/eu/cherrytree/zaria/editor/appproperties/version.properties"));
			}
			catch(IOException ex)
			{
				DebugConsole.logger.log(Level.SEVERE, null, ex);
			}

			showVersionOnSplash();

			if (args.length < 1)
				throw new ApplicationStartupException("Incorrect input parameters. Must input config file path.");

			loadConifg(args[0]);

			if (assetsLocation == null)
				throw new ApplicationStartupException("No assets location!");
			
			if (workFilesLocation  == null)
				throw new ApplicationStartupException("No work files location!");

			if (dataBaseLocation == null)
				throw new ApplicationStartupException("No data base location!");
			
			if (editorScriptsLocation == null)
				throw new ApplicationStartupException("No editor scripts location!");
			
			if (scriptsLocation == null)
				throw new ApplicationStartupException("No script files location!");
			
			if (scriptJarLocation == null)
				throw new ApplicationStartupException("No script jar location!");
					
			Settings.init(projectName);
			
			updateLookAndFeel();

			if (jarPaths.isEmpty())
				JOptionPane.showMessageDialog(null, "No jar files specified!\n", "No jar files!", JOptionPane.ERROR_MESSAGE);

			String [] jarPathArray = new String[jarPaths.size()];		
			jarPaths.toArray(jarPathArray);		

			try
			{
				Serializer.setJarPaths(jarPathArray);
				Serializer.init();
			}
			catch(IOException ex)
			{
				DebugConsole.logger.log(Level.SEVERE, null, ex);
				JOptionPane.showMessageDialog(frame, "Couldn't load palette. " + ex, "Palette error!", JOptionPane.ERROR_MESSAGE);
			}		
						
			if (!DataBase.init(dataBaseLocation))
				throw new ApplicationStartupException("Data base init failed!");

			frame = new EditorFrame(projectName);	
			debugConsole = new DebugConsole(frame);		

			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run()
				{
					frame.setVisible(true);
					debugConsole.addLine("Editor started.");
					debugConsole.addLine("Assets directory: " + assetsLocation);
					debugConsole.addLine("Work files directory: " + workFilesLocation);
					debugConsole.addLine("Data base directory: " + dataBaseLocation);
					debugConsole.addLine("Editor scrtips directory: " + editorScriptsLocation);
				}
			});
		}
		catch(Throwable ex)
		{
			DebugConsole.logger.log(Level.SEVERE, null, ex);						
			JOptionPane.showMessageDialog(frame, "Couldn't start application:\n" + ex.getMessage(), "Fatal error!", JOptionPane.ERROR_MESSAGE);
			
			DataBase.close(false);
		}
	}

	
	//--------------------------------------------------------------------------
			
	private static void showVersionOnSplash()
	{
		SplashScreen splash = SplashScreen.getSplashScreen();
		
		if (splash != null)
		{   
			String version = "Version " + getVersion();
			
            Graphics2D graphics = splash.createGraphics();
			graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			
            Font font = new Font("Dialog", Font.PLAIN, 13);
            
			graphics.setFont(font);	
			graphics.setPaint(new Color(0xffca1f));
			
			FontMetrics metrics = graphics.getFontMetrics(font);
			
			graphics.drawString(version, (splash.getSize().width - metrics.stringWidth(version))/2, 193);
			
			splash.update();
		}
	}
	
	//--------------------------------------------------------------------------
	
	private static String verifyPath(String srcLocation, String path) throws IOException
	{
		// Making sure that the locations exist.
		File assets = new File(path);

		if (!assets.isAbsolute())
			assets = new File(srcLocation + path);

		if (!assets.exists())
			assets.mkdirs();

		// Making sure that we're using absolute paths.
		path = Paths.get(assets.getAbsolutePath()).toRealPath().toString();
		
		// Making sure that the path string ends with a separator.
		if (!path.endsWith(File.separator))
			path += File.separator;
		
		return path;
	}
	
	//--------------------------------------------------------------------------
	
	private static String verifyJar(String srcLocation, String path)
	{
		// Making sure that the locations exist.
		File file = new File(path);
		
		if (!file.isAbsolute())
			file = new File(srcLocation + path);

		// Making sure that we're using absolute paths.
		path = file.getAbsolutePath();
		
		// Making sure that the path string ends with the proper extension.
		if (!path.endsWith(".jar"))
			path += ".jar";
		
		return path;
	}
	
	//--------------------------------------------------------------------------
	
	private static void loadConifg(String path)
	{
		try
		{
			File file = new File(path);
			String root_path = file.getAbsolutePath().replace(file.getName(), "");
						
			Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file);

			doc.getDocumentElement().normalize();
			
			NodeList nList = doc.getElementsByTagName("config");

			for (int temp = 0; temp < nList.getLength(); temp++)
			{
				Node node = nList.item(temp);

				if (node.getNodeType() == Node.ELEMENT_NODE)
				{
					Element element = (Element) node;
					
					assetsLocation = element.getElementsByTagName("assets").item(0).getTextContent();
					workFilesLocation = element.getElementsByTagName("workFiles").item(0).getTextContent();
					dataBaseLocation = element.getElementsByTagName("database").item(0).getTextContent();
					editorScriptsLocation = element.getElementsByTagName("editorScripts").item(0).getTextContent();
					scriptsLocation = element.getElementsByTagName("scripts").item(0).getTextContent();					
					scriptJarLocation = element.getElementsByTagName("scriptjar").item(0).getTextContent();
					
					if (element.getElementsByTagName("project").getLength() > 0)
						projectName = element.getElementsByTagName("project").item(0).getTextContent();
					
					int len = element.getElementsByTagName("jar").getLength();
					
					for (int i = 0 ; i < len ; i++)
						jarPaths.add(verifyJar(root_path, element.getElementsByTagName("jar").item(i).getTextContent()));
				}
			}
									
			assetsLocation = verifyPath(root_path, assetsLocation);
			workFilesLocation = verifyPath(root_path, workFilesLocation);
			dataBaseLocation = verifyPath(root_path, dataBaseLocation) + File.separator + "derby";
			editorScriptsLocation = verifyPath(root_path, editorScriptsLocation);
			scriptsLocation = verifyPath(root_path, scriptsLocation);
			scriptJarLocation = verifyJar(root_path, scriptJarLocation);			
			
		}
		catch (NullPointerException | ParserConfigurationException | SAXException | IOException | DOMException ex)
		{
			DebugConsole.logger.log(Level.SEVERE, null, ex);
		}	
	}
	
	//--------------------------------------------------------------------------
	
	public static void updateLookAndFeel()
	{
		Properties props = new Properties();

		props.put("controlTextFont", "Dialog " + Settings.getUiFontSize());
		props.put("systemTextFont", "Dialog " + Settings.getUiFontSize());
		props.put("userTextFont", "Dialog " + Settings.getUiFontSize());
		props.put("menuTextFont", "Dialog " + Settings.getUiFontSize());
		props.put("windowTitleFont", "Dialog bold " + Settings.getUiFontSize());
		props.put("subTextFont", "Dialog " + Settings.getSmallUiFontSize());

		props.put("logoString", "Zone Editor"); 

		props.put("backgroundColorLight", "32 32 32");
		props.put("backgroundColor", "32 32 32");
		props.put("backgroundColorDark", "32 32 32");

		props.put("foregroundColor", "192 192 192");
		props.put("inputForegroundColor", "192 192 192");
		props.put("buttonForegroundColor", "192 192 192");
		props.put("controlForegroundColor", "192 192 192");
		props.put("menuForegroundColor", "192 192 192");
		props.put("windowTitleForegroundColor", "192 192 192");

		try
		{		
			HiFiLookAndFeel.setTheme(props);
			UIManager.setLookAndFeel("com.jtattoo.plaf.hifi.HiFiLookAndFeel");
		}
		catch(UnsupportedLookAndFeelException | ClassNotFoundException | InstantiationException | IllegalAccessException ex)
		{
			 DebugConsole.logger.log(java.util.logging.Level.SEVERE, null, ex);
		}
	}
			
	//--------------------------------------------------------------------------

	public static DocumentManager getDocumentManager()
	{
		return documentManager;
	}

	//--------------------------------------------------------------------------

	public static DebugConsole getDebugConsole()
	{
		return debugConsole;
	}

	//--------------------------------------------------------------------------

	public static String getAssetsLocation()
	{
		return assetsLocation;
	}

	//--------------------------------------------------------------------------

	public static String getWorkFilesLocation()
	{
		return workFilesLocation;
	}

	//--------------------------------------------------------------------------
	
	public static String getScriptsLocation()
	{
		return scriptsLocation;
	}
	
	//--------------------------------------------------------------------------
	
	public static String getScriptJarLocation()
	{
		return scriptJarLocation;
	}
	
	//--------------------------------------------------------------------------

	public static String getEditorScriptsLocation()
	{
		return editorScriptsLocation;
	}
	
	//--------------------------------------------------------------------------
			
	public static String getVersion()
	{
		return properties.getProperty("MAJOR_VERSION") + "." + properties.getProperty("MINOR_VERSION") + "." + properties.getProperty("REVISION") + "." + properties.getProperty("BUILD").replace(",", "");
	}
	
	//--------------------------------------------------------------------------
	
	public static String getHelpUrl()
	{
		return properties.getProperty("HELP_URL");
	}
		
	//--------------------------------------------------------------------------
	
	public static String getBlogUrl()
	{
		return properties.getProperty("BLOG_URL");
	}
		
	//--------------------------------------------------------------------------
	
	public static ArrayList<String> getJarPaths()
	{
		return jarPaths;
	}
	
	//--------------------------------------------------------------------------
}

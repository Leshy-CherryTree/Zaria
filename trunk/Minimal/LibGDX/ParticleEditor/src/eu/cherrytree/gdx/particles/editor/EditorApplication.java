/****************************************/
/* EditorApplication.java				*/
/* Created on: 02-08-2016				*/
/* Copyright Cherry Tree Studio 2016	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.gdx.particles.editor;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jtattoo.plaf.hifi.HiFiLookAndFeel;

import java.awt.EventQueue;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Properties;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

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
	
	private static String assetsLocation = null;
	private static ArrayList<String> jarPaths = new ArrayList<>();
	
	private static ClassLoader jarLoader;
	private static ObjectMapper mapper;
	
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
	
	private static void loadConifg(String path) throws ParserConfigurationException, SAXException, IOException
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

				int len = element.getElementsByTagName("jar").getLength();

				for (int i = 0 ; i < len ; i++)
					jarPaths.add(verifyJar(root_path, element.getElementsByTagName("jar").item(i).getTextContent()));
			}
		}

		assetsLocation = verifyPath(root_path, assetsLocation);				
	}
	
	//--------------------------------------------------------------------------
	
	private static void loadJars() throws MalformedURLException
	{
		URL [] urls = new URL[jarPaths.size()];
		
		for(int i = 0 ; i < jarPaths.size() ; i++)
			urls[i] = new URL("jar:file:" + jarPaths.get(i) + "!/");
		
		jarLoader = URLClassLoader.newInstance(urls);
	}
	
	//--------------------------------------------------------------------------
	
	private static void createSerializer()
	{
		if (Thread.currentThread().getContextClassLoader() != jarLoader)
			Thread.currentThread().setContextClassLoader(jarLoader);
		
		mapper = new ObjectMapper();
		
		mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
		mapper.setVisibility(PropertyAccessor.GETTER, JsonAutoDetect.Visibility.NONE);
		mapper.setVisibility(PropertyAccessor.IS_GETTER, JsonAutoDetect.Visibility.NONE);
		mapper.setVisibility(PropertyAccessor.SETTER, JsonAutoDetect.Visibility.NONE);
		mapper.setVisibility(PropertyAccessor.CREATOR, JsonAutoDetect.Visibility.NONE);
		
		mapper.configure(MapperFeature.AUTO_DETECT_GETTERS, false);
		mapper.configure(MapperFeature.AUTO_DETECT_CREATORS, false);
		mapper.configure(MapperFeature.AUTO_DETECT_SETTERS, false);
		mapper.configure(MapperFeature.AUTO_DETECT_IS_GETTERS, false);
		mapper.configure(MapperFeature.AUTO_DETECT_IS_GETTERS, false);
		mapper.configure(MapperFeature.USE_GETTERS_AS_SETTERS, false);
		mapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
		mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
		
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		
		mapper.configure(JsonGenerator.Feature.QUOTE_FIELD_NAMES, false);	
	}

	//--------------------------------------------------------------------------
	
	public static ObjectMapper getSerializer()
	{
		return mapper;
	}
	
	//--------------------------------------------------------------------------
	
	public static void main(String[] args)
	{
		if (args.length != 1)
		{
			System.err.println("Must input path to assets.");
			return;
		}
		
		try
		{
			loadConifg(args[0]);
			loadJars();
			createSerializer();
		}
		catch (ParserConfigurationException | SAXException | IOException ex)
		{
			ex.printStackTrace();
			return;
		}
		
		Properties props = new Properties();

		props.put("controlTextFont", "Dialog " + 13);
		props.put("systemTextFont", "Dialog " + 13);
		props.put("userTextFont", "Dialog " + 13);
		props.put("menuTextFont", "Dialog " + 13);
		props.put("windowTitleFont", "Dialog bold " + 13);
		props.put("subTextFont", "Dialog " + 10);

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
		catch (UnsupportedLookAndFeelException | ClassNotFoundException | InstantiationException | IllegalAccessException ex)
		{
			ex.printStackTrace();
		}

		ParticleEffectZoneContainer.init(assetsLocation);
		
		EventQueue.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				new ParticleEditor();
			}
		});
	}
	
	//--------------------------------------------------------------------------
}

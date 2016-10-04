/****************************************/
/* ZoneDocument.java						*/
/* Created on: 04-May-2013				*/
/* Copyright Cherry Tree Studio 2013		*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.editor.document;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import eu.cherrytree.zaria.debug.SyncInterface;
import eu.cherrytree.zaria.editor.EditorApplication;
import eu.cherrytree.zaria.editor.classlist.ZoneClassList;
import eu.cherrytree.zaria.editor.database.DataBase;
import eu.cherrytree.zaria.editor.debug.DebugConsole;
import eu.cherrytree.zaria.editor.ReflectionTools;
import eu.cherrytree.zaria.editor.properties.propertysheet.PropertySheetPanel;
import eu.cherrytree.zaria.editor.serialization.Serializer;
import eu.cherrytree.zaria.serialization.DefinitionValidation;
import eu.cherrytree.zaria.serialization.ValidationException;
import eu.cherrytree.zaria.serialization.ZariaObjectDefinition;
import eu.cherrytree.zaria.serialization.ZariaObjectDefinitionLibrary;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;
import java.util.logging.Level;

import javax.swing.BoxLayout;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;
import javax.swing.text.BadLocationException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public class ZoneDocument
{
	//--------------------------------------------------------------------------
	
	public enum EditType
	{
		TEXT_EDIT,
		GRAPH_EDIT
	}	
	
	//--------------------------------------------------------------------------
	
	public enum DocumentLocationType
	{
		ASSETS,
		SCRIPTS;		
		
		public String getPath()
		{
			switch(this)
			{
				case ASSETS:
					return EditorApplication.getAssetsLocation();
					
				case SCRIPTS:
					return EditorApplication.getScriptsLocation();
					
				default:
					return null;
			}
		}
	};
	
	//--------------------------------------------------------------------------
	
	public enum DocumentType
	{				
		ZONE						("zone", "Zone Game File", DocumentLocationType.ASSETS),
		ZONE_LIBRARY			("zonelib", "Zone Game Library", DocumentLocationType.ASSETS),
		ZONE_SCRIPT				("zonescript", "Zone Script", DocumentLocationType.SCRIPTS);

		private DocumentType(String suffix, String name, DocumentLocationType locationType)
		{
			this.suffix = "." + suffix;
			this.name = name;
			this.locationType = locationType;
		}
						
		private String suffix;
		private String name;
		private DocumentLocationType locationType;

		public String getSuffix()
		{
			return suffix;
		}	

		public String getName()
		{
			return name;
		}				

		public DocumentLocationType getLocationType()
		{
			return locationType;
		}				
	}
	
	//--------------------------------------------------------------------------
	
	public static final String dateCreatedComment = "// Created on: ";
	public static final String dateModifiedCreatedComment = "// Last modified on: ";
	public static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy", Locale.UK);
		
	//--------------------------------------------------------------------------
	
	public static boolean isZone(String path)
	{
		path = path.toLowerCase();
		
		for (DocumentType type : DocumentType.values())
		{
			if (path.endsWith(type.getSuffix().toLowerCase()))
				return type != DocumentType.ZONE_SCRIPT;
		}
		
		return false;
	}
	
	//--------------------------------------------------------------------------
	
	public static boolean isScript(String path)
	{
		return path.toLowerCase().endsWith(DocumentType.ZONE_SCRIPT.getSuffix());
	}
	
	//--------------------------------------------------------------------------
	
	private JTabbedPane tabPane;
	private JPanel panel;
	
	private EditorState [] states = new EditorState[EditType.values().length];
	private EditType currentState = EditType.TEXT_EDIT;
	private DocumentType documentType;
	
	private File file;
	private Date createdDate;
	private DocumentManager documentManager;
	
	private boolean saved = true;

	//--------------------------------------------------------------------------

	public ZoneDocument(DocumentType documentType, JTabbedPane tabPane, File file, final DocumentManager documentManager, PropertySheetPanel propertySheetPanel, JPopupMenu popupMenu, ZoneClassList zoneClassList)
	{
		this.tabPane = tabPane;	
		this.documentType = documentType;
		this.file = file;
		this.documentManager = documentManager;
		
		String text = "";
		
		if (file == null)
		{
			text = getNewText(documentType);
			
			EditorApplication.getDebugConsole().addLine("Created new file.");
		}
		else
		{						
			try
			{
				text = loadFileAsString(file);
				
				EditorApplication.getDebugConsole().addLine("Opened " + file.getName() + ".");
			}
			catch(IOException ex)
			{
				JOptionPane.showMessageDialog(null,"Couldn't load file " + file.getAbsolutePath() + ".\n" + ex,"Error!",JOptionPane.ERROR_MESSAGE);
				
				DebugConsole.logger.log(Level.SEVERE, null, ex);
			}
		}
		
		createdDate = getCreationDate(text);		
		
		switch(documentType)
		{
			case ZONE:
				currentState = EditType.GRAPH_EDIT;
			
				states[EditType.TEXT_EDIT.ordinal()] = new TextEditorState(this, zoneClassList);
				states[EditType.GRAPH_EDIT.ordinal()] = new GraphEditorState(this, propertySheetPanel, popupMenu, zoneClassList);
				
				break;
				
			case ZONE_LIBRARY:
			case ZONE_SCRIPT:
				currentState = EditType.TEXT_EDIT;
				
				states[EditType.TEXT_EDIT.ordinal()] = new TextEditorState(this, zoneClassList);
				states[EditType.GRAPH_EDIT.ordinal()] = null;
				
				break;
		}		
					
		panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		panel.setName("Editor");
		tabPane.add(panel);

		if (file != null)
			tabPane.setToolTipTextAt(tabPane.indexOfComponent(panel), file.getAbsolutePath());	
				
		boolean result = states[currentState.ordinal()].setText(text);
		
		// If we couldn't open the apprioprate editor, fall back to text editing.
		if (!result)
		{
			currentState = EditType.TEXT_EDIT;
			
			for (EditType type : EditType.values())
			{
				if (type != EditType.TEXT_EDIT)
					states[type.ordinal()] = null;
			}
			
			states[currentState.ordinal()].setText(text);
		}
		
		
		states[currentState.ordinal()].attach(panel);
				
		saved = true;
	}
	
	//--------------------------------------------------------------------------
	
	public DocumentManager getDocumentManager()
	{
		return documentManager;
	}
	
	//--------------------------------------------------------------------------

	public File getFile()
	{
		return file;
	}
	
	//--------------------------------------------------------------------------
	
	public Date getCreatedDate()
	{
		return createdDate;
	}
	
	//--------------------------------------------------------------------------
	
	public static String getNewHeader(String name)
	{
		String text = "// " + name + "\n";
		
		Calendar cal = Calendar.getInstance();
		cal.clear();
		cal.setTimeInMillis(System.currentTimeMillis());
						
		text += dateCreatedComment + dateFormat.format(cal.getTime()) + "\n";
		text += dateModifiedCreatedComment + dateFormat.format(cal.getTime()) + "\n";
		text += "\n";
		
		return text;
	}
	
	//--------------------------------------------------------------------------
	
	private String getNewText(DocumentType type)
	{
		String text = "// untitled" + type.suffix + "\n";
		
		Calendar cal = Calendar.getInstance();
		cal.clear();
		cal.setTimeInMillis(System.currentTimeMillis());
						
		text += dateCreatedComment + dateFormat.format(cal.getTime()) + "\n";
		text += dateModifiedCreatedComment + dateFormat.format(cal.getTime()) + "\n";
		text += "\n";
		
		switch(type)
		{
			case ZONE:
				
				text += "[\n";
				text += "\n";
				text += "]\n";
				text += "\n";
				
				break;
				
			case ZONE_LIBRARY:
				
				try
				{
					text += Serializer.getText(ReflectionTools.createNewDefinition(ZariaObjectDefinitionLibrary.class));
				}
				catch(JsonProcessingException ex)
				{
					DebugConsole.logger.log(Level.SEVERE, null, ex);
				}
				
				text += "\n";
				
				break;
		}
		
		return text;
	}
	
	//--------------------------------------------------------------------------
	
	public static Date getCreationDate(String text)
	{
		Date ret = null;
		
		if (text.contains(dateCreatedComment))
		{
			int index_beg = text.indexOf(dateCreatedComment) + dateCreatedComment.length();
			int index_end = text.substring(index_beg).indexOf("\n") + index_beg;
			
			String date_text = text.substring(index_beg, index_end);		
			
			try
			{
				ret = dateFormat.parse(date_text);
			}
			catch(ParseException ex)
			{
				DebugConsole.logger.log(Level.SEVERE, null, ex);
			}
		}
		
		return ret;
	}	
	
	//--------------------------------------------------------------------------
	
	public static String loadFileAsString(File file) throws IOException
	{		
		byte[] bytes;
		
		try (InputStream in = new FileInputStream(file))
		{
			long length = file.length();
			
			if (length > Integer.MAX_VALUE)
				throw new IOException("File is too large!");
			
			bytes = new byte[(int) length];
			int offset = 0;
			int numRead = 0;
			
			while (offset < bytes.length && (numRead = in.read(bytes, offset, bytes.length - offset)) >= 0)
				offset += numRead;
			
			if (offset < bytes.length)
			   throw new IOException("Could not completely read file " + file);
		}

		return new String(bytes);
	}
	
	//--------------------------------------------------------------------------
	
	public void save(File file) throws IOException
	{
		this.file = file;
		
		tabPane.setToolTipTextAt(tabPane.indexOfComponent(panel), file.getAbsolutePath());	
		
		save();
	}
	
	//--------------------------------------------------------------------------
	
	public void save() throws IOException
	{		
		if (file == null)
			file = DocumentManager.showSaveDialog(documentType);
			
		if (file != null)
		{
			try (BufferedWriter writer = new BufferedWriter(new FileWriter(file)))
			{
				states[currentState.ordinal()].updateText();
				writer.write(states[currentState.ordinal()].getText());
				saved = true;
			}
			
			if (documentType == DocumentType.ZONE)
			{
				ArrayList<UUID> removed = DataBase.save(states[currentState.ordinal()].getDefinitions(), file.getAbsolutePath());			
				ArrayList<UUID> dangling_links = new ArrayList<>();

				for (UUID uuid : removed)
					DataBase.checkLinks(uuid, dangling_links);

				if (!dangling_links.isEmpty())
				{
					String info = "Saving of " + file.getName() + " resulted in the following definitons having invalid links:\n\n";

					for (UUID uuid : dangling_links)
						info += "\t" + DataBase.getID(uuid) + ":\t\t" + DataBase.getLocation(uuid) + "\n";

					DocumentManager.showInfoDialog("Broken links detected", info);
				}	
			}			
		}
		
		tabPane.repaint();
	}
	
	//--------------------------------------------------------------------------
	
	public void close()
	{
		states[currentState.ordinal()].detach(panel);
		tabPane.remove(panel);
	}
	
	//--------------------------------------------------------------------------
	
	public void setModified()
	{
		saved = false;
		tabPane.repaint();
	}

	//--------------------------------------------------------------------------
	
	public boolean isSaved()
	{
		return saved;
	}
			
	//--------------------------------------------------------------------------
	
	public void undo()
	{
		states[currentState.ordinal()].undo();
	}
	
	//--------------------------------------------------------------------------
	
	public void redo()
	{
		states[currentState.ordinal()].redo();
	}
	
	//--------------------------------------------------------------------------
	
	public void cut()
	{
		states[currentState.ordinal()].cut();
	}
	
	//--------------------------------------------------------------------------
	
	public void copy()
	{
		states[currentState.ordinal()].copy();
	}
	
	//--------------------------------------------------------------------------
	
	public void paste()
	{
		states[currentState.ordinal()].paste();
	}
	
	//--------------------------------------------------------------------------
	
	public void selectAll()
	{
		states[currentState.ordinal()].selectAll();
	}

	//--------------------------------------------------------------------------
	
	public boolean find(String text, FindOptions options)
	{			
		return states[currentState.ordinal()].find(text, options);
	}
	
	//--------------------------------------------------------------------------
	
	public boolean replace(String text, String replace, FindOptions options)
	{		
		return states[currentState.ordinal()].replace(text, replace, options);
	}

	//--------------------------------------------------------------------------
	
	public boolean replaceFind(String text, String replace, FindOptions options)
	{
		return states[currentState.ordinal()].replaceFind(text, replace, options);
	}
	
	//--------------------------------------------------------------------------
	
	public int replaceAll(String text, String replace, FindOptions options)
	{	
		return states[currentState.ordinal()].replaceAll(text, replace, options);
	}
	
	//--------------------------------------------------------------------------
	
	public void unmark()
	{
		states[currentState.ordinal()].unmark();
	}
	
	//--------------------------------------------------------------------------
	
	public void goToLine(int line) throws BadLocationException
	{
		states[currentState.ordinal()].goToLine(line);
	}
	
	//--------------------------------------------------------------------------
	
	public void editSelected()
	{
		states[currentState.ordinal()].editSelected();
	}
	
	//--------------------------------------------------------------------------
	
	public String getTitle()
	{
		if (file != null)
			return file.getName();
		else
			return "untitled" + documentType.getSuffix();
	}	

	//--------------------------------------------------------------------------
	
	public String getHeader(String title)
	{
		Calendar cal = Calendar.getInstance();
		cal.clear();
		cal.setTimeInMillis(System.currentTimeMillis());
		
		String ret = "// " + title + "\n";
		
		if (getCreatedDate() != null)
			ret += dateCreatedComment + dateFormat.format(getCreatedDate()) + "\n";
		
		ret += dateModifiedCreatedComment + dateFormat.format(cal.getTime()) + "\n";
		
		ret += "\n";
		
		return ret;
	}
	
	//--------------------------------------------------------------------------
	
	public String getHeader()
	{				
		return getHeader(getTitle());
	}
	
	//--------------------------------------------------------------------------
	
	public String getPath()
	{
		if (file != null)
			return file.getAbsolutePath();
		else
			return null;
	}
	
	//--------------------------------------------------------------------------
	
	public void setState(EditType state)
	{
		if (state != currentState)
		{						
			String text = states[currentState.ordinal()].getText();
			
			states[currentState.ordinal()].detach(panel);
			
			currentState = state;					
			
			states[currentState.ordinal()].attach(panel);
			
			states[currentState.ordinal()].setText(text);
			
			panel.repaint();
		}
	}
	
	//--------------------------------------------------------------------------
	
	public void setText(String text)
	{
		states[currentState.ordinal()].setText(text);
		panel.repaint();
	}
	
	//--------------------------------------------------------------------------
	
	public void onFocusGained()
	{
		unmark();
				
		for (EditorState state : states)
		{
			if (state != null)
				state.onFocusGained(panel);
		}
	}
	
	//--------------------------------------------------------------------------
	
	public void onFocusLost()
	{
		for (EditorState state : states)
		{
			if (state != null)
				state.onFocusLost(panel);
		}
	}
	
	//--------------------------------------------------------------------------
	
	public EditorState getEditorState(EditType state)
	{
		return states[state.ordinal()];
	}
	
	//--------------------------------------------------------------------------
	
	public EditType getEditType()
	{
		return currentState;
	}
	
	//--------------------------------------------------------------------------
	
	public DocumentType getDocumentType()
	{
		return documentType;
	}
	
	//--------------------------------------------------------------------------
	
	public String getText()
	{
		return states[currentState.ordinal()].getText();
	}
	
	//--------------------------------------------------------------------------
	
	public void updateSettings()
	{
		for (EditorState state : states)
		{
			if (state != null)
				state.updateSettings();
		}
	}
	
	//--------------------------------------------------------------------------
	
	public Object getSelected()
	{
		return states[currentState.ordinal()].getSelected();
	}
	
	//--------------------------------------------------------------------------
	
	public static ZariaObjectDefinitionLibrary validateLibrary(String libraryText) throws IOException, ValidationException
	{
		ObjectMapper objectMapper = Serializer.createDeserializationMapper();										
		
		ZariaObjectDefinitionLibrary library = objectMapper.readValue(libraryText, ZariaObjectDefinitionLibrary.class);				

		DefinitionValidation val = library.validate();
		
		if (val.hasErrors())
		{
			ArrayList<DefinitionValidation> ret = new ArrayList<>();
			ret.add(val);

			throw new ValidationException("Couldn't validate library!", ret);
		}
		
		return library;
	}
	
	//--------------------------------------------------------------------------
	
	private static Field getField(Class c, String name)
	{
		ArrayList<Field> fields_array = new ArrayList<>();
		
		while(c != null && ZariaObjectDefinition.class.isAssignableFrom(c))
		{
			Field [] fields = c.getDeclaredFields();
			fields_array.addAll(Arrays.asList(fields));
			
			c = c.getSuperclass();
		}
		
		for (Field f : fields_array)
		{
			if (f.getName().equals(name))
				return f;
		}
		
		return null;
	}
	
	//--------------------------------------------------------------------------
	
	public static ZoneMetadata getMetadata(Object definition)
	{
		assert definition != null;
		
		ZoneMetadata metadata = null;
				
		String str = "";
		
		try
		{			
			Field f = getField(definition.getClass(), "metadata");
			f.setAccessible(true);

			str = (String) f.get(definition);
		}
		catch(IllegalArgumentException | IllegalAccessException | SecurityException ex)
		{
			DebugConsole.logger.log(Level.SEVERE, null, ex);
		}
			
		if (str != null && !str.isEmpty())
		{
			try
			{
				JAXBContext context = JAXBContext.newInstance(ZoneMetadata.class);
				Unmarshaller um = context.createUnmarshaller();
				metadata = (ZoneMetadata) um.unmarshal(new StringReader(str));
			}
			catch(JAXBException ex)
			{
				DebugConsole.logger.log(Level.SEVERE, null, ex);
			}
		}
		
		if (metadata == null)
			metadata = new ZoneMetadata();
		
		return metadata;
	}
	
	//--------------------------------------------------------------------------
	
	public static void saveMetadata(ZariaObjectDefinition definition, ZoneMetadata metadata)
	{	
		String str = "";
		
		try
		{						
			JAXBContext context = JAXBContext.newInstance(ZoneMetadata.class);
			Marshaller m = context.createMarshaller();
			
			StringWriter writer = new StringWriter();
			
			m.marshal(metadata, writer);
			
			str = writer.getBuffer().toString();
		}
		catch(JAXBException ex)
		{
			DebugConsole.logger.log(Level.SEVERE, null, ex);
		}
			
		try
		{			
			Field f = getField(definition.getClass(), "metadata");
			f.setAccessible(true);			
			f.set(definition, str);
		}
		catch(SecurityException | IllegalArgumentException | IllegalAccessException ex)
		{
			DebugConsole.logger.log(Level.SEVERE, null, ex);
		}
	}
	
	//--------------------------------------------------------------------------
	
	public static ZariaObjectDefinitionLibrary getLibrary(String path) throws IOException
	{
		String libraryText = loadFileAsString(new File(path));
		
		if (libraryText.isEmpty())
			throw new NullPointerException();
				
		return validateLibrary(libraryText);
	}
	
	//--------------------------------------------------------------------------
	
	public void setEditingObjectEnabled(boolean enabled)
	{
		documentManager.setEditingObjectEnabled(enabled);
	}
	
	//--------------------------------------------------------------------------
	
	public void syncWithGame() throws RemoteException, NotBoundException
	{
		Registry registry = LocateRegistry.getRegistry();
		SyncInterface sync_manager = (SyncInterface) registry.lookup("SyncManager");

		sync_manager.sync(states[currentState.ordinal()].getText());
	}
	
	//--------------------------------------------------------------------------
}

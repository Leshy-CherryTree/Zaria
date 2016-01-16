/****************************************/
/* DataBase.java						*/
/* Created on: 11-Mar-2015				*/
/* Copyright Cherry Tree Studio 2015	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.editor.database;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.cherrytree.zaria.editor.EditorApplication;
import eu.cherrytree.zaria.editor.debug.DebugConsole;
import eu.cherrytree.zaria.editor.properties.PropertyTools;
import eu.cherrytree.zaria.editor.serialization.Serializer;
import eu.cherrytree.zaria.serialization.ZariaObjectDefinition;
import java.io.File;
import java.io.IOException;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.ArrayList;
import java.util.Properties;
import java.util.UUID;
import java.util.logging.Level;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public class DataBase
{
	//--------------------------------------------------------------------------
	
	public static class DuplicateUUIDFoundException extends IOException
	{
		public DuplicateUUIDFoundException(UUID uuid)
		{
			super("Found duplicate UUID " + uuid);
		}		
	}
	
	//--------------------------------------------------------------------------
	
	private static Connection connection;
	
	//--------------------------------------------------------------------------
	
	public static boolean init(String dbPath)
	{	
		Statement s = null;
		
		try
		{
			connection = DriverManager.getConnection("jdbc:derby:" + dbPath + ";create=true", new Properties());
			connection.setAutoCommit(false);
			
			boolean schema = checkIfSchemaExists();
			
			if(schema)
			{
				s = connection.createStatement();
				s.execute("SET CURRENT SCHEMA ZONE");
			}
			else
			{
				s = connection.createStatement();
				s.execute("CREATE SCHEMA zone");
				
				s.close();
				s = null;
				
				s = connection.createStatement();
				s.execute("SET CURRENT SCHEMA ZONE");
				
				if(checkIfTableExists("definitions"))
					deleteTable("definitions");
				
				if(checkIfTableExists("links"))
					deleteTable("links");
				
				createTables();
				
				rebuildData();
				
				connection.commit();
			}
		}
		catch (SQLException ex)
		{						
			DebugConsole.logger.log(Level.SEVERE, null,  ex);
			close(false);
			return false;
		}
		finally
		{
			try
			{
				if(s != null)
					s.close();
			}
			catch (SQLException ex)
			{
				DebugConsole.logger.log(Level.SEVERE, null,  ex);
			}
		}
		
		return true;
	}
	
	//--------------------------------------------------------------------------
	
	public static void rebuildDataBase()
	{
		try
		{
			deleteTable("links");
			deleteTable("definitions");
			createTables();
			rebuildData();

			connection.commit();
		}
		catch (SQLException ex)
		{						
			DebugConsole.logger.log(Level.SEVERE, null,  ex);
		}
	}
	
	//--------------------------------------------------------------------------
	
	private static boolean checkIfSchemaExists()
	{
		Statement s = null;
		ResultSet rs = null;
		
		try
		{
			s = connection.createStatement();
			rs = s.executeQuery("SELECT * FROM SYS.SYSSCHEMAS");
			
			while(rs.next())
			{
				if(rs.getString(2).equals("ZONE"))
					return true;
			}				
		}
		catch (SQLException ex)
		{
			DebugConsole.logger.log(Level.SEVERE, null,  ex);
		}
		finally
		{			
			try
			{
				if(rs != null)
					rs.close();
				
				if(s != null)
					s.close();
			}
			catch (SQLException ex)
			{
				DebugConsole.logger.log(Level.SEVERE, null,  ex);
			}
		}
		
		return false;
	}
	
	//--------------------------------------------------------------------------
			
	private static boolean checkIfTableExists(String name) throws SQLException
	{
		boolean exists = false;
		ResultSet rs = null;
		
		try
		{			
			DatabaseMetaData dbmd = connection.getMetaData();
			rs = dbmd.getTables(null, "ZONE", name.toUpperCase(), null);
			
			exists = rs.next();						
		}
		finally
		{			
			if(rs != null)
				rs.close();

		}

		return exists;
	}
	
	//--------------------------------------------------------------------------
	
	private static void getDefinitions(File file, ObjectMapper mapper,  ArrayList<ZariaObjectDefinition> definitions, ArrayList<String> filePaths)
	{
		if(file.isDirectory())
		{
			File[] files = file.listFiles();
			
			for(File f : files)
				getDefinitions(f, mapper, definitions, filePaths);
		}
		else if(file.getPath().endsWith(".zone") || file.getPath().endsWith(".zonemap"))
		{
			ZariaObjectDefinition[] defs = null;
			
			try
			{
				defs = mapper.readValue(file, ZariaObjectDefinition[].class);
			}
			catch (IOException ex)
			{
				DebugConsole.logger.log(Level.SEVERE, null,  ex);
			}

			if(defs != null)
			{
				for(ZariaObjectDefinition def : defs)
				{
					definitions.add(def);
					filePaths.add(file.getAbsolutePath());
				}
			}
		}
	}
	
	//--------------------------------------------------------------------------
	
	private static void deleteTable(String table)
	{
		Statement s = null;
		try
		{
			s = connection.createStatement();
			s.execute("DROP TABLE " + table);
		}
		catch (SQLException ex)
		{
			DebugConsole.logger.log(Level.SEVERE, null,  ex);
		}
		finally
		{			
			try
			{
				if(s != null)
					s.close();
			}
			catch (SQLException ex)
			{
				DebugConsole.logger.log(Level.SEVERE, null,  ex);
			}
		}		
	}
	
	//--------------------------------------------------------------------------
	
	private static void createTables()
	{
		Statement s = null;
		try
		{
			s = connection.createStatement();
			s.execute("CREATE TABLE definitions(uuid char(36), id varchar(256), file varchar(1024))");
			s.execute("CREATE TABLE links(src char(36), tgt char(36))");
		}
		catch (SQLException ex)
		{
			DebugConsole.logger.log(Level.SEVERE, null,  ex);
		}
		finally
		{			
			try
			{
				if(s != null)
					s.close();
			}
			catch (SQLException ex)
			{
				DebugConsole.logger.log(Level.SEVERE, null,  ex);
			}
		}
	}
	
	//--------------------------------------------------------------------------
	
	private static void rebuildData()
	{		
		File assets = new File(EditorApplication.getAssetsLocation());
		ArrayList<ZariaObjectDefinition> definitions = new ArrayList<>();
		ArrayList<String> filePaths = new ArrayList<>();
		ObjectMapper mapper= Serializer.createDeserializationMapper();
		
		getDefinitions(assets, mapper, definitions, filePaths);

		assert definitions.size() == filePaths.size();
		
		for(int i = 0 ; i < definitions.size() ; i++)
			addDefinition(definitions.get(i), filePaths.get(i));
	}
	
	//--------------------------------------------------------------------------
	
	public static boolean checkLinks(UUID uuid, ArrayList<UUID> linkingUUIDS)
	{
		Statement s = null;
		ResultSet rs = null;
		
		try
		{
			s = connection.createStatement();
			rs = s.executeQuery("SELECT src FROM links WHERE tgt='" + uuid.toString() + "'");
			
			while(rs.next())
				linkingUUIDS.add(UUID.fromString(rs.getString(1)));
		}
		catch (SQLException ex)
		{
			DebugConsole.logger.log(Level.SEVERE, null, ex);
		}
		finally
		{			
			try
			{
				if(rs != null)
					rs.close();
				
				if(s != null)
					s.close();
			}
			catch (SQLException ex)
			{
				DebugConsole.logger.log(Level.SEVERE, null, ex);
			}
		}	
		
		return !linkingUUIDS.isEmpty();
	}
	
	//--------------------------------------------------------------------------
	
	private static void removeDefinition(UUID uuid)
	{
		Statement s = null;
		try
		{
			s = connection.createStatement();
			s.execute("DELETE FROM definitions WHERE uuid='" + uuid.toString() + "'");
			s.execute("DELETE FROM links WHERE src='" + uuid.toString() + "'");
		}
		catch (SQLException ex)
		{
			DebugConsole.logger.log(Level.SEVERE, null,  ex);
		}
		finally
		{			
			try
			{
				if(s != null)
					s.close();
			}
			catch (SQLException ex)
			{
				DebugConsole.logger.log(Level.SEVERE, null,  ex);
			}
		}		
	}
	
	//--------------------------------------------------------------------------
	
	private static ArrayList<UUID> getDefinitionsInFile(String file)
	{
		Statement s = null;
		ResultSet rs = null;
		
		ArrayList<UUID> uuids = new ArrayList<>();
		
		try
		{
			s = connection.createStatement();
			rs = s.executeQuery("SELECT uuid FROM definitions WHERE file='" + file +"'");
			
			while(rs.next())
				uuids.add(UUID.fromString(rs.getString(1)));
		}
		catch (SQLException ex)
		{
			DebugConsole.logger.log(Level.SEVERE, null,  ex);
		}
		finally
		{			
			try
			{
				if(rs != null)
					rs.close();
				
				if(s != null)
					s.close();
			}
			catch (SQLException ex)
			{
				DebugConsole.logger.log(Level.SEVERE, null,  ex);
			}
		}
		
		return uuids;
	}	
	
	//--------------------------------------------------------------------------
	
	private static void addDefinition(ZariaObjectDefinition definition, String file)
	{
		ArrayList<UUID> links = PropertyTools.getAllLinks(definition);
		
		PreparedStatement insert = null;
		
		try
		{		
			insert = connection.prepareStatement("INSERT INTO definitions VALUES (?, ?, ?)");
			insert.setString(1, definition.getUUID().toString());
			insert.setString(2, definition.getID());
			insert.setString(3, file);
			
			insert.executeUpdate();
			
			insert.close();
			insert = null;
			
			for(UUID uuid : links)
			{
				insert = connection.prepareStatement("INSERT INTO links VALUES (?, ?)");
				insert.setString(1, definition.getUUID().toString());
				insert.setString(2, uuid.toString());

				insert.executeUpdate();

				insert.close();
				insert = null;	
			}
		}
		catch (SQLException ex)
		{
			DebugConsole.logger.log(Level.SEVERE, null, ex);
		}
		finally
		{			
			try
			{
				if(insert != null)
					insert.close();
			}
			catch (SQLException ex)
			{
				DebugConsole.logger.log(Level.SEVERE, null, ex);
			}
		}		
	}
	
	//--------------------------------------------------------------------------
	
	public static ArrayList<UUID> save(ZariaObjectDefinition[] definitions, String file)
	{		
		//Purge the DB of all entries for that file.
		ArrayList<UUID> uuids = getDefinitionsInFile(file);

		for(UUID uuid : uuids)
			removeDefinition(uuid);
			
		if(definitions != null)
		{
			// Save all input defintions.
			for(ZariaObjectDefinition definition : definitions)
			{
				uuids.remove(definition.getUUID());
				addDefinition(definition, file);	
			}
		}
		
		try
		{
			connection.commit();
		}
		catch (SQLException ex)
		{
			DebugConsole.logger.log(Level.SEVERE, null, ex);
		}
		
		return uuids;
	}
	
	//--------------------------------------------------------------------------
	
	public static String getLocation(UUID uuid) throws DuplicateUUIDFoundException
	{
		return getDefinitionData(uuid, "file");
	}	
	//--------------------------------------------------------------------------
	
	
	public static String getID(UUID uuid) throws DuplicateUUIDFoundException
	{
		String id = getDefinitionData(uuid, "id");		
		return id != null ? id : "[BROKEN LINK]";
	}
	
	//--------------------------------------------------------------------------
	
	public static String getDefinitionData(UUID uuid, String type) throws DuplicateUUIDFoundException
	{
		String data = null;
		Statement s = null;
		ResultSet rs = null;
		try
		{
			s = connection.createStatement();
			rs = s.executeQuery("SELECT " + type + " FROM definitions WHERE uuid='" + uuid.toString() +"'");
			
			while(rs.next())
			{
				if(data != null)
					throw new DuplicateUUIDFoundException(UUID.fromString(data));
				
				data = rs.getString(1);
			}
		}
		catch (SQLException ex)
		{
			DebugConsole.logger.log(Level.SEVERE, null, ex);
		}
		finally
		{			
			try
			{
				if(rs != null)
					rs.close();
				
				if(s != null)
					s.close();
			}
			catch (SQLException ex)
			{
				DebugConsole.logger.log(Level.SEVERE, null, ex);
			}
		}	
		
		return data;
	}	
	
	//--------------------------------------------------------------------------
	
	public static void close(boolean normal)
	{	
		if(normal)
		{
			try
			{
				DriverManager.getConnection("jdbc:derby:;shutdown=true");
			}
			catch (SQLException ex)
			{
				if((ex.getErrorCode() != 50000) || !("XJ015".equals(ex.getSQLState())))
					DebugConsole.logger.log(Level.SEVERE, null, ex);
			}			
		}
		
		try
		{
			if (connection != null)
				connection.close();
		}
		catch (SQLException ex)
		{
			DebugConsole.logger.log(Level.SEVERE, null, ex);
		}	
	}
	
	//--------------------------------------------------------------------------
	
	public static void dumpDataBase()
	{
		Statement s = null;
		ResultSet rs = null;
		try
		{
			s = connection.createStatement();
			rs = s.executeQuery("SELECT * FROM definitions");
			
			while(rs.next())
				System.out.println("\t" + rs.getString(1)  + "\t" + rs.getString(2) + "\t" + rs.getString(3));
			
		}
		catch (SQLException ex)
		{
			DebugConsole.logger.log(Level.SEVERE, null, ex);
		}
		finally
		{			
			try
			{
				if(rs != null)
					rs.close();
				
				if(s != null)
					s.close();
			}
			catch (SQLException ex)
			{
				DebugConsole.logger.log(Level.SEVERE, null, ex);
			}
		}	
	}
	
	//--------------------------------------------------------------------------
}

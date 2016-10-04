/****************************************/
/* ZoneFileListModel.java				*/
/* Created on: 15-Mar-2015				*/
/* Copyright Cherry Tree Studio 2015	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.editor.datamodels.file;

import com.fasterxml.jackson.databind.ObjectMapper;

import eu.cherrytree.zaria.editor.debug.DebugConsole;
import eu.cherrytree.zaria.editor.document.ZoneDocument.DocumentType;
import eu.cherrytree.zaria.editor.serialization.Serializer;
import eu.cherrytree.zaria.serialization.ZariaObjectDefinition;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public class ZoneFileListModel extends FileListModel
{
	//--------------------------------------------------------------------------
	
	private Class<? extends ZariaObjectDefinition> zoneClass;
	private ArrayList<File> filesStore = new ArrayList<>();
	private ObjectMapper mapper;
	
	//--------------------------------------------------------------------------

	public ZoneFileListModel(Class<? extends ZariaObjectDefinition> zoneClass, FilenameFilter filter, boolean showSize)
	{
		super(filter, showSize);
		this.zoneClass = zoneClass;
		
		this.mapper = Serializer.createDeserializationMapper();
	}
	
	//--------------------------------------------------------------------------
	
	@Override
	public void setDirectory(File file)
	{
		directory = file;
		
		filesStore.clear();
		File[] file_array = file.listFiles(filter);
		
		if (file_array == null)
		{
			files = null;
		}
		else
		{
			for (File f : file_array)
			{
				try
				{
					if (f.getAbsolutePath().endsWith(DocumentType.ZONE.getSuffix()))
					{
						ZariaObjectDefinition[] defs = mapper.readValue(f, ZariaObjectDefinition[].class);

						for (ZariaObjectDefinition def : defs)
						{
							if (zoneClass.isAssignableFrom(def.getClass()))
							{
								filesStore.add(f);
								break;
							}
						}
					}
				}
				catch (IOException ex)
				{
					DebugConsole.logger.log(Level.SEVERE, null, ex);
				}
			}
			
			files = new File[filesStore.size()];
			filesStore.toArray(files);
		}
		
		if (files != null)
			Arrays.sort(files);
		
		fireContentsChanged(this,0,getSize());
	}
		
	//--------------------------------------------------------------------------
}

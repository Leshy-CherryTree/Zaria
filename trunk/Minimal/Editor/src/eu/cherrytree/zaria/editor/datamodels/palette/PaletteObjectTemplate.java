/****************************************/
/* PaletteObjectTemplate.java           */
/* Created on: 06-May-2013				*/
/* Copyright Cherry Tree Studio 2013	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.editor.datamodels.palette;

import eu.cherrytree.zaria.editor.classlist.ZoneClass;
import eu.cherrytree.zaria.editor.debug.DebugConsole;
import eu.cherrytree.zaria.editor.ReflectionTools;
import eu.cherrytree.zaria.editor.serialization.Serializer;
import eu.cherrytree.zaria.serialization.ZariaObjectDefinition;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;

import java.io.IOException;
import java.util.UUID;
import java.util.logging.Level;

import javax.swing.Icon;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public class PaletteObjectTemplate extends PaletteElement<PaletteObjectTemplate> implements Transferable
{
	//--------------------------------------------------------------------------
	
	public final static DataFlavor graphDataFlavor;
	private final static DataFlavor [] paletteDataFalvors;
	
	static
	{	
		DataFlavor flav = null;
		
		try
		{
			flav = new DataFlavor(DataFlavor.javaSerializedObjectMimeType + "; class=" + PaletteObjectTemplate.class.getCanonicalName());
			
		}
		catch(ClassNotFoundException ex)
		{
			DebugConsole.logger.log(Level.SEVERE, null, ex);
		}								
		
		graphDataFlavor = flav;
		
		paletteDataFalvors = new DataFlavor[2];
		paletteDataFalvors[0] = DataFlavor.stringFlavor;
		paletteDataFalvors[1] = graphDataFlavor;
	}				
	
	//--------------------------------------------------------------------------
	
	private Icon icon;
	private String toolTip;
	
	private Class objectClass;
	
	//--------------------------------------------------------------------------
	
	public PaletteObjectTemplate(ZoneClass zoneClass)
	{
		super(zoneClass.getName());
		
		objectClass = zoneClass.getObjectClass();		
		icon = zoneClass.getIcon();
		toolTip = zoneClass.getToolTip();
	}

	//--------------------------------------------------------------------------

	@Override
	public Icon getIcon()
	{
		return icon;
	}
	
	//--------------------------------------------------------------------------

	@Override
	public String getToolTip()
	{
		return toolTip;
	}
			
	//--------------------------------------------------------------------------
	
	@Override
	public DataFlavor[] getTransferDataFlavors()
	{		
		return paletteDataFalvors;
	}

	//--------------------------------------------------------------------------

	@Override
	public boolean isDataFlavorSupported(DataFlavor df)
	{
		return df.isFlavorTextType();
	}

	//--------------------------------------------------------------------------

	@Override
	public Object getTransferData(DataFlavor df) throws UnsupportedFlavorException, IOException
	{
		if(df == DataFlavor.stringFlavor)
		{
			return Serializer.getText(ReflectionTools.createNewDefinition(objectClass));
		}
		else if(df == graphDataFlavor)
		{
			return ReflectionTools.createNewDefinition(objectClass);
		}
		
		return null;
	}
	
	//--------------------------------------------------------------------------

	@Override
	public int compareTo(PaletteObjectTemplate template)
	{
		return getName().compareTo(template.getName());
	}
	
	//--------------------------------------------------------------------------
	
	@Override
	public boolean isLeaf()
	{
		return true;
	}
	
	//--------------------------------------------------------------------------
}

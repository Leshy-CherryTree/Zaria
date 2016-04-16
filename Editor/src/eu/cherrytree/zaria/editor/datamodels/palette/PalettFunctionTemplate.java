/****************************************/
/* PalettFunctionTemplate.java 			*/
/* Created on: 06-Jul-2014				*/
/* Copyright Cherry Tree Studio 2014	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.editor.datamodels.palette;

import eu.cherrytree.zaria.editor.classlist.ZoneScriptStaticFunction;
import eu.cherrytree.zaria.editor.components.ButtonTabComponent;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;

import java.io.IOException;

import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public class PalettFunctionTemplate extends PaletteElement<PalettFunctionTemplate> implements Transferable
{
	//--------------------------------------------------------------------------
	
	private final static DataFlavor [] paletteDataFalvors = {DataFlavor.stringFlavor};
	private final static Icon icon;
	
	static
	{
		icon = new ImageIcon(ButtonTabComponent.class.getResource("/eu/cherrytree/zaria/editor/res/icons/pallette/function.png"));
	}

	//--------------------------------------------------------------------------
	
	private String template;
	private String toolTip;
	
	//--------------------------------------------------------------------------
	
	public PalettFunctionTemplate(ZoneScriptStaticFunction function)
	{
		super(function.getName());
		
		this.template = function.getTemplate() + "\n";

		toolTip = function.getToolTip();
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
			return template;

		return null;
	}
	
	//--------------------------------------------------------------------------

	@Override
	public int compareTo(PalettFunctionTemplate template)
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

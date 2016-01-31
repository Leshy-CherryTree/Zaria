/********************************************/
/* ZoneGraphElement.java						*/
/* Created on: 16-May-2013					*/
/* Copyright Cherry Tree Studio 2013			*/
/* Released under EUPL v1.1					*/
/********************************************/

package eu.cherrytree.zaria.editor.graph;

import com.mxgraph.model.mxCell;
import com.mxgraph.util.mxUtils;
import eu.cherrytree.zaria.editor.FastMath;
import eu.cherrytree.zaria.editor.Settings;

import eu.cherrytree.zaria.editor.debug.DebugConsole;
import eu.cherrytree.zaria.editor.classlist.ZoneClass;
import eu.cherrytree.zaria.editor.classlist.ZoneClass.InvalidClassHierarchyException;
import eu.cherrytree.zaria.serialization.ColorName;
import eu.cherrytree.zaria.serialization.ZariaObjectDefinition;

import java.awt.Font;
import java.awt.FontMetrics;
import java.util.logging.Level;


/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public abstract class ZoneGraphElement extends mxCell
{
	//--------------------------------------------------------------------------
	
	protected final static FontMetrics nodeMetrics =  mxUtils.getFontMetrics(new Font("Monospaced", Font.PLAIN, Settings.getNodeFontSize()));
	protected final static FontMetrics portMetrics =  mxUtils.getFontMetrics(new Font("Monospaced", Font.PLAIN, Settings.getNodePortFontSize()));
	protected final static FontMetrics externalMetrics =  mxUtils.getFontMetrics(new Font("Monospaced", Font.PLAIN, Settings.getNodeOutsideFontSize()));
	
	//--------------------------------------------------------------------------
	
	public abstract String getToolTip();
	public abstract Class<? extends ZariaObjectDefinition> getDefinitionClass();
	
	//--------------------------------------------------------------------------
	
	protected static String getColorString(Class<? extends ZariaObjectDefinition> definitionClass)
	{
		return getColorString(definitionClass, 0.0f);
	}
	
	//--------------------------------------------------------------------------
	
	protected static String getColorString(Class<? extends ZariaObjectDefinition> definitionClass, float shift)
	{		
		try
		{		
			ColorName c = ZoneClass.getColor(definitionClass);
			
			return "#" +	String.format("%02X", (int) FastMath.clamp(c.getRed()	+ 255 * shift, 0, 255)) + 
							String.format("%02X", (int) FastMath.clamp(c.getGreen() + 255 * shift, 0, 255)) + 
							String.format("%02X", (int) FastMath.clamp(c.getBlue() + 255 * shift, 0, 255));
		}
		catch(InvalidClassHierarchyException ex)
		{
			DebugConsole.logger.log(Level.SEVERE, null, ex);
			return "#000000";
		}
	}
	
	//--------------------------------------------------------------------------
}

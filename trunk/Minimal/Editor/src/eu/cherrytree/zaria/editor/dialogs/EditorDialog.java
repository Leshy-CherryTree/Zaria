/****************************************/
/* EditorDialog.java						*/
/* Created on: 15-07-2016				*/
/* Copyright Cherry Tree Studio 2016	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.editor.dialogs;

import eu.cherrytree.zaria.serialization.ZariaObjectDefinition;

import javax.swing.JDialog;
import javax.swing.JFrame;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public abstract class EditorDialog extends JDialog
{
	//--------------------------------------------------------------------------
	
	protected ZariaObjectDefinition definition;
	
	//--------------------------------------------------------------------------

	public EditorDialog(JFrame frame, ZariaObjectDefinition definition)
	{
		super(frame, true);
		this.definition = definition;
	}		
	
	//--------------------------------------------------------------------------
}

/****************************************/
/* DesktopErrorUI.java					*/
/* Created on: 17-01-2016				*/
/* Copyright Cherry Tree Studio 2016	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.base;

import javax.swing.JOptionPane;



/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public class DesktopErrorUI implements ErrorUI
{
	//--------------------------------------------------------------------------

	@Override
	public void showDialog(String title, String message)
	{
		JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
	}

	//--------------------------------------------------------------------------
}

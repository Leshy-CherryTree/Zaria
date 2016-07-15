/****************************************/
/* PointListModel.java					*/
/* Created on: 14-Jul-2016				*/
/* Copyright Cherry Tree Studio 2016	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.editor.dialogs.curves;

import java.util.ArrayList;
import javax.swing.AbstractListModel;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public class PointListModel extends AbstractListModel
{
	//--------------------------------------------------------------------------
	
	private ArrayList<GraphPanel.Point> points;
	
	//--------------------------------------------------------------------------

	public PointListModel(ArrayList<GraphPanel.Point> points)
	{
		this.points = points;
	}

	//--------------------------------------------------------------------------
	
	@Override
	public int getSize()
	{
		return points.size();
	}
	
	//--------------------------------------------------------------------------

	@Override
	public Object getElementAt(int index)
	{
		return points.get(index);
	}

	//--------------------------------------------------------------------------
	
	public void update()
	{
		fireContentsChanged(this, 0, points.size());
	}
	
	//--------------------------------------------------------------------------
}

/****************************************/
/* ZoneGraphManager.java                */
/* Created on: 19-May-2013				*/
/* Copyright Cherry Tree Studio 2013	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.editor.graph;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public interface ZoneGraphManager
{
	public void addNode(ZoneGraphNode node, int x, int y);
	public void removeNode(ZoneGraphNode node);
	public float getZoomFactor();
	
	public int getScrollX();
	public int getScrollY();
}

/****************************************/
/* PlacableTreeElement.java				*/
/* Created on: 20-Oct-2013				*/
/* Copyright Cherry Tree Studio 2013	*/
/* Released under EUPL v1.1				*/
/****************************************/


package eu.cherrytree.zaria.editor.datamodels;


/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public interface PlacableTreeElement<T extends PlacableTreeElement> extends Comparable<T>
{
	public String getCategory();	
}

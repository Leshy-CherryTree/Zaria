/****************************************/
/* ZoneDocumentListener.java            */
/* Created on: 05-May-2013				*/
/* Copyright Cherry Tree Studio 2013	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.editor.listeners;

import eu.cherrytree.zaria.editor.document.ZoneDocument;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public interface ZoneDocumentListener
{
	public void onDocumentOpened(ZoneDocument document);
	public void onDocumentSavedAs(ZoneDocument document, String oldPath);
	public void onDocumentClosed(ZoneDocument document);
	public void onDocumentSwitched(ZoneDocument document);
}

/****************************************/
/* ZoneGraphKeyboardHandler.java        */
/* Created on: 23-May-2013				*/
/* Copyright Cherry Tree Studio 2013	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.editor.graph;

import com.mxgraph.swing.handler.mxKeyboardHandler;
import com.mxgraph.swing.util.mxGraphActions;
import eu.cherrytree.zaria.editor.graph.actions.CopyAction;
import eu.cherrytree.zaria.editor.graph.actions.CutAction;
import eu.cherrytree.zaria.editor.graph.actions.PasteAction;

import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.UIManager;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public class ZoneGraphKeyboardHandler extends mxKeyboardHandler
{
	//--------------------------------------------------------------------------
	
	private static CutAction cutAction = new CutAction();
	private static CopyAction copyAction = new CopyAction();
	private static PasteAction pasteAction = new PasteAction();
	
	//--------------------------------------------------------------------------

	public ZoneGraphKeyboardHandler(ZoneGraphComponent component)
	{
		super(component);
	}

	//--------------------------------------------------------------------------
	
	@Override
	protected InputMap getInputMap(int condition)
	{
		InputMap map = null;

		if (condition == JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
		{
			map = (InputMap) UIManager.get("ScrollPane.ancestorInputMap");
		}
		else if (condition == JComponent.WHEN_FOCUSED)
		{
			map = new InputMap();

			map.put(KeyStroke.getKeyStroke("DELETE"), "delete");

			map.put(KeyStroke.getKeyStroke("control A"), "selectAll");
			map.put(KeyStroke.getKeyStroke("control D"), "selectNone");
			
			map.put(KeyStroke.getKeyStroke("control X"), "cut");
			map.put(KeyStroke.getKeyStroke("CUT"), "cut");
			map.put(KeyStroke.getKeyStroke("control C"), "copy");
			map.put(KeyStroke.getKeyStroke("COPY"), "copy");
			map.put(KeyStroke.getKeyStroke("control V"), "paste");
			map.put(KeyStroke.getKeyStroke("PASTE"), "paste");
		}

		return map;
	}

	//--------------------------------------------------------------------------

	@Override
	protected ActionMap createActionMap()
	{
		ActionMap map = (ActionMap) UIManager.get("ScrollPane.actionMap");

		map.put("delete", mxGraphActions.getDeleteAction());

		map.put("selectAll", mxGraphActions.getSelectAllAction());
		map.put("selectNone", mxGraphActions.getSelectNoneAction());
				
		map.put("cut", cutAction);
		map.put("copy", copyAction);
		map.put("paste", pasteAction);
		
		return map;
	}
		
	//--------------------------------------------------------------------------
}

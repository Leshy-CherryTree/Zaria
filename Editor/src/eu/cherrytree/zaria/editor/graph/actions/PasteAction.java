/****************************************/
/* PasteAction.java						*/
/* Created on: 25-Jul-2013				*/
/* Copyright Cherry Tree Studio 2013	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.editor.graph.actions;

import eu.cherrytree.zaria.editor.debug.DebugConsole;
import eu.cherrytree.zaria.editor.document.GraphEditorState;
import eu.cherrytree.zaria.editor.graph.ZoneGraph;
import eu.cherrytree.zaria.editor.graph.ZoneGraphNode;
import eu.cherrytree.zaria.editor.ReflectionTools;

import java.awt.event.ActionEvent;
import java.util.UUID;
import java.util.logging.Level;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public class PasteAction extends ZoneGraphAction
{
	//--------------------------------------------------------------------------
	
	private static final long serialVersionUID = 1L;
	
	//--------------------------------------------------------------------------
	
	public static void perform(ZoneGraph graph, GraphEditorState editorState) throws SecurityException, IllegalArgumentException, NoSuchFieldException
	{
		if(graph != null && editorState != null)
		{
			ZoneGraphNode [] nodes = GraphEditorState.getGraphClipboard().getNodes();
						
			if(nodes != null)
			{
				for(ZoneGraphNode node : nodes)
				{
					ReflectionTools.setDefinitionFieldValue(node.getDefinition(), "uuid", UUID.randomUUID());
					node.applyMetadata();
					editorState.addNode(node, (int)node.getGeometry().getX() + 10, (int)node.getGeometry().getY() + 10);
				}
								
				for(ZoneGraphNode node : nodes)
					graph.setLinks(node);
			}
		}
	}
	
	//--------------------------------------------------------------------------

	@Override
	public void actionPerformed(ActionEvent event)
	{
		try
		{
			perform(getGraph(event), getEditorState(event));
		}
		catch (SecurityException | IllegalArgumentException | NoSuchFieldException ex)
		{
			DebugConsole.logger.log(Level.SEVERE, null, ex);
		}
	}

	//--------------------------------------------------------------------------
}

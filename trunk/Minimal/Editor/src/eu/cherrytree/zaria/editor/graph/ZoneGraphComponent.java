/****************************************/
/* ZoneGraphComponent.java              */
/* Created on: 19-May-2013				*/
/* Copyright Cherry Tree Studio 2013	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.editor.graph;

import com.mxgraph.swing.handler.mxCellHandler;
import com.mxgraph.swing.handler.mxEdgeHandler;
import com.mxgraph.swing.handler.mxRubberband;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxCellState;

import eu.cherrytree.zaria.editor.document.GraphEditorState;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import javax.swing.ImageIcon;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public class ZoneGraphComponent extends mxGraphComponent
{
	//--------------------------------------------------------------------------
	
	private static final long serialVersionUID = 1L;
	
	//--------------------------------------------------------------------------
	
	private Color selectionColor = new Color(31, 142, 255);
	private Stroke selectionStroke = new BasicStroke(2.0f);
	
	private mxRubberband rubberband;
	private ZoneGraphKeyboardHandler keyboardHandler;
	
	private GraphEditorState editorState;
	
	//--------------------------------------------------------------------------

	public ZoneGraphComponent(ZoneGraph graph, GraphEditorState editorState)
	{
		super(graph);
		
		this.editorState = editorState;
				
		setAntiAlias(true);
		setGridVisible(true);
		setGridColor(new Color(46, 46, 46));
		setGridStyle(mxGraphComponent.GRID_STYLE_LINE);
		setToolTips(true);
		setPreviewAlpha(0.45f);		
		getViewport().setBackground(new Color(36, 36, 36));		
		getConnectionHandler().getMarker().setHotspot(0.9);
		getConnectionHandler().getMarker().setValidColor(new Color(31, 142, 255));
				
		rubberband = new mxRubberband(this)
		{
			@Override
			public boolean isRubberbandTrigger(MouseEvent me)
			{
				return me.getButton() == 1;
			}			
		};		
		
		keyboardHandler = new ZoneGraphKeyboardHandler(this);		
	}
	
	//--------------------------------------------------------------------------
	
	@Override
	public ImageIcon getFoldingIcon(mxCellState cs)
	{
		return null;
	}
						
	//--------------------------------------------------------------------------

	public GraphEditorState getEditorState()
	{
		return editorState;
	}

	//--------------------------------------------------------------------------
								
	@Override
	public mxCellHandler createHandler(mxCellState state)
	{				
		if(graph.getModel().isEdge(state.getCell()))
		{
			return new mxEdgeHandler(this, state)
			{
				@Override
				public Color getSelectionColor()
				{
					return selectionColor;
				}

				@Override
				public Stroke getSelectionStroke()
				{
					return selectionStroke;
				}
			};
		}

		return null;
	}
	
	//--------------------------------------------------------------------------
}

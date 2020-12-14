/**
 * Copyright (c) 2008-2010, Gaudenz Alder, David Benson
 */
package com.mxgraph.swing.handler;

import java.awt.AlphaComposite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;

import com.mxgraph.canvas.Graphics2DCanvas;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.model.mxICell;
import com.mxgraph.swing.JGraphXComponent;
import com.mxgraph.util.*;
import com.mxgraph.util.JGraphXUtils;
import com.mxgraph.view.JGraphXCellState;
import com.mxgraph.view.JGraphX;

/**
 * Connection handler creates new connections between cells. This control is used to display the connector
 * icon, while the preview is used to draw the line.
 */
public class mxConnectPreview extends mxEventSource
{
	/**
	 * 
	 */
	protected JGraphXComponent graphComponent;

	/**
	 * 
	 */
	protected JGraphXCellState previewState;

	/**
	 * 
	 */
	protected JGraphXCellState sourceState;

	/**
	 * 
	 */
	protected mxPoint startPoint;

	/**
	 * 
	 * @param graphComponent
	 */
	public mxConnectPreview(JGraphXComponent graphComponent)
	{
		this.graphComponent = graphComponent;

		// Installs the paint handler
		graphComponent.addListener(mxEvent.AFTER_PAINT, new mxIEventListener()
		{
			public void invoke(Object sender, mxEventObject evt)
			{
				Graphics g = (Graphics) evt.getProperty("g");
				paint(g);
			}
		});
	}

	/**
	 * Creates a new instance of mxShape for previewing the edge.
	 */
	protected Object createCell(JGraphXCellState startState, String style)
	{
		JGraphX graph = graphComponent.getGraph();
		mxICell cell = ((mxICell) graph
				.createEdge(null, null, "",
						(startState != null) ? startState.getCell() : null,
						null, style));
		((mxICell) startState.getCell()).insertEdge(cell, true);

		return cell;
	}
	
	/**
	 * 
	 */
	public boolean isActive()
	{
		return sourceState != null;
	}

	/**
	 * 
	 */
	public JGraphXCellState getSourceState()
	{
		return sourceState;
	}

	/**
	 * 
	 */
	public JGraphXCellState getPreviewState()
	{
		return previewState;
	}

	/**
	 * 
	 */
	public mxPoint getStartPoint()
	{
		return startPoint;
	}

	/**
	 * Updates the style of the edge preview from the incoming edge
	 */
	public void start(MouseEvent e, JGraphXCellState startState, String style)
	{
		JGraphX graph = graphComponent.getGraph();
		sourceState = startState;
		startPoint = transformScreenPoint(startState.getCenterX(),
				startState.getCenterY());
		Object cell = createCell(startState, style);
		graph.getView().validateCell(cell);
		previewState = graph.getView().getState(cell);
		
		fireEvent(new mxEventObject(mxEvent.START, "event", e, "state",
				previewState));
	}

	/**
	 * 
	 */
	public void update(MouseEvent e, JGraphXCellState targetState, double x, double y)
	{
		JGraphX graph = graphComponent.getGraph();
		mxICell cell = (mxICell) previewState.getCell();

		mxRectangle dirty = graphComponent.getGraph().getPaintBounds(
				new Object[] { previewState.getCell() });

		if (cell.getTerminal(false) != null)
		{
			cell.getTerminal(false).removeEdge(cell, false);
		}

		if (targetState != null)
		{
			((mxICell) targetState.getCell()).insertEdge(cell, false);
		}

		mxGeometry geo = graph.getCellGeometry(previewState.getCell());

		geo.setTerminalPoint(startPoint, true);
		geo.setTerminalPoint(transformScreenPoint(x, y), false);

		revalidate(previewState);
		fireEvent(new mxEventObject(mxEvent.CONTINUE, "event", e, "x", x, "y",
				y));

		// Repaints the dirty region
		// TODO: Cache the new dirty region for next repaint
		Rectangle tmp = getDirtyRect(dirty);

		if (tmp != null)
		{
			graphComponent.getGraphControl().repaint(tmp);
		}
		else
		{
			graphComponent.getGraphControl().repaint();
		}
	}

	/**
	 * 
	 */
	protected Rectangle getDirtyRect()
	{
		return getDirtyRect(null);
	}

	/**
	 * 
	 */
	protected Rectangle getDirtyRect(mxRectangle dirty)
	{
		if (previewState != null)
		{
			mxRectangle tmp = graphComponent.getGraph().getPaintBounds(
					new Object[] { previewState.getCell() });

			if (dirty != null)
			{
				dirty.add(tmp);
			}
			else
			{
				dirty = tmp;
			}

			if (dirty != null)
			{
				// TODO: Take arrow size into account
				dirty.grow(2);

				return dirty.getRectangle();
			}
		}

		return null;
	}

	/**
	 * 
	 */
	protected mxPoint transformScreenPoint(double x, double y)
	{
		JGraphX graph = graphComponent.getGraph();
		mxPoint tr = graph.getView().getTranslate();
		double scale = graph.getView().getScale();

		return new mxPoint(graph.snap(x / scale - tr.getX()), graph.snap(y
				/ scale - tr.getY()));
	}

	/**
	 * 
	 */
	public void revalidate(JGraphXCellState state)
	{
		state.getView().invalidate(state.getCell());
		state.getView().validateCellState(state.getCell());
	}

	/**
	 * 
	 */
	public void paint(Graphics g)
	{
		if (previewState != null)
		{
			Graphics2DCanvas canvas = graphComponent.getCanvas();

			if (graphComponent.isAntiAlias())
			{
				JGraphXUtils.setAntiAlias((Graphics2D) g, true, false);
			}

			float alpha = graphComponent.getPreviewAlpha();

			if (alpha < 1)
			{
				((Graphics2D) g).setComposite(AlphaComposite.getInstance(
						AlphaComposite.SRC_OVER, alpha));
			}

			Graphics2D previousGraphics = canvas.getGraphics();
			mxPoint previousTranslate = canvas.getTranslate();
			double previousScale = canvas.getScale();

			try
			{
				canvas.setScale(graphComponent.getGraph().getView().getScale());
				canvas.setTranslate(0, 0);
				canvas.setGraphics((Graphics2D) g);

				paintPreview(canvas);
			}
			finally
			{
				canvas.setScale(previousScale);
				canvas.setTranslate(previousTranslate.getX(), previousTranslate.getY());
				canvas.setGraphics(previousGraphics);
			}
		}
	}

	/**
	 * Draws the preview using the graphics canvas.
	 */
	protected void paintPreview(Graphics2DCanvas canvas)
	{
		graphComponent.getGraphControl().drawCell(graphComponent.getCanvas(),
				previewState.getCell());
	}

	/**
	 *
	 */
	public Object stop(boolean commit)
	{
		return stop(commit, null);
	}

	/**
	 *
	 */
	public Object stop(boolean commit, MouseEvent e)
	{
		Object result = (sourceState != null) ? sourceState.getCell() : null;

		if (previewState != null)
		{
			JGraphX graph = graphComponent.getGraph();

			graph.getModel().beginUpdate();
			try
			{
				mxICell cell = (mxICell) previewState.getCell();
				Object src = cell.getTerminal(true);
				Object trg = cell.getTerminal(false);

				if (src != null)
				{
					((mxICell) src).removeEdge(cell, true);
				}

				if (trg != null)
				{
					((mxICell) trg).removeEdge(cell, false);
				}

				if (commit)
				{
					result = graph.addCell(cell, null, null, src, trg);
				}

				fireEvent(new mxEventObject(mxEvent.STOP, "event", e, "commit",
						commit, "cell", (commit) ? result : null));

				// Clears the state before the model commits
				if (previewState != null)
				{
					Rectangle dirty = getDirtyRect();
					graph.getView().clear(cell, false, true);
					previewState = null;

					if (!commit && dirty != null)
					{
						graphComponent.getGraphControl().repaint(dirty);
					}
				}
			}
			finally
			{
				graph.getModel().endUpdate();
			}
		}

		sourceState = null;
		startPoint = null;

		return result;
	}

}

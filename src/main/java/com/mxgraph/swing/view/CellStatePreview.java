/**
 * Copyright (c) 2007, Gaudenz Alder
 */
package com.mxgraph.swing.view;

import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.mxgraph.canvas.Graphics2DCanvas;
import com.mxgraph.model.Geometry;
import com.mxgraph.model.IGraphModel;
import com.mxgraph.swing.JGraphXComponent;
import com.mxgraph.util.JGraphXUtils;
import com.mxgraph.util.mxPoint;
import com.mxgraph.util.mxRectangle;
import com.mxgraph.view.JGraphXCellState;
import com.mxgraph.view.JGraphX;

/**
 * Represents the current state of a cell in a given graph view.
 */
public class CellStatePreview
{
	/**
	 * 
	 */
	protected Map<JGraphXCellState, mxPoint> deltas = new LinkedHashMap<JGraphXCellState, mxPoint>();

	/**
	 * 
	 */
	protected int count = 0;

	/**
	 * 
	 */
	protected JGraphXComponent graphComponent;

	/**
	 * Specifies if cell states should be cloned or changed in-place.
	 */
	protected boolean cloned;

	/**
	 * 
	 */
	protected float opacity = 1;

	/**
	 * 
	 */
	protected List<JGraphXCellState> cellStates;

	/**
	 * Constructs a new state preview. The paint handler to invoke the paint
	 * method must be installed elsewhere.
	 */
	public CellStatePreview(JGraphXComponent graphComponent, boolean cloned)
	{
		this.graphComponent = graphComponent;
		this.cloned = cloned;
	}

	/**
	 * 
	 */
	public boolean isCloned()
	{
		return cloned;
	}

	/**
	 * 
	 */
	public void setCloned(boolean value)
	{
		cloned = value;
	}

	/**
	 * 
	 */
	public boolean isEmpty()
	{
		return count == 0;
	}

	/**
	 * 
	 */
	public int getCount()
	{
		return count;
	}

	/**
	 * 
	 */
	public Map<JGraphXCellState, mxPoint> getDeltas()
	{
		return deltas;
	}

	/**
	 * 
	 */
	public void setOpacity(float value)
	{
		opacity = value;
	}

	/**
	 * 
	 */
	public float getOpacity()
	{
		return opacity;
	}

	/**
	 * 
	 */
	public mxPoint moveState(JGraphXCellState state, double dx, double dy)
	{
		return moveState(state, dx, dy, true, true);
	}

	/**
	 * 
	 */
	public mxPoint moveState(JGraphXCellState state, double dx, double dy,
                             boolean add, boolean includeEdges)
	{
		mxPoint delta = deltas.get(state);

		if (delta == null)
		{
			delta = new mxPoint(dx, dy);
			deltas.put(state, delta);
			count++;
		}
		else
		{
			if (add)
			{
				delta.setX(delta.getX() + dx);
				delta.setY(delta.getY() + dy);
			}
			else
			{
				delta.setX(dx);
				delta.setY(dy);
			}
		}

		if (includeEdges)
		{
			addEdges(state);
		}

		return delta;
	}

	/**
	 * Returns a dirty rectangle to be repainted in mxGraphControl.
	 */
	public mxRectangle show()
	{
		JGraphX graph = graphComponent.getGraph();
		IGraphModel model = graph.getModel();

		// Stores a copy of the cell states
		List<JGraphXCellState> previousStates = null;

		if (isCloned())
		{
			previousStates = new LinkedList<JGraphXCellState>();
			Iterator<JGraphXCellState> it = deltas.keySet().iterator();

			while (it.hasNext())
			{
				JGraphXCellState state = it.next();
				previousStates.addAll(snapshot(state));
			}
		}

		// Translates the states in step
		Iterator<JGraphXCellState> it = deltas.keySet().iterator();

		while (it.hasNext())
		{
			JGraphXCellState state = it.next();
			mxPoint delta = deltas.get(state);
			JGraphXCellState parentState = graph.getView().getState(
					model.getParent(state.getCell()));
			translateState(parentState, state, delta.getX(), delta.getY());
		}

		// Revalidates the states in step
		mxRectangle dirty = null;
		it = deltas.keySet().iterator();

		while (it.hasNext())
		{
			JGraphXCellState state = it.next();
			mxPoint delta = deltas.get(state);
			JGraphXCellState parentState = graph.getView().getState(
					model.getParent(state.getCell()));
			mxRectangle tmp = revalidateState(parentState, state, delta.getX(),
					delta.getY());

			if (dirty != null)
			{
				dirty.add(tmp);
			}
			else
			{
				dirty = tmp;
			}
		}

		// Takes a snapshot of the states for later drawing. If the states
		// are not cloned then this does nothing and just expects a repaint
		// of the dirty rectangle.
		if (previousStates != null)
		{
			cellStates = new LinkedList<JGraphXCellState>();
			it = deltas.keySet().iterator();

			while (it.hasNext())
			{
				JGraphXCellState state = it.next();
				cellStates.addAll(snapshot(state));
			}

			// Restores the previous states
			restore(previousStates);
		}

		if (dirty != null)
		{
			dirty.grow(2);
		}

		return dirty;
	}

	/**
	 * 
	 */
	public void restore(List<JGraphXCellState> snapshot)
	{
		JGraphX graph = graphComponent.getGraph();
		Iterator<JGraphXCellState> it = snapshot.iterator();

		while (it.hasNext())
		{
			JGraphXCellState state = it.next();
			JGraphXCellState orig = graph.getView().getState(state.getCell());

			if (orig != null && orig != state)
			{
				restoreState(orig, state);
			}
		}
	}

	/**
	 * 
	 */
	public void restoreState(JGraphXCellState state, JGraphXCellState from)
	{
		state.setLabelBounds(from.getLabelBounds());
		state.setAbsolutePoints(from.getAbsolutePoints());
		state.setOrigin(from.getOrigin());
		state.setAbsoluteOffset(from.getAbsoluteOffset());
		state.setBoundingBox(from.getBoundingBox());
		state.setTerminalDistance(from.getTerminalDistance());
		state.setSegments(from.getSegments());
		state.setLength(from.getLength());
		state.setX(from.getX());
		state.setY(from.getY());
		state.setWidth(from.getWidth());
		state.setHeight(from.getHeight());
	}

	/**
	 * 
	 */
	public List<JGraphXCellState> snapshot(JGraphXCellState state)
	{
		List<JGraphXCellState> result = new LinkedList<JGraphXCellState>();

		if (state != null)
		{
			result.add((JGraphXCellState) state.clone());

			JGraphX graph = graphComponent.getGraph();
			IGraphModel model = graph.getModel();
			Object cell = state.getCell();
			int childCount = model.getChildCount(cell);

			for (int i = 0; i < childCount; i++)
			{
				result.addAll(snapshot(graph.getView().getState(
						model.getChildAt(cell, i))));
			}
		}

		return result;
	}

	/**
	 *
	 */
	protected void translateState(JGraphXCellState parentState, JGraphXCellState state,
                                  double dx, double dy)
	{
		if (state != null)
		{
			JGraphX graph = graphComponent.getGraph();
			IGraphModel model = graph.getModel();
			Object cell = state.getCell();

			if (model.isVertex(cell))
			{
				state.getView().updateCellState(state);
				Geometry geo = graph.getCellGeometry(cell);

				// Moves selection cells and non-relative vertices in
				// the first phase so that edge terminal points will
				// be updated in the second phase
				if ((dx != 0 || dy != 0) && geo != null
						&& (!geo.isRelative() || deltas.get(state) != null))
				{
					state.setX(state.getX() + dx);
					state.setY(state.getY() + dy);
				}
			}

			int childCount = model.getChildCount(cell);

			for (int i = 0; i < childCount; i++)
			{
				translateState(state,
						graph.getView().getState(model.getChildAt(cell, i)),
						dx, dy);
			}
		}
	}

	/**
	 *
	 */
	protected mxRectangle revalidateState(JGraphXCellState parentState,
                                          JGraphXCellState state, double dx, double dy)
	{
		mxRectangle dirty = null;

		if (state != null)
		{
			JGraphX graph = graphComponent.getGraph();
			IGraphModel model = graph.getModel();
			Object cell = state.getCell();
			
			// Updates the edge terminal points and restores the
			// (relative) positions of any (relative) children
			if (model.isEdge(cell))
			{
				state.getView().updateCellState(state);
			}
			
			dirty = state.getView().getBoundingBox(state, false);

			// Moves selection vertices which are relative
			Geometry geo = graph.getCellGeometry(cell);

			if ((dx != 0 || dy != 0)
					&& geo != null
					&& geo.isRelative()
					&& model.isVertex(cell)
					&& (parentState == null
							|| model.isVertex(parentState.getCell()) || deltas
							.get(state) != null))
			{
				state.setX(state.getX() + dx);
				state.setY(state.getY() + dy);

				// TODO: Check this change
				dirty.setX(dirty.getX() + dx);
				dirty.setY(dirty.getY() + dy);

				graph.getView().updateLabelBounds(state);
			}

			int childCount = model.getChildCount(cell);

			for (int i = 0; i < childCount; i++)
			{
				mxRectangle tmp = revalidateState(state, graph.getView()
						.getState(model.getChildAt(cell, i)), dx, dy);

				if (dirty != null)
				{
					dirty.add(tmp);
				}
				else
				{
					dirty = tmp;
				}
			}
		}

		return dirty;
	}

	/**
	 * 
	 */
	public void addEdges(JGraphXCellState state)
	{
		JGraphX graph = graphComponent.getGraph();
		IGraphModel model = graph.getModel();
		Object cell = state.getCell();
		int edgeCount = model.getEdgeCount(cell);

		for (int i = 0; i < edgeCount; i++)
		{
			JGraphXCellState state2 = graph.getView().getState(
					model.getEdgeAt(cell, i));

			if (state2 != null)
			{
				moveState(state2, 0, 0);
			}
		}
	}

	/**
	 * 
	 */
	public void paint(Graphics g)
	{
		if (cellStates != null && cellStates.size() > 0)
		{
			Graphics2DCanvas canvas = graphComponent.getCanvas();

			// Sets antialiasing
			if (graphComponent.isAntiAlias())
			{
				JGraphXUtils.setAntiAlias((Graphics2D) g, true, true);
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
	 * 
	 */
	protected float getOpacityForCell(Object cell)
	{
		return opacity;
	}

	/**
	 * Draws the preview using the graphics canvas.
	 */
	protected void paintPreview(Graphics2DCanvas canvas)
	{
		Composite previousComposite = canvas.getGraphics().getComposite();

		// Paints the preview states
		Iterator<JGraphXCellState> it = cellStates.iterator();

		while (it.hasNext())
		{
			JGraphXCellState state = it.next();
			canvas.getGraphics().setComposite(
					AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
							getOpacityForCell(state.getCell())));
			paintPreviewState(canvas, state);
		}

		canvas.getGraphics().setComposite(previousComposite);
	}

	/**
	 * Draws the preview using the graphics canvas.
	 */
	protected void paintPreviewState(Graphics2DCanvas canvas,
                                     JGraphXCellState state)
	{
		graphComponent.getGraph().drawState(
				canvas,
				state,
				state.getCell() != graphComponent.getCellEditor()
						.getEditingCell());
	}
}

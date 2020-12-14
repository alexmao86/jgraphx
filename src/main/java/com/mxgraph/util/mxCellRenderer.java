/**
 * Copyright (c) 2007-2012, JGraph Ltd
 */
package com.mxgraph.util;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import com.mxgraph.canvas.*;
import com.mxgraph.view.TemporaryCellStates;
import org.w3c.dom.Document;

import com.mxgraph.canvas.JGraphXImageCanvas;
import com.mxgraph.view.JGraphX;
import com.mxgraph.view.GraphView;

public class mxCellRenderer
{
	/**
	 * 
	 */
	private mxCellRenderer()
	{
		// static class
	}

	/**
	 * Draws the given cells using a Graphics2D canvas and returns the buffered image
	 * that represents the cells.
	 * 
	 * @param graph Graph to be painted onto the canvas.
	 * @return Returns the image that represents the canvas.
	 */
	public static IJGraphXCanvas drawCells(JGraphX graph, Object[] cells,
										   double scale, mxRectangle clip, CanvasFactory factory)
	{
		IJGraphXCanvas canvas = null;

		if (cells == null)
		{
			cells = new Object[] { graph.getModel().getRoot() };
		}

		// Gets the current state of the view
		GraphView view = graph.getView();

		// Keeps the existing translation as the cells might
		// be aligned to the grid in a different way in a graph
		// that has a translation other than zero
		boolean eventsEnabled = view.isEventsEnabled();

		// Disables firing of scale events so that there is no
		// repaint or update of the original graph
		view.setEventsEnabled(false);

		// Uses the view to create temporary cell states for each cell
		TemporaryCellStates temp = new TemporaryCellStates(view, scale,
				cells);

		try
		{
			if (clip == null)
			{
				clip = graph.getPaintBounds(cells);
			}

			if (clip != null && clip.getWidth() > 0 && clip.getHeight() > 0)
			{
				Rectangle rect = clip.getRectangle();
				canvas = factory.createCanvas(rect.width + 1, rect.height + 1);

				if (canvas != null)
				{
					double previousScale = canvas.getScale();
					mxPoint previousTranslate = canvas.getTranslate();

					try
					{
						canvas.setTranslate(-rect.x, -rect.y);
						canvas.setScale(view.getScale());

						for (int i = 0; i < cells.length; i++)
						{
							graph.drawCell(canvas, cells[i]);
						}
					}
					finally
					{
						canvas.setScale(previousScale);
						canvas.setTranslate(previousTranslate.getX(),
								previousTranslate.getY());
					}
				}
			}
		}
		finally
		{
			temp.destroy();
			view.setEventsEnabled(eventsEnabled);
		}

		return canvas;
	}

	/**
	 * 
	 */
	public static BufferedImage createBufferedImage(JGraphX graph,
			Object[] cells, double scale, Color background, boolean antiAlias,
			mxRectangle clip)
	{
		return createBufferedImage(graph, cells, scale, background, antiAlias,
				clip, new Graphics2DCanvas());
	}

	/**
	 * 
	 */
	public static BufferedImage createBufferedImage(JGraphX graph,
			Object[] cells, double scale, final Color background,
			final boolean antiAlias, mxRectangle clip,
			final Graphics2DCanvas graphicsCanvas)
	{
		JGraphXImageCanvas canvas = (JGraphXImageCanvas) drawCells(graph, cells, scale,
				clip, new CanvasFactory()
				{
					public IJGraphXCanvas createCanvas(int width, int height)
					{
						return new JGraphXImageCanvas(graphicsCanvas, width, height,
								background, antiAlias);
					}

				});

		return (canvas != null) ? canvas.destroy() : null;
	}

	/**
	 * 
	 */
	public static Document createHtmlDocument(JGraphX graph, Object[] cells,
			double scale, Color background, mxRectangle clip)
	{
		HtmlCanvas canvas = (HtmlCanvas) drawCells(graph, cells, scale,
				clip, new CanvasFactory()
				{
					public IJGraphXCanvas createCanvas(int width, int height)
					{
						return new HtmlCanvas(mxDomUtils.createHtmlDocument());
					}

				});

		return (canvas != null) ? canvas.getDocument() : null;
	}

	/**
	 * 
	 */
	public static Document createSvgDocument(JGraphX graph, Object[] cells,
			double scale, Color background, mxRectangle clip)
	{
		SvgCanvas canvas = (SvgCanvas) drawCells(graph, cells, scale, clip,
				new CanvasFactory()
				{
					public IJGraphXCanvas createCanvas(int width, int height)
					{
						return new SvgCanvas(mxDomUtils.createSvgDocument(width,
								height));
					}

				});

		return (canvas != null) ? canvas.getDocument() : null;
	}

	/**
	 * 
	 */
	public static Document createVmlDocument(JGraphX graph, Object[] cells,
			double scale, Color background, mxRectangle clip)
	{
		VmlCanvas canvas = (VmlCanvas) drawCells(graph, cells, scale, clip,
				new CanvasFactory()
				{
					public IJGraphXCanvas createCanvas(int width, int height)
					{
						return new VmlCanvas(mxDomUtils.createVmlDocument());
					}

				});

		return (canvas != null) ? canvas.getDocument() : null;
	}

	/**
	 * 
	 */
	public static abstract class CanvasFactory
	{

		/**
		 * Separates the creation of the canvas from its initialization, when the
		 * size of the required graphics buffer / document / container is known.
		 */
		public abstract IJGraphXCanvas createCanvas(int width, int height);

	}

}

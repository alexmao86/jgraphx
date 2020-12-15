/**
 * Copyright (c) 2010, Gaudenz Alder, David Benson
 */
package com.mxgraph.shape;

import java.awt.Color;
import java.awt.Paint;
import java.awt.Shape;
import java.util.Map;

import com.mxgraph.canvas.Graphics2DCanvas;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxRectangle;
import com.mxgraph.util.JGraphXUtils;
import com.mxgraph.view.JGraphXCellState;

public class BasicShape implements IShape
{

	/**
	 * 
	 */
	public void paintShape(Graphics2DCanvas canvas, JGraphXCellState state)
	{
		Shape shape = createShape(canvas, state);

		if (shape != null)
		{
			// Paints the background
			if (configureGraphics(canvas, state, true))
			{
				canvas.fillShape(shape, hasShadow(canvas, state));
			}

			// Paints the foreground
			if (configureGraphics(canvas, state, false))
			{
				canvas.getGraphics().draw(shape);
			}
		}
	}

	/**
	 * 
	 */
	public Shape createShape(Graphics2DCanvas canvas, JGraphXCellState state)
	{
		return null;
	}

	/**
	 * Configures the graphics object ready to paint.
	 * @param canvas the canvas to be painted to
	 * @param state the state of cell to be painted
	 * @param background whether or not this is the background stage of 
	 * 			the shape paint
	 * @return whether or not the shape is ready to be drawn
	 */
	protected boolean configureGraphics(Graphics2DCanvas canvas,
                                        JGraphXCellState state, boolean background)
	{
		Map<String, Object> style = state.getStyle();

		if (background)
		{
			// Paints the background of the shape
			Paint fillPaint = hasGradient(canvas, state) ? canvas
					.createFillPaint(getGradientBounds(canvas, state), style)
					: null;

			if (fillPaint != null)
			{
				canvas.getGraphics().setPaint(fillPaint);

				return true;
			}
			else
			{
				Color color = getFillColor(canvas, state);
				canvas.getGraphics().setColor(color);

				return color != null;
			}
		}
		else
		{
			canvas.getGraphics().setPaint(null);
			Color color = getStrokeColor(canvas, state);
			canvas.getGraphics().setColor(color);
			canvas.getGraphics().setStroke(canvas.createStroke(style));

			return color != null;
		}
	}

	/**
	 * 
	 */
	protected mxRectangle getGradientBounds(Graphics2DCanvas canvas,
                                            JGraphXCellState state)
	{
		return state;
	}

	/**
	 * 
	 */
	public boolean hasGradient(Graphics2DCanvas canvas, JGraphXCellState state)
	{
		return true;
	}

	/**
	 * 
	 */
	public boolean hasShadow(Graphics2DCanvas canvas, JGraphXCellState state)
	{
		return JGraphXUtils
				.isTrue(state.getStyle(), mxConstants.STYLE_SHADOW, false);
	}

	/**
	 * 
	 */
	public Color getFillColor(Graphics2DCanvas canvas, JGraphXCellState state)
	{
		return JGraphXUtils.getColor(state.getStyle(), mxConstants.STYLE_FILLCOLOR);
	}

	/**
	 * 
	 */
	public Color getStrokeColor(Graphics2DCanvas canvas, JGraphXCellState state)
	{
		return JGraphXUtils
				.getColor(state.getStyle(), mxConstants.STYLE_STROKECOLOR);
	}

}

package com.mxgraph.shape;

import com.mxgraph.canvas.Graphics2DCanvas;
import com.mxgraph.canvas.GraphicsCanvas2D;
import com.mxgraph.util.JGraphXUtils;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxRectangle;
import com.mxgraph.view.JGraphXCellState;

public class mxSwimlaneShape extends mxBasicShape
{

	/**
	 * Returns the bounding box for the gradient box for this shape.
	 */
	protected double getTitleSize(Graphics2DCanvas canvas, JGraphXCellState state)
	{
		return Math.max(
				0,
				JGraphXUtils.getFloat(state.getStyle(), mxConstants.STYLE_STARTSIZE,
						mxConstants.DEFAULT_STARTSIZE) * canvas.getScale());
	};

	/**
	 * 
	 */
	protected mxRectangle getGradientBounds(Graphics2DCanvas canvas,
                                            JGraphXCellState state)
	{
		double start = getTitleSize(canvas, state);

		if (JGraphXUtils
				.isTrue(state.getStyle(), mxConstants.STYLE_HORIZONTAL, true))
		{
			start = Math.min(start, state.getHeight());

			return new mxRectangle(state.getX(), state.getY(),
					state.getWidth(), start);
		}
		else
		{
			start = Math.min(start, state.getWidth());

			return new mxRectangle(state.getX(), state.getY(), start,
					state.getHeight());
		}
	}

	/**
	 * 
	 */
	public void paintShape(Graphics2DCanvas canvas, JGraphXCellState state)
	{
		double start = getTitleSize(canvas, state);
		String fill = JGraphXUtils.getString(state.getStyle(),
				mxConstants.STYLE_SWIMLANE_FILLCOLOR, mxConstants.NONE);
		boolean swimlaneLine = JGraphXUtils.isTrue(state.getStyle(),
				mxConstants.STYLE_SWIMLANE_LINE, true);
		double r = 0;

		if (JGraphXUtils
				.isTrue(state.getStyle(), mxConstants.STYLE_HORIZONTAL, true))
		{
			start = Math.min(start, state.getHeight());
		}
		else
		{
			start = Math.min(start, state.getWidth());
		}

		canvas.getGraphics().translate(state.getX(), state.getY());

		if (!JGraphXUtils.isTrue(state.getStyle(), mxConstants.STYLE_ROUNDED))
		{
			paintSwimlane(canvas, state, start, fill, swimlaneLine);
		}
		else
		{
			r = getArcSize(state, start);
			paintRoundedSwimlane(canvas, state, start, r, fill, swimlaneLine);
		}
		
		String sep = JGraphXUtils.getString(state.getStyle(), mxConstants.STYLE_SEPARATORCOLOR, mxConstants.NONE);
		paintSeparator(canvas, state, start, sep);
	}

	/**
	 * Helper method to configure the given wrapper canvas.
	 */
	protected double getArcSize(JGraphXCellState state, double start)
	{
		double f = JGraphXUtils.getDouble(state.getStyle(),
				mxConstants.STYLE_ARCSIZE,
				mxConstants.RECTANGLE_ROUNDING_FACTOR * 100) / 100;

		return start * f * 3;
	}

	/**
	 * Helper method to configure the given wrapper canvas.
	 */
	protected GraphicsCanvas2D configureCanvas(Graphics2DCanvas canvas,
											   JGraphXCellState state, GraphicsCanvas2D c)
	{
		c.setShadow(hasShadow(canvas, state));
		c.setStrokeColor(JGraphXUtils.getString(state.getStyle(),
				mxConstants.STYLE_STROKECOLOR, mxConstants.NONE));
		c.setStrokeWidth(JGraphXUtils.getInt(state.getStyle(),
				mxConstants.STYLE_STROKEWIDTH, 1));
		c.setDashed(JGraphXUtils.isTrue(state.getStyle(), mxConstants.STYLE_DASHED,
				false));

		String fill = JGraphXUtils.getString(state.getStyle(),
				mxConstants.STYLE_FILLCOLOR, mxConstants.NONE);
		String gradient = JGraphXUtils.getString(state.getStyle(),
				mxConstants.STYLE_GRADIENTCOLOR, mxConstants.NONE);

		if (!mxConstants.NONE.equals(fill)
				&& !mxConstants.NONE.equals(gradient))
		{
			mxRectangle b = getGradientBounds(canvas, state);
			c.setGradient(fill, gradient, b.getX(), b.getY(), b.getWidth(), b
					.getHeight(), JGraphXUtils.getString(state.getStyle(),
					mxConstants.STYLE_GRADIENT_DIRECTION,
					mxConstants.DIRECTION_NORTH), 1, 1);
		}
		else
		{
			c.setFillColor(fill);
		}

		return c;
	}

	/**
	 * 
	 */
	protected void paintSwimlane(Graphics2DCanvas canvas, JGraphXCellState state,
                                 double start, String fill, boolean swimlaneLine)
	{
		GraphicsCanvas2D c = configureCanvas(canvas, state,
				new GraphicsCanvas2D(canvas.getGraphics()));
		double w = state.getWidth();
		double h = state.getHeight();

		if (!mxConstants.NONE.equals(fill))
		{
			c.save();
			c.setFillColor(fill);
			c.rect(0, 0, w, h);
			c.fillAndStroke();
			c.restore();
			c.setShadow(false);
		}

		c.begin();

		if (JGraphXUtils
				.isTrue(state.getStyle(), mxConstants.STYLE_HORIZONTAL, true))
		{
			c.moveTo(0, start);
			c.lineTo(0, 0);
			c.lineTo(w, 0);
			c.lineTo(w, start);

			if (swimlaneLine || start >= h)
			{
				c.close();
			}

			c.fillAndStroke();

			// Transparent content area
			if (start < h && mxConstants.NONE.equals(fill))
			{
				c.begin();
				c.moveTo(0, start);
				c.lineTo(0, h);
				c.lineTo(w, h);
				c.lineTo(w, start);
				c.stroke();
			}
		}
		else
		{
			c.moveTo(start, 0);
			c.lineTo(0, 0);
			c.lineTo(0, h);
			c.lineTo(start, h);

			if (swimlaneLine || start >= w)
			{
				c.close();
			}

			c.fillAndStroke();

			// Transparent content area
			if (start < w && mxConstants.NONE.equals(fill))
			{
				c.begin();
				c.moveTo(start, 0);
				c.lineTo(w, 0);
				c.lineTo(w, h);
				c.lineTo(start, h);
				c.stroke();
			}
		}
	};

	/**
	 * Function: paintRoundedSwimlane
	 *
	 * Paints the swimlane vertex shape.
	 */
	protected void paintRoundedSwimlane(Graphics2DCanvas canvas,
                                        JGraphXCellState state, double start, double r, String fill,
                                        boolean swimlaneLine)
	{
		GraphicsCanvas2D c = configureCanvas(canvas, state,
				new GraphicsCanvas2D(canvas.getGraphics()));
		double w = state.getWidth();
		double h = state.getHeight();

		if (!mxConstants.NONE.equals(fill))
		{
			c.save();
			c.setFillColor(fill);
			c.roundrect(0, 0, w, h, r, r);
			c.fillAndStroke();
			c.restore();
			c.setShadow(false);
		}

		c.begin();

		if (JGraphXUtils
				.isTrue(state.getStyle(), mxConstants.STYLE_HORIZONTAL, true))
		{
			c.moveTo(w, start);
			c.lineTo(w, r);
			c.quadTo(w, 0, w - Math.min(w / 2, r), 0);
			c.lineTo(Math.min(w / 2, r), 0);
			c.quadTo(0, 0, 0, r);
			c.lineTo(0, start);

			if (swimlaneLine || start >= h)
			{
				c.close();
			}

			c.fillAndStroke();

			// Transparent content area
			if (start < h && mxConstants.NONE.equals(fill))
			{
				c.begin();
				c.moveTo(0, start);
				c.lineTo(0, h - r);
				c.quadTo(0, h, Math.min(w / 2, r), h);
				c.lineTo(w - Math.min(w / 2, r), h);
				c.quadTo(w, h, w, h - r);
				c.lineTo(w, start);
				c.stroke();
			}
		}
		else
		{
			c.moveTo(start, 0);
			c.lineTo(r, 0);
			c.quadTo(0, 0, 0, Math.min(h / 2, r));
			c.lineTo(0, h - Math.min(h / 2, r));
			c.quadTo(0, h, r, h);
			c.lineTo(start, h);

			if (swimlaneLine || start >= w)
			{
				c.close();
			}

			c.fillAndStroke();

			// Transparent content area
			if (start < w && mxConstants.NONE.equals(fill))
			{
				c.begin();
				c.moveTo(start, h);
				c.lineTo(w - r, h);
				c.quadTo(w, h, w, h - Math.min(h / 2, r));
				c.lineTo(w, Math.min(h / 2, r));
				c.quadTo(w, 0, w - r, 0);
				c.lineTo(start, 0);
				c.stroke();
			}
		}
	};

	/**
	 * Function: paintSwimlane
	 *
	 * Paints the swimlane vertex shape.
	 */
	protected void paintSeparator(Graphics2DCanvas canvas, JGraphXCellState state,
                                  double start, String color)
	{
		GraphicsCanvas2D c = new GraphicsCanvas2D(canvas.getGraphics());
		double w = state.getWidth();
		double h = state.getHeight();

		if (!mxConstants.NONE.equals(color))
		{
			c.setStrokeColor(color);
			c.setDashed(true);
			c.begin();

			if (JGraphXUtils.isTrue(state.getStyle(), mxConstants.STYLE_HORIZONTAL,
					true))
			{
				c.moveTo(w, start);
				c.lineTo(w, h);
			}
			else
			{
				c.moveTo(start, 0);
				c.lineTo(w, 0);
			}

			c.stroke();
			c.setDashed(false);
		}
	};

}

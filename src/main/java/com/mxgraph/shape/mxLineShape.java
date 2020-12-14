package com.mxgraph.shape;

import com.mxgraph.canvas.Graphics2DCanvas;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxPoint;
import com.mxgraph.util.JGraphXUtils;
import com.mxgraph.view.JGraphXCellState;

public class mxLineShape extends mxBasicShape
{

	/**
	 * 
	 */
	public void paintShape(Graphics2DCanvas canvas, JGraphXCellState state)
	{
		if (configureGraphics(canvas, state, false))
		{
			boolean rounded = JGraphXUtils.isTrue(state.getStyle(),
					mxConstants.STYLE_ROUNDED, false)
					&& canvas.getScale() > mxConstants.MIN_SCALE_FOR_ROUNDED_LINES;

			canvas.paintPolyline(createPoints(canvas, state), rounded);
		}
	}

	/**
	 * 
	 */
	public mxPoint[] createPoints(Graphics2DCanvas canvas, JGraphXCellState state)
	{
		String direction = JGraphXUtils.getString(state.getStyle(),
				mxConstants.STYLE_DIRECTION, mxConstants.DIRECTION_EAST);

		mxPoint p0, pe;

		if (direction.equals(mxConstants.DIRECTION_EAST)
				|| direction.equals(mxConstants.DIRECTION_WEST))
		{
			double mid = state.getCenterY();
			p0 = new mxPoint(state.getX(), mid);
			pe = new mxPoint(state.getX() + state.getWidth(), mid);
		}
		else
		{
			double mid = state.getCenterX();
			p0 = new mxPoint(mid, state.getY());
			pe = new mxPoint(mid, state.getY() + state.getHeight());
		}

		mxPoint[] points = new mxPoint[2];
		points[0] = p0;
		points[1] = pe;

		return points;
	}

}

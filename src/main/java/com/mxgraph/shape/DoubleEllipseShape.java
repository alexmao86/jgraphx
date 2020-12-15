package com.mxgraph.shape;

import java.awt.Rectangle;

import com.mxgraph.canvas.Graphics2DCanvas;
import com.mxgraph.util.JGraphXUtils;
import com.mxgraph.util.mxConstants;
import com.mxgraph.view.JGraphXCellState;

public class DoubleEllipseShape extends EllipseShape
{

	/**
	 * 
	 */
	public void paintShape(Graphics2DCanvas canvas, JGraphXCellState state)
	{
		super.paintShape(canvas, state);

		int inset = (int) Math.round((JGraphXUtils.getFloat(state.getStyle(),
				mxConstants.STYLE_STROKEWIDTH, 1) + 3)
				* canvas.getScale());

		Rectangle rect = state.getRectangle();
		int x = rect.x + inset;
		int y = rect.y + inset;
		int w = rect.width - 2 * inset;
		int h = rect.height - 2 * inset;
		
		canvas.getGraphics().drawOval(x, y, w, h);
	}

}

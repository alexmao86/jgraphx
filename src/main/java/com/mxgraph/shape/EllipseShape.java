package com.mxgraph.shape;

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;

import com.mxgraph.canvas.Graphics2DCanvas;
import com.mxgraph.view.JGraphXCellState;

public class EllipseShape extends BasicShape
{

	/**
	 * 
	 */
	public Shape createShape(Graphics2DCanvas canvas, JGraphXCellState state)
	{
		Rectangle temp = state.getRectangle();

		return new Ellipse2D.Float(temp.x, temp.y, temp.width, temp.height);
	}

}

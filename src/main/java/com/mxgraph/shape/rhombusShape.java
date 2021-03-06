package com.mxgraph.shape;

import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Shape;

import com.mxgraph.canvas.Graphics2DCanvas;
import com.mxgraph.view.JGraphXCellState;

public class rhombusShape extends BasicShape
{

	/**
	 * 
	 */
	public Shape createShape(Graphics2DCanvas canvas, JGraphXCellState state)
	{
		Rectangle temp = state.getRectangle();
		int x = temp.x;
		int y = temp.y;
		int w = temp.width;
		int h = temp.height;
		int halfWidth = w / 2;
		int halfHeight = h / 2;

		Polygon rhombus = new Polygon();
		rhombus.addPoint(x + halfWidth, y);
		rhombus.addPoint(x + w, y + halfHeight);
		rhombus.addPoint(x + halfWidth, y + h);
		rhombus.addPoint(x, y + halfHeight);

		return rhombus;
	}

}

package com.mxgraph.shape;

import com.mxgraph.canvas.Graphics2DCanvas;
import com.mxgraph.view.JGraphXCellState;

public interface IShape
{
	/**
	 * 
	 */
	void paintShape(Graphics2DCanvas canvas, JGraphXCellState state);

}

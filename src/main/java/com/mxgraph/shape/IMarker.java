package com.mxgraph.shape;

import com.mxgraph.canvas.Graphics2DCanvas;
import com.mxgraph.util.mxPoint;
import com.mxgraph.view.JGraphXCellState;

public interface IMarker
{
	/**
	 * 
	 */
	mxPoint paintMarker(Graphics2DCanvas canvas, JGraphXCellState state, String type,
                        mxPoint pe, double nx, double ny, double size, boolean source);

}

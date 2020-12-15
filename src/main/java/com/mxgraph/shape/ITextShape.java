/**
 * Copyright (c) 2010, Gaudenz Alder, David Benson
 */
package com.mxgraph.shape;

import java.util.Map;

import com.mxgraph.canvas.Graphics2DCanvas;
import com.mxgraph.view.JGraphXCellState;

public interface ITextShape
{
	/**
	 * 
	 */
	void paintShape(Graphics2DCanvas canvas, String text, JGraphXCellState state,
                    Map<String, Object> style);

}

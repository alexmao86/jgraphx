package com.mxgraph.swing.util;

import com.mxgraph.util.mxRectangle;
import com.mxgraph.view.JGraphXCellState;

public interface ICellOverlay
{

	/**
	 * 
	 */
	mxRectangle getBounds(JGraphXCellState state);

}

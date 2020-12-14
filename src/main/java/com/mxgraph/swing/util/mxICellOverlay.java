package com.mxgraph.swing.util;

import com.mxgraph.util.mxRectangle;
import com.mxgraph.view.JGraphXCellState;

public interface mxICellOverlay
{

	/**
	 * 
	 */
	mxRectangle getBounds(JGraphXCellState state);

}

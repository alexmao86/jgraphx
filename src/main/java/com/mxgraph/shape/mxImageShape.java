/**
 * Copyright (c) 2007-2010, Gaudenz Alder, David Benson
 */
package com.mxgraph.shape;

import java.awt.Color;
import java.awt.Rectangle;

import com.mxgraph.canvas.Graphics2DCanvas;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.JGraphXUtils;
import com.mxgraph.view.JGraphXCellState;

/**
 * A rectangular shape that contains a single image. See mxImageBundle for
 * creating a lookup table with images which can then be referenced by key.
 */
public class mxImageShape extends mxRectangleShape
{

	/**
	 * 
	 */
	public void paintShape(Graphics2DCanvas canvas, JGraphXCellState state)
	{
		super.paintShape(canvas, state);

		boolean flipH = JGraphXUtils.isTrue(state.getStyle(),
				mxConstants.STYLE_IMAGE_FLIPH, false);
		boolean flipV = JGraphXUtils.isTrue(state.getStyle(),
				mxConstants.STYLE_IMAGE_FLIPV, false);

		canvas.drawImage(getImageBounds(canvas, state),
				getImageForStyle(canvas, state),
				Graphics2DCanvas.PRESERVE_IMAGE_ASPECT, flipH, flipV);
	}

	/**
	 * 
	 */
	public Rectangle getImageBounds(Graphics2DCanvas canvas, JGraphXCellState state)
	{
		return state.getRectangle();
	}

	/**
	 * 
	 */
	public boolean hasGradient(Graphics2DCanvas canvas, JGraphXCellState state)
	{
		return false;
	}

	/**
	 * 
	 */
	public String getImageForStyle(Graphics2DCanvas canvas, JGraphXCellState state)
	{
		return canvas.getImageForStyle(state.getStyle());
	}

	/**
	 * 
	 */
	public Color getFillColor(Graphics2DCanvas canvas, JGraphXCellState state)
	{
		return JGraphXUtils.getColor(state.getStyle(),
				mxConstants.STYLE_IMAGE_BACKGROUND);
	}

	/**
	 * 
	 */
	public Color getStrokeColor(Graphics2DCanvas canvas, JGraphXCellState state)
	{
		return JGraphXUtils.getColor(state.getStyle(),
				mxConstants.STYLE_IMAGE_BORDER);
	}

}

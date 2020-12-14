/**
 * Copyright (c) 2007-2017, Gaudenz Alder
 * Copyright (c) 2007-2017, JGraph Ltd
 */
package com.mxgraph.analysis;

import com.mxgraph.view.JGraphXCellState;

/**
 * Implements a cost function for a constant cost per traversed cell.
 */
public class ConstantCostFunction implements ICostFunction
{

	/**
	 * 
	 */
	protected double cost = 0;

	/**
	 * 
	 * @param cost the cost value for this function
	 */
	public ConstantCostFunction(double cost)
	{
		this.cost = cost;
	}

	/**
	 *
	 */
	public double getCost(JGraphXCellState state)
	{
		return cost;
	}

}

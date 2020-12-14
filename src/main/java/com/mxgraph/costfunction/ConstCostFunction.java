package com.mxgraph.costfunction;

import com.mxgraph.view.JGraphXCellState;

/**
 * @author Mate
 * A constant cost function that returns <b>const</b> regardless of edge value
 */
public class ConstCostFunction extends CostFunction
{
	private double cost;
	
	public ConstCostFunction(double cost)
	{
		this.cost = cost;
	};
	
	public double getCost(JGraphXCellState state)
	{
		return cost;
	};
}

package com.mxgraph.generatorfunction;

import com.mxgraph.view.JGraphXCellState;

/**
 * @author Mate
 * A generator random cost function
 * It will generate random (type "double") edge weights in the range of (<b>minWeight</b>, <b>maxWeight</b>) and rounds the values to <b>roundToDecimals</b>
 */
public class GeneratorRandomFunction extends GeneratorFunction
{
	private double maxWeight = 1;

	private double minWeight = 0;

	private int roundToDecimals = 2;

	public GeneratorRandomFunction(double minWeight, double maxWeight, int roundToDecimals)
	{
		setWeightRange(minWeight, maxWeight);
		setRoundToDecimals(roundToDecimals);
	};

	public double getCost(JGraphXCellState state)
	{
		Double edgeWeight = null;

		edgeWeight = Math.random() * (maxWeight - minWeight) + minWeight;
		edgeWeight = (double) Math.round(edgeWeight * Math.pow(10, getRoundToDecimals())) / Math.pow(10, getRoundToDecimals());

		return edgeWeight;
	};

	public double getMaxWeight()
	{
		return maxWeight;
	};

	public void setWeightRange(double minWeight, double maxWeight)
	{
		this.maxWeight = Math.max(minWeight, maxWeight);
		this.minWeight = Math.min(minWeight, maxWeight);
	};

	public double getMinWeight()
	{
		return minWeight;
	};

	public int getRoundToDecimals()
	{
		return roundToDecimals;
	};

	public void setRoundToDecimals(int roundToDecimals)
	{
		this.roundToDecimals = roundToDecimals;
	};
};

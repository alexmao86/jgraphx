/*
 * Copyright (c) 2005, David Benson
 *
 * All rights reserved.
 *
 * This file is licensed under the JGraph software license, a copy of which
 * will have been provided to you in the file LICENSE at the root of your
 * installation directory. If you are unable to locate this file please
 * contact JGraph sales for another copy.
 */
package com.mxgraph.layout.hierarchical.stage;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.mxgraph.layout.hierarchical.HierarchicalLayout;
import com.mxgraph.layout.hierarchical.model.GraphHierarchyEdge;
import com.mxgraph.layout.hierarchical.model.GraphHierarchyModel;
import com.mxgraph.layout.hierarchical.model.GraphHierarchyNode;
import com.mxgraph.view.JGraphX;

/**
 * An implementation of the first stage of the Sugiyama layout. Straightforward
 * longest path calculation of layer assignment
 */
public class MinimumCycleRemover implements HierarchicalLayoutStage
{

	/**
	 * Reference to the enclosing layout algorithm
	 */
	protected HierarchicalLayout layout;

	/**
	 * Constructor that has the roots specified
	 */
	public MinimumCycleRemover(HierarchicalLayout layout)
	{
		this.layout = layout;
	}

	/**
	 * Produces the layer assignmment using the graph information specified
	 */
	public void execute(Object parent)
	{
		GraphHierarchyModel model = layout.getModel();
		final Set<GraphHierarchyNode> seenNodes = new HashSet<GraphHierarchyNode>();
		final Set<GraphHierarchyNode> unseenNodes = new HashSet<GraphHierarchyNode>(
				model.getVertexMapper().values());

		// Perform a dfs through the internal model. If a cycle is found,
		// reverse it.
		GraphHierarchyNode[] rootsArray = null;

		if (model.roots != null)
		{
			Object[] modelRoots = model.roots.toArray();
			rootsArray = new GraphHierarchyNode[modelRoots.length];

			for (int i = 0; i < modelRoots.length; i++)
			{
				Object node = modelRoots[i];
				GraphHierarchyNode internalNode = model
						.getVertexMapper().get(node);
				rootsArray[i] = internalNode;
			}
		}

		model.visit(new GraphHierarchyModel.CellVisitor()
		{
			public void visit(GraphHierarchyNode parent,
							  GraphHierarchyNode cell,
							  GraphHierarchyEdge connectingEdge, int layer, int seen)
			{
				// Check if the cell is in it's own ancestor list, if so
				// invert the connecting edge and reverse the target/source
				// relationship to that edge in the parent and the cell
				if ((cell)
						.isAncestor(parent))
				{
					connectingEdge.invert();
					parent.connectsAsSource.remove(connectingEdge);
					parent.connectsAsTarget.add(connectingEdge);
					cell.connectsAsTarget.remove(connectingEdge);
					cell.connectsAsSource.add(connectingEdge);
				}
				seenNodes.add(cell);
				unseenNodes.remove(cell);
			}
		}, rootsArray, true, null);

		Set<Object> possibleNewRoots = null;

		if (unseenNodes.size() > 0)
		{
			possibleNewRoots = new HashSet<Object>(unseenNodes);
		}

		// If there are any nodes that should be nodes that the dfs can miss
		// these need to be processed with the dfs and the roots assigned
		// correctly to form a correct internal model
		Set<GraphHierarchyNode> seenNodesCopy = new HashSet<GraphHierarchyNode>(
				seenNodes);

		// Pick a random cell and dfs from it
		GraphHierarchyNode[] unseenNodesArray = new GraphHierarchyNode[1];
		unseenNodes.toArray(unseenNodesArray);
		
		model.visit(new GraphHierarchyModel.CellVisitor()
		{
			public void visit(GraphHierarchyNode parent,
							  GraphHierarchyNode cell,
							  GraphHierarchyEdge connectingEdge, int layer, int seen)
			{
				// Check if the cell is in it's own ancestor list, if so
				// invert the connecting edge and reverse the target/source
				// relationship to that edge in the parent and the cell
				if ((cell)
						.isAncestor(parent))
				{
					connectingEdge.invert();
					parent.connectsAsSource.remove(connectingEdge);
					parent.connectsAsTarget.add(connectingEdge);
					cell.connectsAsTarget.remove(connectingEdge);
					cell.connectsAsSource.add(connectingEdge);
				}
				seenNodes.add(cell);
				unseenNodes.remove(cell);
			}
		}, unseenNodesArray, true, seenNodesCopy);

		JGraphX graph = layout.getGraph();

		if (possibleNewRoots != null && possibleNewRoots.size() > 0)
		{
			Iterator<Object> iter = possibleNewRoots.iterator();
			List<Object> roots = model.roots;

			while (iter.hasNext())
			{
				GraphHierarchyNode node = (GraphHierarchyNode) iter.next();
				Object realNode = node.cell;
				int numIncomingEdges = graph.getIncomingEdges(realNode).length;

				if (numIncomingEdges == 0)
				{
					roots.add(realNode);
				}
			}
		}
	}
}

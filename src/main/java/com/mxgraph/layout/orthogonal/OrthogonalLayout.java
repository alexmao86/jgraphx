/**
 * Copyright (c) 2008-2009, JGraph Ltd
 */
package com.mxgraph.layout.orthogonal;

import com.mxgraph.layout.GraphLayout;
import com.mxgraph.layout.orthogonal.model.OrthogonalModel;
import com.mxgraph.view.JGraphX;

/**
 *
 */
/**
*
*/
public class OrthogonalLayout extends GraphLayout
{

  /**
   * 
   */
  protected OrthogonalModel orthModel;

  /**
   * Whether or not to route the edges along grid lines only, if the grid
   * is enabled. Default is false
   */
  protected boolean routeToGrid = false;
  
  /**
   * 
   */
  public OrthogonalLayout(JGraphX graph)
  {
     super(graph);
     orthModel = new OrthogonalModel(graph);
  }

  /**
   * 
   */
  public void execute(Object parent)
  {
     // Create the rectangulation
     
  }

}

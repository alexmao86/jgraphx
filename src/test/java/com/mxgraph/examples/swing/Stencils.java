package com.mxgraph.examples.swing;

import java.awt.Image;

import javax.swing.JFrame;

import com.mxgraph.swing.view.JGraphXInteractiveCanvas;
import com.mxgraph.util.JGraphXUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.mxgraph.canvas.Graphics2DCanvas;
import com.mxgraph.canvas.GraphicsCanvas2D;
import com.mxgraph.shape.Stencil;
import com.mxgraph.shape.StencilRegistry;
import com.mxgraph.swing.JGraphXComponent;
import com.mxgraph.util.XmlUtils;
import com.mxgraph.view.JGraphX;

public class Stencils extends JFrame
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2707712944901661771L;

	public Stencils()
	{
		super("Stencils");

		try
		{
			String filename = Stencils.class.getResource(
					"/com/mxgraph/examples/swing/shapes.xml").getPath();
			Document doc = XmlUtils.parseXml(JGraphXUtils.readFile(filename));

			Element shapes = (Element) doc.getDocumentElement();
			NodeList list = shapes.getElementsByTagName("shape");

			for (int i = 0; i < list.getLength(); i++)
			{
				Element shape = (Element) list.item(i);
				StencilRegistry.addStencil(shape.getAttribute("name"),
						new Stencil(shape)
						{
							protected GraphicsCanvas2D createCanvas(
									final Graphics2DCanvas gc)
							{
								// Redirects image loading to graphics canvas
								return new GraphicsCanvas2D(gc.getGraphics())
								{
									protected Image loadImage(String src)
									{
										// Adds image base path to relative image URLs
										if (!src.startsWith("/")
												&& !src.startsWith("http://")
												&& !src.startsWith("https://")
												&& !src.startsWith("file:"))
										{
											src = gc.getImageBasePath() + src;
										}

										// Call is cached
										return gc.loadImage(src);
									}
								};
							}
						});
			}

			JGraphX graph = new JGraphX();
			Object parent = graph.getDefaultParent();

			graph.getModel().beginUpdate();
			try
			{
				Object v1 = graph
						.insertVertex(parent, null, "Hello", 20, 20, 80, 30,
								"shape=and;fillColor=#ff0000;gradientColor=#ffffff;shadow=1");
				Object v2 = graph.insertVertex(parent, null, "World!", 240,
						150, 80, 30, "shape=xor;shadow=1");
				graph.insertEdge(parent, null, "Edge", v1, v2);
			}
			finally
			{
				graph.getModel().endUpdate();
			}

			JGraphXComponent graphComponent = new JGraphXComponent(graph)
			{
				// Sets global image base path
				public JGraphXInteractiveCanvas createCanvas()
				{
					JGraphXInteractiveCanvas canvas = super.createCanvas();
					canvas.setImageBasePath("/com/mxgraph/examples/swing/");

					return canvas;
				}
			};
			getContentPane().add(graphComponent);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public static void main(String[] args)
	{
		Stencils frame = new Stencils();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(400, 320);
		frame.setVisible(true);
	}

}

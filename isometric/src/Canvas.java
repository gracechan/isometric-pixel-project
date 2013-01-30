import geometry.Point3D;
import geometry.Shape3D;

import java.awt.*;

import javax.swing.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

@SuppressWarnings("serial")
class Canvas extends JPanel
implements MouseListener, MouseMotionListener {
	private CanvasData data;

	public Canvas(CanvasData data) {
		this.data = data;
		addListeners();
		repaint();
	}

	private void addListeners() {
		addMouseListener(this);
		addMouseMotionListener(this);
	}

	public Dimension getPreferredSize() {
		return new Dimension(getWidth(), getHeight());
	}

	// add all the drawing code here
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING,
							java.awt.RenderingHints.VALUE_ANTIALIAS_OFF );

		setBackground(Color.white);

		double[][] isoMatrix = data.getIsometricMatrix();
		AffineTransform at = g2.getTransform();
		g2.translate(getWidth()/2, getHeight()/2);

		for(int i=0; i<data.getNumShapes(); i++) {
			drawShape(g2, data.getShape(i), isoMatrix);
		}
		g2.setTransform(at);
		revalidate();
	}

	public void drawShape(Graphics2D g, Shape3D shape, double[][] isoMatrix) {

		g.setColor(Color.black);

		// draw vertices (mostly for picking purposes because it helps to highlight
		// a selected vertex if we wish to modify its location)
		for(int i=0; i<shape.getNumVertices(); i++) {
			Point3D p = shape.getVertex(i);
			Point2D.Double p_2D = p.transform(isoMatrix);
			g.fillOval((int)(p_2D.x-3), (int)(p_2D.y-3), 6, 6);
		}

		for(int i=0; i<shape.getNumEdges(); i++) {
			int[] edgeInds = shape.getEdge(i);
			Point3D p1 = shape.getVertex(edgeInds[0]);
			Point3D p2 = shape.getVertex(edgeInds[1]);
			Point2D.Double p1_2D = p1.transform(isoMatrix);
			Point2D.Double p2_2D = p2.transform(isoMatrix);
			g.drawLine((int)p1_2D.x, (int)p1_2D.y, (int)p2_2D.x, (int)p2_2D.y);
		}
	}	

	public void mouseClicked(MouseEvent e) {
		System.out.println("Canvas.mouseClicked");
	}

	public void mousePressed(MouseEvent e) {
		System.out.println("Canvas.mousePressed");
	}

	public void mouseReleased(MouseEvent e) {
		System.out.println("Canvas.mouseReleased");
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mouseDragged(MouseEvent e) {
	}

	public void mouseMoved(MouseEvent e) {
	}

	public void keyTyped(KeyEvent e) {	
		System.out.println("Canvas.keyTyped");
	}

	public void keyPressed(KeyEvent e) {
		System.out.println("Canvas.keyPressed");
	}

	public void keyReleased(KeyEvent e) {
		System.out.println("Canvas.keyReleased");
	}
}
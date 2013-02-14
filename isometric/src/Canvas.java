import geometry.Point2D;
import geometry.Point3D;
import geometry.Shape3D;

import java.awt.*;

import javax.swing.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;

@SuppressWarnings("serial")
class Canvas extends JPanel
implements MouseListener, MouseMotionListener {
	private CanvasData data;
	private int[] translation;

	public Canvas(CanvasData data) {
		this.data = data;
		addListeners();
		repaint();
		translation = new int[3];
		translation[0] = 0;
		translation[1] = 0;
		translation[2] = 0;
	}

	private void addListeners() {
		addMouseListener(this);
		addMouseMotionListener(this);
	}

	public Dimension getPreferredSize() {
		return new Dimension(getWidth(), getHeight());
	}
	
	public void drawAxes(Graphics2D g2) {
		// draw axis
		double[][] isoMatrix = data.getIsometricMatrix();
		
		Point3D x_axis = new Point3D(600, 0, 0);
		Point3D y_axis = new Point3D(0, 600, 0);
		Point3D z_axis = new Point3D(0, 0, 600);
		Point3D x_axis_neg = new Point3D(-600, 0, 0);
		Point3D y_axis_neg = new Point3D(0, -600, 0);
		Point3D z_axis_neg = new Point3D(0, 0, -600);
		
		Point2D x_axis2D = x_axis.transform(isoMatrix);
		Point2D y_axis2D = y_axis.transform(isoMatrix);
		Point2D z_axis2D = z_axis.transform(isoMatrix);
		Point2D x_axis2D_neg = x_axis_neg.transform(isoMatrix);
		Point2D y_axis2D_neg = y_axis_neg.transform(isoMatrix);
		Point2D z_axis2D_neg = z_axis_neg.transform(isoMatrix);
		
		g2.setColor(Color.RED);
		g2.drawLine(0, 0, (int) x_axis2D.x, (int) x_axis2D.y);
		g2.drawLine(0, 0, (int) x_axis2D_neg.x, (int) x_axis2D_neg.y);
		g2.setColor(Color.pink);
		g2.drawLine(0, 0, (int) y_axis2D.x, (int) y_axis2D.y);
		g2.drawLine(0, 0, (int) y_axis2D_neg.x, (int) y_axis2D_neg.y);
		g2.setColor(Color.blue);
		g2.drawLine(0, 0, (int) z_axis2D.x, (int) z_axis2D.y);
		g2.drawLine(0, 0, (int) z_axis2D_neg.x, (int) z_axis2D_neg.y);	
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
		drawAxes(g2);

		for(int i=0; i<data.getNumShapes(); i++) {
			drawShape(g2, data.getShape(i), isoMatrix, i);
		}
		g2.setTransform(at);
		revalidate();
	}
	
	public void drawShape(Graphics2D g, Shape3D shape, double[][] isoMatrix, int shapeIndex) {	
		for(int i=0; i<shape.getNumFaces(); i++) {
			int[] face = shape.getFace(i);
			int numVertices = face.length;
			int[] faceX = new int[numVertices];
			int[] faceY = new int[numVertices];
			for(int j=0; j<numVertices; j++) {
				Point3D p = shape.getVertex(face[j]);
				Point2D v = p.transform(isoMatrix);
				faceX[j] = (int) v.x;
				faceY[j] = (int) v.y;
			}
			g.setColor(shape.getColor());
			g.fillPolygon(faceX, faceY, numVertices);
		}
		
		g.setColor(Color.black);
		// draw vertices (mostly for picking purposes because it helps to highlight
		// a selected vertex if we wish to modify its location)
		for(int i=0; i<shape.getNumVertices(); i++) {
			Point3D p = shape.getVertex(i);
			Point2D p_2D = p.transform(isoMatrix);
			
			g.setColor(data.isVertexSelected(shapeIndex, i) ? Color.red : Color.black);
			g.fillOval((int)(p_2D.x-3), (int)(p_2D.y-3), 6, 6);
		}
		
		g.setColor(Color.black);
		for(int i=0; i<shape.getNumEdges(); i++) {
			int[] edgeInds = shape.getEdge(i);
			Point3D p1 = shape.getVertex(edgeInds[0]);
			Point3D p2 = shape.getVertex(edgeInds[1]);
			Point2D p1_2D = p1.transform(isoMatrix);
			Point2D p2_2D = p2.transform(isoMatrix);
			g.drawLine((int)p1_2D.x, (int)p1_2D.y, (int)p2_2D.x, (int)p2_2D.y);
		}
	}	

	public void checkSelected(MouseEvent e) {
		int hitX = e.getX() - getWidth()/2;
		int hitY = e.getY() - getHeight()/2;

		data.clearSelectedPoint();
		for(int i=0; i<data.getNumShapes(); i++) {
			Shape3D s = data.getShape(i);
			for(int j=0; j<s.getNumVertices(); j++) {
				Point3D p = s.getVertex(j);
				Point2D p_2D = p.transform(data.getIsometricMatrix());
				Rectangle r = new Rectangle((int)p_2D.x-3, (int)p_2D.y-3, 6, 6);
				
				if (r.contains(hitX, hitY)) {
					data.setSelectedPoint(i, j);
				}
			}
		}
	}
	
	public void mouseClicked(MouseEvent e) {
		System.out.println("Canvas.mouseClicked");
	}

	public void mousePressed(MouseEvent e) {
		System.out.println("Canvas.mousePressed");
		checkSelected(e);
		repaint();
	}

	public void mouseReleased(MouseEvent e) {
		System.out.println("Canvas.mouseReleased");
		repaint();
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mouseDragged(MouseEvent e) {
		System.out.println("(" + e.getX() + ", " + e.getY() + ")");
		data.translateShape(new Point2D(e.getX() - getWidth()/2, e.getY() - getHeight()/2));
		repaint();
	}

	public void mouseMoved(MouseEvent e) {
	}

	public void keyTyped(KeyEvent e) {	
		System.out.println("Canvas.keyTyped");
		
		if (e.getKeyChar() == 't') {
			data.getShape(0).translate(-10, -10, -10);
		}
		repaint();
	}

	public void keyPressed(KeyEvent e) {
		System.out.println("Canvas.keyPressed");
	}

	public void keyReleased(KeyEvent e) {
		System.out.println("Canvas.keyReleased");
	}
}
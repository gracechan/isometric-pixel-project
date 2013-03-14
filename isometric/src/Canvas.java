import geometry.Point2D;
import geometry.Point3D;
import geometry.Shape3D;

import java.awt.*;

import javax.swing.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.lang.Math;

@SuppressWarnings("serial")
class Canvas extends JPanel
implements MouseListener, MouseMotionListener {
	private CanvasData data;
	private final int vertex_size = 4;

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
	
	public void paintLine(Graphics2D g2, int slopeY, int slopeX, Point2D p1, Point2D p2) {
		if (slopeX == 0) {
			// draw vertical line
			g2.drawLine((int)p1.x, (int)p1.y, (int)p2.x, (int)p2.y);
			return;
		}
		// start at end farthest to the left
		Point2D start = (p1.x <= p2.x) ? p1.clone() : p2.clone();
		Point2D end = (p1.x > p2.x) ? p1.clone() : p2.clone();
		System.out.println("p1: " + p1.toString());
		System.out.println("p2: " + p2.toString());
		
		if (slopeY * slopeX < 0) {		
			slopeX = Math.abs(slopeX);
			while(start.x < end.x && start.y >= end.y) {
				// draw the line
				int i=0;
				for (i=0; i<slopeX; i++) {
					g2.fillRect((int)start.x+i, (int)start.y, 1, 1);
					if (start.x+1 == end.x) {
						break;
					}
				}
				start.x += i;
				start.y -= 1;
			}
		} else {
			slopeX = Math.abs(slopeX);
			while(start.x < end.x && start.y <= end.y) {
				// draw the line
				int i=0;
				for (i=0; i<slopeX;i++) {
					g2.fillRect((int)start.x+i, (int)start.y, 1, 1);
					if (start.x+1 == end.x) {
						break;
					}
				}
				start.x += i;
				start.y += 1;
			}
		}
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
		//drawAxes(g2);
		
		for(int i=0; i<data.getNumShapes(); i++) {
			//drawShape(g2, data.getShape(i), isoMatrix, i);
		}
		g2.setColor(Color.BLUE);
		//g2.drawLine(99, 33, 240, 80);
		//g2.drawLine(18, 192, 34, 186);
		g2.setColor(Color.RED);
		paintLine(g2, 1, -3, new Point2D(18,192), new Point2D(34,186)); // -6 / 16
		//paintLine(g2, 1, 3, new Point2D(99,33), new Point2D(240,80));
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
		
		// draw vertices (mostly for picking purposes because it helps to highlight
		// a selected vertex if we wish to modify its location)
		g.setColor(Color.black);
		for(int i=1; i<shape.getNumVertices(); i++) {
			Point3D p = shape.getVertex(i);
			Point2D p_2D = p.transform(isoMatrix);
			
			g.setColor(data.isVertexSelected(shapeIndex, i) ? Color.red : Color.black);
			g.fillOval((int)(p_2D.x-(vertex_size/2)), (int)(p_2D.y-(vertex_size/2)), vertex_size, vertex_size);
		}
		
		g.setColor(Color.black);
		for(int i=0; i<shape.getNumEdges(); i++) {
			int[] edgeInds = shape.getEdge(i);
			Point3D p1 = shape.getVertex(edgeInds[0]);
			Point3D p2 = shape.getVertex(edgeInds[1]);
			Point2D p1_2D = p1.transform(isoMatrix);
			Point2D p2_2D = p2.transform(isoMatrix);
			System.out.print("(" + (int)p1_2D.x + ", " + (int)p1_2D.y + ")");
			System.out.println(" | (" + (int)p2_2D.x + ", " + (int)p2_2D.y + ")");
			g.drawLine((int)p1_2D.x, (int)p1_2D.y, (int)p2_2D.x, (int)p2_2D.y);
/*
			if ((int)p2_2D.x - (int)p1_2D.x != 0) {
				int dy = (int)p2_2D.y - (int)p1_2D.y;
				int dx = (int)p2_2D.x - (int)p1_2D.x;
				double slope = (double) dy / dx;				
				System.out.print(slope);
				System.out.print('\t');
			}
			if (p2_2D.x - p1_2D.x != 0) {
				double dy = p2_2D.y - p1_2D.y;
				double dx = p2_2D.x - p1_2D.x;
				double slope = dy / dx;				
				System.out.print(slope);
				System.out.print('\t');
			}
			
			if ((int)(Math.round(p2_2D.x) - Math.round(p1_2D.x)) != 0) {
				int dy = (int) (Math.round(p2_2D.y) - Math.round(p1_2D.y));
				int dx = (int) (Math.round(p2_2D.x) - Math.round(p1_2D.x));
				double slope = (double) dy / dx;				
				System.out.print(slope);
			}
			System.out.println();
*/
		}
	}	
	
	// Checks to see if we have clicked on a vertex
	public void checkSelected(int hitX, int hitY) {
		data.clearSelectedPoint();
		for(int i=0; i<data.getNumShapes(); i++) {
			Shape3D s = data.getShape(i);
			for(int j=0; j<s.getNumVertices(); j++) {
				Point3D p = s.getVertex(j);
				Point2D p_2D = p.transform(data.getIsometricMatrix());
				Rectangle r = new Rectangle((int)p_2D.x-(vertex_size/2), (int)p_2D.y-(vertex_size/2),
											vertex_size, vertex_size);
				
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
		checkSelected(e.getX() - getWidth()/2, e.getY() - getHeight()/2);
		repaint();
	}

	public void mouseReleased(MouseEvent e) {
		System.out.println("Canvas.mouseReleased");
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mouseDragged(MouseEvent e) {
		data.translateShape(new Point2D(e.getX()-getWidth()/2, e.getY()-getHeight()/2));
		repaint();
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
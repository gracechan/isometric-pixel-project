import geometry.Point2D;
import geometry.Point3D;
import geometry.Shape3D;

import java.awt.*;

import javax.swing.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

@SuppressWarnings("serial")
class Canvas extends JPanel
implements MouseListener, MouseMotionListener {
	private BufferedImage imgdata;
	private CanvasData data;
	private final int vertex_size = 4;
	private boolean drawSuggestions;

	public Canvas(CanvasData data) {
		this.data = data;
		drawSuggestions = false;
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

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		imgdata = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = (Graphics2D) g;
		getBufferedImage();
		g2.drawImage(imgdata, null, null);
		Color c = Color.black;
		Color r = Color.red;
		System.out.println("Black: "+c.toString());
		System.out.println("Red: "+r.toString());
		
		Color ex = new Color(imgdata.getRGB(getWidth()/2-56, getHeight()/2+34), true);
		System.out.println("pixel (-56,34): "+ex.toString());
	}
	
	// add all the drawing code here
	private void getBufferedImage() {
		Graphics2D g2 = imgdata.createGraphics();
		g2.setBackground(Color.white);
		g2.clearRect(0, 0, getWidth(), getHeight());
		g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING,
							java.awt.RenderingHints.VALUE_ANTIALIAS_OFF );

		double[][] isoMatrix = data.getIsometricMatrix();
		AffineTransform at = g2.getTransform();
		g2.translate(getWidth()/2, getHeight()/2);
		
		for(int i=0; i<data.getNumShapes(); i++) {
			drawShape(g2, data.getShape(i), isoMatrix, i);
		}
		
		g2.setTransform(at);
		revalidate();
	}
	
	public void drawShape(Graphics2D g, Shape3D shape, double[][] isoMatrix, int shapeIndex) {	
		System.out.println("draw shape");
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
		/*
		for(int i=0; i<shape.getNumVertices(); i++) {
			Point3D p = shape.getVertex(i);
			Point2D p_2D = p.transform(isoMatrix);
			g.setColor(data.isVertexSelected(shapeIndex, i) ? Color.red : Color.black);
			g.fillOval((int)(p_2D.x-(vertex_size/2)), (int)(p_2D.y-(vertex_size/2)), vertex_size, vertex_size);
		}
		*/
		g.setColor(Color.black);
		CanvasUtils.paintShape(imgdata, g, shape, isoMatrix);	
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
		data.clearSuggestions();
		checkSelected(e.getX() - getWidth()/2, e.getY() - getHeight()/2);
		data.suggestPoints(new Point2D(e.getX() - getWidth()/2, e.getY() - getHeight()/2));
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
		//data.translateShape(new Point2D(e.getX()-getWidth()/2, e.getY()-getHeight()/2));
		data.distortVertex(new Point2D(e.getX()-getWidth()/2, e.getY()-getHeight()/2));
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
	
	public void drawSuggestedPoints() {
		if (!drawSuggestions) return;		
	}
}
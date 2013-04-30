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
	/*
	 * imgdata: stores the image to draw on screen
	 * data: stores the 3D representation of the shapes
	 * vertex_size: diameter of the vertex being drawn
	 */
	private BufferedImage imgdata;
	private CanvasData data;
	private final int vertex_size = 4;
	private CanvasActions op = CanvasActions.TRANSLATE_OBJECT;
	private CanvasActions lineOption = CanvasActions.LINE_OPTION_ALTERNATE;

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

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		imgdata = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = (Graphics2D) g;
		getBufferedImage();
		g2.drawImage(imgdata, null, null);
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
			drawShape(g2, data.getShape(i), isoMatrix, i, lineOption);
		}
		
		g2.setTransform(at);
		revalidate();
	}
	
	public void drawShape(Graphics2D g, Shape3D shape, double[][] isoMatrix, int shapeIndex, 
			CanvasActions lineOption) {	
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
		for(int i=0; i<shape.getNumVertices(); i++) {
			Point3D p = shape.getVertex(i);
			Point2D p_2D = p.transform(isoMatrix);
			g.setColor(data.isVertexSelected(shapeIndex, i) ? Color.red : Color.black);
			g.fillOval((int)(p_2D.x-(vertex_size/2)), (int)(p_2D.y-(vertex_size/2)), vertex_size, vertex_size);
		}
		
		g.setColor(Color.black);
		CanvasUtils.paintShape(imgdata, g, shape, isoMatrix, lineOption);	
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
	
	public void setOperation(CanvasActions c) {
		op = c;		
	}
	
	public void setLineOption(CanvasActions c) {
		lineOption = c;		
	}
	
	public void mouseClicked(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
		data.clearSuggestions();
		checkSelected(e.getX() - getWidth()/2, e.getY() - getHeight()/2);
		data.suggestPoints(new Point2D(e.getX() - getWidth()/2, e.getY() - getHeight()/2));
		repaint();
	}

	public void mouseReleased(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	/*
	 * (non-Javadoc)
	 * @see java.awt.event.MouseMotionListener#mouseDragged(java.awt.event.MouseEvent)
	 * For this particular event, we distort vertices (i.e. we can drag them around the screen,
	 *  and alter the shape of the cube). 
	 *  translateShape is if you just want to move the whole object.
	 */
	public void mouseDragged(MouseEvent e) {
		switch(op) {
          case TRANSLATE_OBJECT:
        	data.translateShape(new Point2D(e.getX()-getWidth()/2, e.getY()-getHeight()/2));
		  case DISTORT_VERTEX:
			data.distortVertex(new Point2D(e.getX()-getWidth()/2, e.getY()-getHeight()/2));    
		}		
		repaint();
	}

	public void mouseMoved(MouseEvent e) {
	}

	public void keyTyped(KeyEvent e) {	
	}

	public void keyPressed(KeyEvent e) {
	}

	public void keyReleased(KeyEvent e) {
	}
}
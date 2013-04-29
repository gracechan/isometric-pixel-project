import geometry.Point2D;
import geometry.Point3D;
import geometry.Shape3D;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.Vector;

/*
 * I apologize in advance: this class is kind of a mess, it contains all my line drawing stuff.
 */

class CanvasUtils {	
	private static final int sample_size = 5;
	private static final int clump_size = 2;
	private static final int clump_penalty = 5;
	private static final int spurious_penalty = 1;
	private static final int correct_points = 1;
	
	// draws the axes. I don't really use this, but can be good for reference. 
	public static void drawAxes(Graphics2D g2, double[][] isoMatrix) {
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
		
		g2.setColor(Color.red);
		g2.drawLine(0, 0, (int) x_axis2D.x, (int) x_axis2D.y);
		g2.drawLine(0, 0, (int) x_axis2D_neg.x, (int) x_axis2D_neg.y);
		g2.setColor(Color.pink);
		g2.drawLine(0, 0, (int) y_axis2D.x, (int) y_axis2D.y);
		g2.drawLine(0, 0, (int) y_axis2D_neg.x, (int) y_axis2D_neg.y);
		g2.setColor(Color.blue);
		g2.drawLine(0, 0, (int) z_axis2D.x, (int) z_axis2D.y);
		g2.drawLine(0, 0, (int) z_axis2D_neg.x, (int) z_axis2D_neg.y);	
	}
	
	public static void paintShape(BufferedImage bi, Graphics2D g2, Shape3D shape, double[][] isoMatrix) {
		boolean[] isDrawn = new boolean[shape.getNumEdges()];
		
		// initialize array to all false since we haven't drawn anything yet.
		for (int i=0; i < isDrawn.length; i++) {
			isDrawn[i] = false;
		}
		
		for (int i=0; i < shape.getNumVertices(); i++) {
			Vector<Integer>	adjVertices = shape.getAdjacentVertices(i);
			Point2D p1 = shape.getVertex(i).transform(isoMatrix);
			/*
			 * minDiffs: stores the associated error with drawing the pixels surrounding p1
			 *  The one producing the least amount of error will be the point that will be drawn from.
			 */
			int minDiffs[][] = new int[9][adjVertices.size()];
			Point2D bestDrawRef = new Point2D(p1.x-1, p1.y-1);
			Point2D bestDraw = bestDrawRef.clone();
			
			for(int j=0; j<adjVertices.size(); j++) {
				Point2D p2 = shape.getVertex(adjVertices.get(j).intValue()).transform(isoMatrix);
				int eIndex = shape.getEdgeIndex(i, adjVertices.get(j).intValue());
				int[] slope = shape.getEdgeSlope(eIndex);
				int count = 0;
							
				if (isDrawn[eIndex] || slope[1] == 0) continue;	
				for (int y=-1; y<=1; y++) {
					for(int x=-1; x<=1; x++) {
						Point2D pStart = new Point2D(p1.x+x, p1.y+y);
						minDiffs[count][j] = getLineError(pStart, p2, slope);
						count++;
					}
				}
			} // for every neighbour of a certain vertex
			
			// find out which point we should use to draw lines from
			int minBad = adjVertices.size();
			for (int k=0; k<9; k++) {
				int total = 0;
				for(int n=0; n<adjVertices.size(); n++) {
					int eIndex = shape.getEdgeIndex(i, adjVertices.get(n).intValue());
					int[] slope = shape.getEdgeSlope(eIndex);
					if (slope[0]==1 && minDiffs[k][n] > Math.abs(slope[1])) {
						total++;
					} else if (slope[1]==1 && minDiffs[k][n] > Math.abs(slope[0])) {
						total++;
					}
				}
				if (minBad > total) {
					minBad = total;
					bestDraw.x = bestDrawRef.x + (int)(k % 3);
					bestDraw.y = bestDrawRef.y + (int)(k / 3);
				}
			}
			
			// draw the lines
			for(int j=0; j<adjVertices.size(); j++) {
				Point2D p2 = shape.getVertex(adjVertices.get(j).intValue()).transform(isoMatrix);
				int eIndex = shape.getEdgeIndex(i, adjVertices.get(j).intValue());
				int[] slope = shape.getEdgeSlope(eIndex);	
				
				// if we've already drawn this edge, do not redraw
				if (isDrawn[eIndex]) continue;
				isDrawn[eIndex] = true;
				
				// if we have a vertical edge, just draw between the two points to avoid jaggy verticals
				if (slope[1] == 0) {
					paintLine(g2, slope, p1, p2);
					continue;
				}
				paintLine(g2, slope, bestDraw, p2);
			}
		} // for every vertex
	}
	
	/*
	 * Calculates how far away p2 is from a line drawn from p1 with a slope of 1/n (or n)
	 */
	private static int getLineError(Point2D p1, Point2D p2, int[] slope) {
		p1 = p1.truncate(); p2 = p2.truncate();
		if(slope[0] == 1) {
			int height = (int) Math.abs(p2.y - p1.y);
			int width = (height+1) * Math.abs(slope[1]);
			int closest = (p1.x >= p2.x) ? (int)(p1.x - width) : (int)(p1.x + width);
			return (p1.x <= p2.x) ? (int)(p2.x - closest) : -(int)(p2.x - closest);
		} else if(slope[1] == 1) {
			int width = (int) Math.abs(p2.x - p1.x);
			int height = (width+1) * Math.abs(slope[0]);
			int closest = (p1.y >= p2.y) ? (int)(p1.y - height) : (int)(p1.y + height);
			return (p1.y <= p2.y) ? (int)(p2.y - closest) : -(int)(p2.y - closest);
		}
		return 0;	
	}
	
	/*
	 * This is a cost function to determine how "nice" a corner looks. We do this by drawing
	 * two images: one with how it will actually look like, and one that shows how an "ideal" corner
	 * would look like with edges with specific slopes meeting to form the corner.
	 * 
	 * We then calculate cost by checking for clumps (2x2 blocks caused by bad intersection of lines)
	 * and for spurious pixels (pixels that appear in actual version, but not in the ideal version). 
	 * 
	 * This method will probably need some more improvement before we can actually use it for painting
	 * shapes, and should probably also be combined with line error calculations. 
	 */
	public static int calculateCost(Shape3D shape, Point2D pStart, int vIndex,
			boolean[]isDrawn, double[][] isoMatrix) {
		Vector<Integer> adjVertices = shape.getAdjacentVertices(vIndex);
		Point2D p1 = pStart.truncate();
		int cost = 0;
		
		// setup some bufferedimages to use for calculating cost
		BufferedImage ideal = new BufferedImage(600, 600, BufferedImage.TYPE_INT_ARGB);
		BufferedImage actual = new BufferedImage(600, 600, BufferedImage.TYPE_INT_ARGB);
		Graphics2D idealg = ideal.createGraphics(), actualg = actual.createGraphics();
		idealg.setBackground(Color.white); actualg.setBackground(Color.white);
		idealg.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING,
				java.awt.RenderingHints.VALUE_ANTIALIAS_OFF);
		actualg.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING,
				java.awt.RenderingHints.VALUE_ANTIALIAS_OFF);
		idealg.translate(ideal.getWidth()/2, ideal.getHeight()/2);
		actualg.translate(ideal.getWidth()/2, ideal.getHeight()/2);
		AffineTransform i_at = idealg.getTransform();
		AffineTransform a_at = actualg.getTransform();
		
		// paint ideal corners and actual corners		
		for(int i=0; i<adjVertices.size(); i++) {
			int slope[] = shape.getEdgeSlope(vIndex, adjVertices.get(i));
			int eIndex = shape.getEdgeIndex(vIndex, adjVertices.get(i));
			Point2D p2_actual = shape.getVertex(adjVertices.get(i)).transform(isoMatrix);
			int dx = Math.abs(slope[1]*sample_size), dy = Math.abs(slope[0]*sample_size);
			Point2D p2_ideal = new Point2D(p1.x + ((p2_actual.x >= p1.x) ? dx : -dx),
					p1.y + ((p2_actual.y >= p1.y) ? dy : -dy));
			
			if (isDrawn[eIndex]) {
				paintLine(actualg, slope, p2_actual, p1);
			} else {
				paintLine(actualg, slope, p1, p2_actual);				
			}
			paintLine(idealg, slope, p1, p2_ideal);
		}
		idealg.setTransform(i_at);
		actualg.setTransform(a_at);
		
		// Check for clumps. A clump is defined as a 2x2 pixel group, 
		// which doesn't look very nice in corners. If we find a clump in the image
		Point2D start = (new Point2D(p1.x-((sample_size-1)/2), p1.y-((sample_size-1)/2))).truncate();
		start.translate(ideal.getWidth()/2, ideal.getWidth()/2);	
		for (int i=0; i<sample_size-(clump_size-1); i++) {
			for (int j=0; j<sample_size-(clump_size-1); j++) {
				boolean isClump = true;
				for(int x=0; x<clump_size; x++) {
					for(int y=0; y<clump_size; y++) {
						Color c = new Color(actual.getRGB((int)(start.x+i+x), (int)(start.y+j+y)), true);
						if (!c.equals(Color.red) && !c.equals(Color.black)) {
							isClump = false;
						}
					}				
				}
				if (isClump) cost += clump_penalty;
			}
		}
		
		// check for spurious pixels which don't match up with the ideal version
		for (int i=0; i<sample_size-(clump_size-1); i++) {
			for (int j=0; j<sample_size-(clump_size-1); j++) {
				Color c_act = new Color(actual.getRGB((int)(start.x+i), (int)(start.y+j)));
				Color c_idl = new Color(ideal.getRGB((int)(start.x+i), (int)(start.y+j)));
				if(c_act.equals(Color.red) || c_act.equals(Color.black) && c_idl.equals(Color.black)) {
					cost -= correct_points;
				} else if(!(c_act.equals(Color.red) || c_act.equals(Color.black)) && !c_idl.equals(Color.black)) {
					cost -= correct_points;
				} else {
					cost += spurious_penalty;
				}
			}
		}
		return cost;
	}

	public static void paintLine(Graphics2D g2, int slope[], Point2D p1, Point2D p2) {
		int slopeY=slope[0], slopeX=slope[1], lineError, span, altspan, numAlt;
		p1 = p1.truncate(); p2 = p2.truncate();
		Point2D start = p1.clone();
		g2.setColor(Color.black);
		if (slopeX == 0) {
			// draw vertical line
			g2.drawLine((int)p1.x, (int)p1.y, (int)p2.x, (int)p2.y);
			return;
		}
		
		lineError = getLineError(p1, p2, slope);
		span = Math.abs((slopeY==1) ? slopeX : slopeY);
		altspan = span;
		numAlt = Math.abs(lineError);
		
		// if we predict that the slope won't allow for the points to connect,
		// then we just draw a regular line
		if (span < 3 && Math.abs(lineError) > span) {
			g2.setColor(Color.red);
			g2.drawLine((int)p1.x, (int)p1.y, (int)p2.x, (int)p2.y);
			return;
		} else if (span >= 3 && Math.abs(lineError) > span) {
			/*
			 * For lines with slope 1/n or n where n >= 3, we can alternate either n and n-1 or 
			 * n and n+1 in order for them to connect, so we have a much wider tolerance for line error
			 */
			int maxError = 0, maxAlt = 0;
			// we can have a maximum of half the spans be n+1 or n-1 spans, and use this to widen our
			// tolerance of error
			if (slopeY == 1) {
				int dy = (int) Math.abs(p2.y - p1.y);
				maxAlt = (dy % 2 == 0) ? dy / 2 : (dy / 2) + 1;
			} else {
				int dx = (int) Math.abs(p2.x - p1.x);
				maxAlt = (dx % 2 == 0) ? dx / 2 : (dx / 2) + 1;
			}
			maxError = span + maxAlt;
			if (Math.abs(lineError) > maxError) {
				// if the error is still way too large, then we just draw a regular red line
				g2.setColor(Color.red);
				g2.drawLine((int)p1.x, (int)p1.y, (int)p2.x, (int)p2.y);
				return;				
			}
			// this is span we alternate with.
			altspan = (lineError > 0) ? altspan+1 : altspan-1;
		}
		
		boolean useAltSpan = false;
		
		// this while loop condition looks complicated, but actually just means
		// "while we haven't connected the two points together yet". We draw each line
		// one span at a time until we get to the other end.
		while( ((p2.x < start.x) ? (p2.x <= p1.x) : (p2.x >= p1.x)) && 
			   ((p2.y < start.y) ? (p2.y <= p1.y) : (p2.y >= p1.y))) {
			// draw the line
			int i=0;
			if (slopeY == 1) {
				// draw the span (horizontal because of 1/n slope)
				for (i = 0; i < ((useAltSpan) ? altspan : span); i++) {
					// figure out x-coordinate of pixel we want to draw
					int dx = (p2.x < p1.x) ? (int)(p1.x-i) : (int)(p1.x+i);
					g2.fillRect(dx, (int)p1.y, 1, 1);
					if ((p2.x < p1.x) ? (dx <= p2.x) : (dx >= p2.x)) {
						break;		
					}
				}
				// depending on where p2 is, adjust p1
				p1.x = (p2.x < start.x) ? p1.x-((i == 0) ? 1 : i) : p1.x+((i == 0) ? 1 : i);
				p1.y = (p2.y < start.y) ? p1.y-1 : p1.y+1;
				
			} else if (slopeX == 1) {
				// draw the span (vertical because of n slope)
				for (i=0; i < ((useAltSpan) ? altspan : span); i++) {
					// figure out y-coordinate of the pixel we want to draw
					int dy = (p2.y < p1.y) ? (int)(p1.y-i) : (int)(p1.y+i);
					g2.fillRect((int)p1.x, dy, 1, 1);
					if ((p2.y < p1.y) ? (dy <= p2.y) : (dy >= p2.y)) {
						break;		
					}
				}
				// depending on where p2 is, adjust p1
				p1.y = (p2.y < start.y) ? p1.y-((i == 0) ? 1 : i) : p1.y+((i == 0) ? 1 : i);
				p1.x = (p2.x < start.x) ? p1.x-1 : p1.x+1;
			}
			// we use this to switch between n and (n+1 or n-1) spans
			if (numAlt > 0) {
				if (useAltSpan)	numAlt--;
				useAltSpan = !useAltSpan;
			} else {
				useAltSpan = false;
			}
		}
	}
}
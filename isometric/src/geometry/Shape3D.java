package geometry;

import java.awt.Color;
//import java.awt.Graphics2D;
import java.util.Vector;

public class Shape3D {
	/*
	 * adjmatrix: tells us which vertices are connected to each other using booleans. Vertices are indexed
	 * using their index in the "vertices" variable
	 *  e.g. if vertex 1 and 3 are are connected by an edge, then adjmatrix[1][3] = true, otherwise false.
	 * populated: tells us whether we've populated the slopes arrays or not.
	 * original_slopes: the slopes of the edges in 2D before distortion.
	 *  the slope is represented as a int array of size 2: [0]rise/[1]run. A vertical edge is represented with
	 *  [0]=1, [1]=0. 
	 * current_slopes: the slopes of the edges in 2D as they are currently are
	 * 
	 * I'm hoping to encapsulate a lot of the 2D parts of this shape object into the actual 2D object.
	 * 
	 * TODO: find a better way to organize all these constructors. they suck :(
	 */
	Vector<Point3D> vertices;
	Vector<int[]> edges;
	Vector<int[]> faces;
	Color colour;
	
	Vector<Vector<Boolean>> adjmatrix;
	int[][] original_slopes;
	int[][] current_slopes;
	Point2D[] vertices_2D;
	private boolean populated;

	public Shape3D() {
		vertices = new Vector<Point3D>();
		edges = new Vector<int[]>();
		faces = new Vector<int[]>();
		adjmatrix = new Vector<Vector<Boolean>>();
		colour = new Color(255, 0, 0, 25);
		populated = false;
	}
	
	public Shape3D(Color c) {
		this();
		colour = c;
	}

	public Shape3D(Point3D[] vs, int[][] es, int[][] fs) {
		this();
		for(int i=0; i<vs.length; i++) addVertex(vs[i]);
		for(int i=0; i<es.length; i++) addEdge(es[i]);
		for(int i=0; i<fs.length; i++) addFace(fs[i], fs[i].length);
	}
	
	public Shape3D(Point3D[] vs, int[][] es, int[][] fs, Color c) {
		this(vs, es, fs);
		colour = c;
	}

	protected void addVertex(Point3D v) {
		// for all when we add a vertex, we need to increase the adjacency matrix to accommodate for this
		for(int i=0; i<adjmatrix.size(); i++) {
			adjmatrix.get(i).add(new Boolean(false));
		}
		vertices.add(v.clone());		
		Vector<Boolean> a = new Vector<Boolean>();	
		for(int i=0; i<getNumVertices(); i++) a.add(new Boolean(false));
		adjmatrix.add(a);
	}

	protected void addEdge(int[] e) {
		int[] e_ = {e[0], e[1]};
		// update adjacency matrix
		adjmatrix.get(e[0]).set(e[1], new Boolean(true));
		adjmatrix.get(e[1]).set(e[0], new Boolean(true));
		edges.add(e_);
	}
	
	protected void addFace(int[] f, int numVertices) {
		int[] f_ = new int[numVertices];
		for(int i=0; i<numVertices; i++) {
			f_[i] = f[i];
		}
		faces.add(f_);
	}

	public void translateVertex(int i, double tx, double ty, double tz) {
		vertices.get(i).translate(tx, ty, tz);		
	}
	
	public void translate(double tx, double ty, double tz) {
		for(int i=0; i<getNumVertices(); i++) {
			translateVertex(i, tx, ty, tz);
		}
	}
	
	public int getNumEdges() {
		return (edges==null) ? 0 : edges.size();
	}

	public int[] getEdge(int i) {
		int[] edge = {edges.get(i)[0], edges.get(i)[1]};
		return edge;
	}
	
	public int[] getEdgeSlope(int i) {
		int[] slope = {(int)current_slopes[i][0],(int)current_slopes[i][1]};
		return slope;
	}
	
	public int[] getEdgeSlope(int vIndex1, int vIndex2) {
		int[] slope = {0,0};
		int eIndex = getEdgeIndex(vIndex1, vIndex2);		
		if (eIndex < 0) return slope;
		
		slope[0] = current_slopes[eIndex][0];
		slope[1] = current_slopes[eIndex][1];
		return slope;
	}
	
	public int getEdgeIndex(int vIndex1, int vIndex2) {
		for (int i=0; i < getNumEdges(); i++) {
			if ((getEdge(i)[0]==vIndex1 && getEdge(i)[1]==vIndex2) ||
				(getEdge(i)[0]==vIndex2 && getEdge(i)[1]==vIndex1)) {
				return i;
			}
		}
		return -1;
	}
		
	public void setEdgeSlope(int edgeIndex, int y, int x) {
		current_slopes[edgeIndex][0] = y;
		current_slopes[edgeIndex][1] = x;
	}
	
	public void setEdgeSlope(int vIndex1, int vIndex2, int y, int x) {
		int eIndex = getEdgeIndex(vIndex1, vIndex2);
		if (eIndex < 0) return;
		current_slopes[eIndex][0] = y;
		current_slopes[eIndex][1] = x;
	}


	public int getNumVertices() {
		return (vertices==null) ? 0 : vertices.size();
	}

	public Point3D getVertex(int i) {
		return vertices.get(i).clone();
	}
	
	public Point2D getVertex2D(int i) {
		return vertices_2D[i].clone();		
	}
	
	public int getNumFaces() {
		return (faces==null) ? 0 : faces.size();
	}
	
	public int[] getFace(int i) {
		int numVertices = faces.get(i).length;
		int[] face = new int[numVertices];
		for(int x=0; x<numVertices; x++) {
			face[x] = faces.get(i)[x];
		}
		return face;
	}
	
	public void changeColor(Color c) {
		colour = c;
	}
	
	public Color getColor() {
		return colour;
	}
	
	public void populateOriginalSlopes(double[][] isoMatrix) {
		// also populates the 2D versions of each point.
		if (populated) return;
		populated = true;
		current_slopes = new int[getNumEdges()][2];
		original_slopes = new int[getNumEdges()][2];
		vertices_2D = new Point2D[getNumVertices()];
		
		for(int i=0; i<getNumEdges(); i++) {
			int[] edgeInds = getEdge(i);
			Point3D p1 = getVertex(edgeInds[0]);
			Point3D p2 = getVertex(edgeInds[1]);
			Point2D p1_2D = p1.transform(isoMatrix);
			Point2D p2_2D = p2.transform(isoMatrix);
			if ((int)p2_2D.x - (int)p1_2D.x == 0) {
				original_slopes[i][0] = 1;
				original_slopes[i][1] = 0;
				current_slopes[i][0] = 1;
				current_slopes[i][1] = 0;
			} else {
				double slope = (p2_2D.y - p1_2D.y) / (p2_2D.x - p1_2D.x);
				original_slopes[i][0] = (Math.abs(slope) > 1) ? (int)Math.round(slope) : 1;
				original_slopes[i][1] = (Math.abs(slope) > 1) ? 1 : (int)Math.round(1/slope);
				
				current_slopes[i][0] = original_slopes[i][0];
				current_slopes[i][1] = original_slopes[i][1];
			}
		}
		
		for(int i=0; i<getNumVertices(); i++) {
			vertices_2D[i] = getVertex(i).transform(isoMatrix).truncate();			
		}
	}
	
	public Vector<Integer> getAdjacentVertices(int v) {
		Vector<Integer> result = new Vector<Integer>();
		for(int i=0; i < adjmatrix.get(v).size(); i++) {
			if (adjmatrix.get(v).get(i).booleanValue()) {
				result.add(new Integer(i));
			}
		}
		return result;
	}
	
	public static class Box extends Shape3D {
		private static final Point3D[] vs =
			   {new Point3D(0,0,0),
				new Point3D(1,0,0),
				new Point3D(1,1,0),
				new Point3D(0,1,0),
				new Point3D(0,0,1),
				new Point3D(1,0,1),
				new Point3D(1,1,1),
				new Point3D(0,1,1)};

		private static final int[][] es = 
			   {{0,1},{1,2},{2,3},{3,0},
				{4,5},{5,6},{6,7},{7,4},
				{0,4},{1,5},{2,6},{3,7}};
		
		private static final int[][] fs = 
			{{0,1,2,3},{4,5,6,7},{0,1,5,4},
			 {2,6,7,3},{2,6,5,1},{0,3,7,4}};

		public Box() {			
			super(vs, es, fs);			
		}
		
		public Box(Color c) {			
			super(vs, es, fs, c);			
		}
		
		public Box(Point3D from, Point3D to) {
			super();
			for(int i=0; i<vs.length; i++) {
				Point3D p = vs[i].clone();
				p.scale(to.x-from.x, to.y-from.y, to.z-from.z);
				p.translate(from.x, from.y, from.z);
				addVertex(p);
			}
			
			for(int i=0; i<es.length; i++) addEdge(es[i]);
			for(int i=0; i<fs.length; i++) addFace(fs[i], 4);
		}
		
		public Box(Point3D from, Point3D to, Color c) {
			this(from, to);
			changeColor(c);
		}
	}
}
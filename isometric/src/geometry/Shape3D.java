package geometry;

import java.awt.Color;
//import java.awt.Graphics2D;
import java.util.Vector;

public class Shape3D {
	Vector<Point3D> vertices;
	Vector<int[]> edges;
	Vector<int[]> faces;
	Color colour;

	public Shape3D() {
		vertices = new Vector<Point3D>();
		edges = new Vector<int[]>();
		faces = new Vector<int[]>();
		colour = new Color(255, 0, 0, 25);
	}
	
	public Shape3D(Color c) {
		this();
		colour = c;
	}

	public Shape3D(Point3D[] vs, int[][] es) {
		this();
		for(int i=0; i<vs.length; i++) addVertex(vs[i]);
		for(int i=0; i<es.length; i++) addEdge(es[i]);
	}

	public void addVertex(Point3D v) {
		vertices.add(v.clone());
	}

	public void addEdge(int[] e) {
		int[] e_ = {e[0], e[1]};
		edges.add(e_);
	}

	public int getNumEdges() {
		return (edges==null) ? 0 : edges.size();
	}

	public int[] getEdge(int i) {
		int[] edge = {edges.get(i)[0], edges.get(i)[1]};
		return edge;
	}

	public int getNumVertices() {
		return (vertices==null) ? 0 : vertices.size();
	}

	public Point3D getVertex(int i) {
		return vertices.get(i).clone();
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
		
		public Box() {			
			super(vs, es);			
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
		}
	}
}
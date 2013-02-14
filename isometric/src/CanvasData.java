import geometry.IsometricTransform;
import geometry.Point2D;
import geometry.Point3D;
import geometry.Shape3D;

import java.util.Vector;

public class CanvasData {
	private static final Point3D[] axes_3D = 
		{new Point3D(1,0,0),
		new Point3D(0,1,0),
		new Point3D(0,0,1),
		new Point3D(-1,0,0),
		new Point3D(0,-1,0),
		new Point3D(0,0,-1)};
	
	private int[] selectedPoint;
	private Point2D[] axes_2D;
	
	Vector<Shape3D> shapes3D;
	IsometricTransform transform;
	Point3D centre;

	public CanvasData() {
		shapes3D = new Vector<Shape3D>();
		transform = new IsometricTransform(-1.0/3.0, 1.0/2.0);
		centre = new Point3D(0,0,0);
		selectedPoint = new int[2];
		selectedPoint[0] = -1;
		selectedPoint[1] = -1;
		axes_2D = new Point2D[6];
		
		for (int i=0; i < axes_3D.length; i++) {
			axes_2D[i] = axes_3D[i].transform(getIsometricMatrix());
			System.out.println(axes_2D[i].toString());
		}
	}

	public void addShape(Shape3D s) {
		shapes3D.add(s);
	}

	public void addShapes(Vector<Shape3D> s) {
		for(int i=0; i<s.size(); i++) shapes3D.add(s.get(i));
	}

	public void removeShape(int i) {
		shapes3D.remove(i);
	}
	
	public void clearShapes() {
		shapes3D.clear();
	}

	public int getNumShapes() {
		return (shapes3D==null) ? 0 : shapes3D.size();
	}

	public Shape3D getShape(int i) {
		return shapes3D.get(i);
	}

	public double[][] getIsometricMatrix() {
		return transform.getMatrix();
	}
	
	public void setSelectedPoint(int shape, int vertex) {
		selectedPoint[0] = shape;
		selectedPoint[1] = vertex;
	}
	
	public void clearSelectedPoint() {
		selectedPoint[0] = -1;
		selectedPoint[1] = -1;
	}
	
	public boolean isVertexSelected(int shape, int vertex) {
		return selectedPoint[0] == shape && selectedPoint[1] == vertex;
	}
	
	public boolean isShapeSelected(int shape) {
		return selectedPoint[0] == shape;
	}
	
	public void translateShape(Point2D dest) {
		int i, shape = selectedPoint[0], vertex = selectedPoint[1];
		if (shape == -1 && vertex == -1) {
			System.out.println("No Point Selected");
			return;
		}
		
		Point3D vertex_3D = shapes3D.get(shape).getVertex(vertex);
		Point2D vertex_2D = vertex_3D.transform(getIsometricMatrix());
		Point2D coeffs_vertex, coeffs_dest;
		
		for(i=0; i < axes_2D.length; i++) {
			// find out distance from origin
			coeffs_vertex = 
					Point2D.findCoefficients(axes_2D[i], axes_2D[(i+1)%axes_2D.length], vertex_2D);
			
			coeffs_dest = 
					Point2D.findCoefficients(axes_2D[i], axes_2D[(i+1)%axes_2D.length], dest);	
			
			if (coeffs_vertex.x >= 0 && coeffs_vertex.y >= 0) {	
				System.out.println("coeffs_dest: " + coeffs_dest);
				Point2D test = new Point2D(
						axes_2D[i].x * coeffs_dest.x + axes_2D[(i+1)%axes_2D.length].x * coeffs_dest.y,
						axes_2D[i].y * coeffs_dest.x + axes_2D[(i+1)%axes_2D.length].y * coeffs_dest.y);
				Point2D test2 = new Point2D(
						axes_2D[i].x * coeffs_vertex.x + axes_2D[(i+1)%axes_2D.length].x * coeffs_vertex.y,
						axes_2D[i].y * coeffs_vertex.x + axes_2D[(i+1)%axes_2D.length].y * coeffs_vertex.y);
				
				System.out.println("test: " + test + " dest: " + dest);
				System.out.println("test2: " + test2 + " dest: " + vertex_2D);
				
				// find out what point is in 3D
				Point3D dest_3D = new Point3D(
						axes_3D[i].x * coeffs_dest.x + axes_3D[(i+1)%axes_2D.length].x * coeffs_dest.y,
						axes_3D[i].y * coeffs_dest.x + axes_3D[(i+1)%axes_2D.length].y * coeffs_dest.y,
						axes_3D[i].z * coeffs_dest.x + axes_3D[(i+1)%axes_2D.length].z * coeffs_dest.y);

				System.out.println("Old position: " + vertex_3D.toString());
				System.out.println("New position 3D: " + dest_3D.toString());
				System.out.println("New position 2D: " + dest_3D.transform(getIsometricMatrix()));
				
				Point3D distance_3D = new Point3D(
						dest_3D.x - vertex_3D.x,
						dest_3D.y - vertex_3D.y,
						dest_3D.z - vertex_3D.z);
				
				shapes3D.get(shape).translate(distance_3D.x, distance_3D.y, distance_3D.z);
				System.out.println("translate shape " + distance_3D.toString());
				return;
			}
		}
	}
}
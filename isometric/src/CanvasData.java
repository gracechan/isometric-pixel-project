import geometry.IsometricTransform;
import geometry.Point3D;
import geometry.Shape3D;
import java.util.Vector;

public class CanvasData {
	Vector<Shape3D> shapes3D;
	IsometricTransform transform;
	Point3D centre;

	public CanvasData() {
		shapes3D = new Vector<Shape3D>();
		transform = new IsometricTransform(-1.0/3.0, 1.0/2.0);
		centre = new Point3D(0,0,0);
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
}
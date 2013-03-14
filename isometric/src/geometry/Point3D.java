package geometry;
import geometry.Point2D;

public class Point3D {
	public double x, y, z;

	public Point3D() {
		x = 0; y = 0; z = 0;
	}

	public Point3D(double[] p) {
		x = p[0]; y = p[1]; z = p[2];
	}

	public Point3D(double x, double y, double z) {
		this.x = x; this.y = y; this.z = z;
	}

	public Point3D(Point3D p) {
		x = p.x; y = p.y; z = p.z;
	}

	public Point3D clone() {
		return new Point3D(this);
	}	

	public Point2D transform(double[][] M) {
		double newX = M[0][0]*x + M[0][1]*y + M[0][2]*z;
		double newY = M[1][0]*x + M[1][1]*y + M[1][2]*z;
		return new Point2D(newX, newY);
	}

	public String toString() {
		String s = "(" + x + ", " + y + ", " + z + ")";
		return s;
	}

	public void scale(double sx, double sy, double sz) {
		x *= sx;
		y *= sy;
		z *= sz;
	}

	public void translate(double tx, double ty, double tz) {
		x += tx;
		y += ty;
		z += tz;
	}
}
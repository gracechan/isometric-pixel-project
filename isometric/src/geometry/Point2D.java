package geometry;

public class Point2D {

	public double x, y;

	public Point2D() {
		x = 0; y = 0;
	}

	public Point2D(double[] p) {
		x = p[0]; y = p[1];
	}

	public Point2D(double x, double y) {
		this.x = x; this.y = y;
	}
	
	public Point2D(Point2D p) {
		x = p.x; y = p.y;
	}

	public Point2D clone() {
		return new Point2D(this);
	}
		
	public String toString() {
		String s = "(" + x + ", " + y + ")";
		return s;
	}

	public void scale(double sx, double sy) {
		x *= sx;
		y *= sy;
	}

	public void translate(double tx, double ty) {
		x += tx;
		y += ty;
	}
	
	// returns an integer version of the point through truncation 
	// (the fields are still technically doubles though o.o)
	public Point2D truncate() {
		Point2D p = this.clone();
		p.x = (int)p.x;
		p.y = (int)p.y;
		return p;
	}
	
	/*
	 * For axis1, axis2, location, which are all points, find (a,b) such that:
	 * (a*axis1.x + b*axis2.x, a*axis1.y + b*axis2.y) = (location.x, location.y)
	 */
	public static Point2D findCoefficients(Point2D axis1, Point2D axis2, Point2D location) {
		double x = 0, y = 0;
		y = (location.x * axis1.y - location.y * axis1.x) / (axis2.x * axis1.y - axis1.x * axis2.y);
		x = (axis1.x != 0) ? 
				((location.x - axis2.x * y) / axis1.x)
				: ((location.y - axis2.y * y) / axis1.y);
		
		return new Point2D(x, y);
	}
}
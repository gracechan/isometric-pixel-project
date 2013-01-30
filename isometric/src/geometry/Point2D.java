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

}
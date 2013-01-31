import geometry.Point3D;
import geometry.Shape3D;

import java.util.Vector;
import java.awt.Color;

public class TestShapes {
	public static Vector<Shape3D> robot() {
		Vector<Shape3D> shapes = new Vector<Shape3D>();
		Shape3D body = new Shape3D.Box(new Point3D(-40,-40,-40), new Point3D(40,40,40));
		shapes.add(body);
		Shape3D head = new Shape3D.Box(new Point3D(-15,-15,40), new Point3D(15,15,70), new Color(0, 255, 0, 25));
		shapes.add(head);
		Shape3D leftArm = new Shape3D.Box(new Point3D(40,-30,20), new Point3D(120,-10,40), new Color(0, 0, 255, 25));
		shapes.add(leftArm);
		Shape3D rightArm = new Shape3D.Box(new Point3D(-30,40,20), new Point3D(-10,120,40), new Color(0, 0, 255, 25));
		shapes.add(rightArm);
		Shape3D leftLeg = new Shape3D.Box(new Point3D(20,-20,-40), new Point3D(30,0,-200), new Color(255, 255, 0, 25));
		shapes.add(leftLeg);
		Shape3D rightLeg = new Shape3D.Box(new Point3D(-20,20,-40), new Point3D(0,30,-200), new Color(255, 255, 0, 25));
		shapes.add(rightLeg);
		return shapes;
	}
}
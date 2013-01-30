package geometry;

public class IsometricTransform {
	private double negSlope, posSlope;
	private double[][] matrix;

	public IsometricTransform() {
		negSlope = -0.5;
		posSlope = 0.5;
		matrix = computeMatrix(negSlope, posSlope);
	}

	public IsometricTransform(double negSlope, double posSlope) {
		this.negSlope = negSlope;
		this.posSlope = posSlope;
		matrix = computeMatrix(negSlope, posSlope);
	}

	private static double[][] computeMatrix(double negSlope, double posSlope) {
		double angle1 = Math.acos(Math.sqrt(-posSlope*negSlope));
		double angle2 = Math.atan(Math.sqrt(-posSlope/negSlope));
		double[][] M = new double[3][3];
		M[0][1] = Math.cos(angle2);
		M[0][0] = -Math.sin(angle2);
		M[0][2] = 0;
		M[1][1] = Math.cos(angle1)*Math.sin(angle2);
		M[1][0] = Math.cos(angle1)*Math.cos(angle2);
		M[1][2] = -Math.sin(angle1);

		return M;
	}

	public double[][] getMatrix() {
		// make a copy
		double[][] M = {{matrix[0][0], matrix[0][1], matrix[0][2]},
						{matrix[1][0], matrix[1][1], matrix[1][2]}};
		return M;
	}
}
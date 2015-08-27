package diffEqGrapherFX;

public class Curve {
	private double[] xVals;
	private double[] yVals;
	
	public Curve(double[] xCoords, double[] yCoords){
		this.xVals = xCoords;
		this.yVals = yCoords;
	}

	public double[] getxVals() {
		return xVals;
	}

	public double[] getyVals() {
		return yVals;
	}

}

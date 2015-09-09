package diffEqGrapherFX;

import java.util.Arrays;
import java.util.function.DoubleBinaryOperator;

public class PointCalculator implements Runnable{

	private Thread t;
	
	private int xMax, xMin;
	private double startX, startY;
	private double[][] x, y;
	private int curveIndex;
	private double stepLength;
	private DoubleBinaryOperator equation;
	
	public PointCalculator(int xMax, int xMin, double startX, double startY, double[][] x, double[][] y, int curveIndex, double stepLength, DoubleBinaryOperator f){
		this.xMax = xMax;
		this.xMin = xMin;
		this.startX = startX;
		this.startY = startY;
		this.x = x;
		this.y = y;
		this.curveIndex = curveIndex;
		this.stepLength = stepLength;
		this.equation = f;
	}
	
	public void run(){
		produceLines();
	}
	
	public void produceLines(){
		producePoints(startX, startY, curveIndex, stepLength, equation);
		producePoints(startX, startY, curveIndex, -stepLength, equation);
	}
	
	public void producePoints(double startX, double startY, int curveIndex, double stepLength, DoubleBinaryOperator f){
		x[curveIndex] = new double[4096];
		y[curveIndex] = new double[4096];
		PointIterator p = Integration.IntegratorPath(f, startX, startY, stepLength);
		int index = 0;
		while(p.getX() <= xMax && p.getX() >= xMin){
			x[curveIndex][index] = p.getX();
			y[curveIndex][index] = p.getY();
			p.advance();
			index++;
			//dynamic resizing
			if(index == x[curveIndex].length){
				x[curveIndex] = Arrays.copyOf(x[curveIndex], x[curveIndex].length * 2);
				y[curveIndex] = Arrays.copyOf(y[curveIndex], y[curveIndex].length * 2);
			}
		}
		x[curveIndex] = Arrays.copyOf(x[curveIndex], index);
		y[curveIndex] = Arrays.copyOf(y[curveIndex], index);
	}

	public double[][] getX() {
		return x;
	}

	public double[][] getY() {
		return y;
	}

	public void start() {
		if(t == null){
			t = new Thread(this);
			t.start();
		}
		
	}
	
	
	
}

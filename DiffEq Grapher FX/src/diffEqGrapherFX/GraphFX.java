package diffEqGrapherFX;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class GraphFX {

	//private double scale;
//	private double xOffset = -100;
//	private double yOffset;
	
	private double[][] xCoordinates, yCoordinates;
	
	public GraphFX(double xOffset, double yOffset, double[][] xCoordinates, double[][] yCoordinates){
//		this.xOffset = xOffset;
//		this.yOffset = yOffset;
		this.xCoordinates = xCoordinates;
		this.yCoordinates = yCoordinates;
	}
	
	public void draw(GraphicsContext g){
		g.setStroke(Color.BLACK);
		g.strokeLine(0,-10000, 0, 10000);
		g.strokeLine(-10000,0, 10000, 0);
		
		g.setStroke(Color.RED);
		//System.out.println(xCoordinates.length);
		for(int i = 0; i < xCoordinates.length; i++){
			double[] xc = xCoordinates[i];
			double[] yc = yCoordinates[i];
//			for(Double d : xc){
//				d += xOffset;
//			}
//			for(Double d : yc){
//				d += yOffset;
//			}
			g.strokePolyline(xc, yc, xc.length);
		}
	}
	
	
}

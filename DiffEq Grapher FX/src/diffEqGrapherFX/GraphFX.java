package diffEqGrapherFX;

import java.util.Arrays;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class GraphFX {

	//private double scale;
	private double xOffset = 0;
	private double yOffset = 0;
	
	private double[][] xCoordinates, yCoordinates;
	
	public GraphFX(double xOffset, double yOffset, double[][] xCoordinates, double[][] yCoordinates){
		this.xOffset = xOffset;
		this.yOffset = yOffset;
		this.xCoordinates = xCoordinates;
		this.yCoordinates = yCoordinates;
	}
	
	public void draw(GraphicsContext g){
		g.setStroke(Color.BLACK);
		g.strokeLine(0 + xOffset,-10000, 0+xOffset, 10000);
		g.strokeLine(-10000,0 + yOffset, 10000, 0 + yOffset);
		
//		System.out.println(Arrays.toString(yCoordinates[0]));
		g.setStroke(Color.RED);
		for(int i = 0; i < xCoordinates.length; i++){
			double[] xc = xCoordinates[i];
			double[] yc = yCoordinates[i];
			for(int j = 0; j < xc.length; j++){
				xc[j] += xOffset;
			}
			for(int j = 0; j < yc.length; j++){
				yc[j] += yOffset;
			}

			g.strokePolyline(xc, yc, xc.length);
		}
	}
	
	
}

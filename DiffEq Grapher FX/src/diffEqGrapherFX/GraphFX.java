package diffEqGrapherFX;

//import javafx.scene.*;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
//import javafx.scene.shape.Line;
import javafx.scene.Group;
//import javafx.scene.Scene;
//import javafx.application.*;
//import javafx.stage.*;

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
	
	public void draw(GraphicsContext g, Group root){
		g.setStroke(Color.BLACK);
		g.strokeLine(0,-10000, 0, 10000);
		g.strokeLine(-10000,0, 10000, 0);
		
		g.setStroke(Color.RED);
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

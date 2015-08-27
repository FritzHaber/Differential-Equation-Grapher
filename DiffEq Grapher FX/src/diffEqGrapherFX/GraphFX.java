package diffEqGrapherFX;

import java.util.ArrayList;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class GraphFX {

	//private double scale;
	private double xOffset;
	private double yOffset;
	
	private ArrayList<Curve> lines;
	private Color defaultColor;
	
	
	public GraphFX(double xOffset, double yOffset, ArrayList<Curve> lines, Color defaultColor) {
		this.lines = lines;
		this.xOffset = xOffset;
		this.yOffset = yOffset;
		this.defaultColor = defaultColor;
	}

	public void draw(GraphicsContext g){
		//axis
		g.setStroke(Color.BLACK);
		g.strokeLine(0 + xOffset,-10000, 0+xOffset, 10000);
		g.strokeLine(-10000,0 + yOffset, 10000, 0 + yOffset);
		
		//curves
		g.setStroke(defaultColor);
		for(int i = 0; i < lines.size(); i++){
			drawLine(g, lines.get(i), defaultColor);
		}
	}
	
	public void drawLine(GraphicsContext g, Curve line, Color lineColor){
		g.setStroke(lineColor);
		g.strokePolyline(line.getxVals(), line.getyVals(), line.getxVals().length);
	}

	public void setLines(ArrayList<Curve> lines) {
		this.lines = lines;
	}
	
	
}

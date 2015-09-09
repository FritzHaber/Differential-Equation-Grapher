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
	
	private GraphicsContext g;
	
	
	public GraphFX(double xOffset, double yOffset, ArrayList<Curve> lines, Color defaultColor, GraphicsContext g) {
		this.lines = lines;
		this.xOffset = xOffset;
		this.yOffset = yOffset;
		this.defaultColor = defaultColor;
		this.g = g;
	}

	public void draw(){
		g.clearRect(-100000,-100000,200000,200000); //to clear the graph
		//axis
		g.setStroke(Color.BLACK);
		g.strokeLine(0 + xOffset,-10000, 0+xOffset, 10000);
		g.strokeLine(-10000,0 + yOffset, 10000, 0 + yOffset);
		
		//curves
		g.setStroke(defaultColor);
		for(int i = 0; i < lines.size(); i++){
			drawLine(lines.get(i), defaultColor);
		}
	}
	
	public void drawLine(Curve line, Color lineColor){
		g.setStroke(lineColor);
		g.strokePolyline(line.getxVals(), line.getyVals(), line.getxVals().length);
	}

	public void setLines(ArrayList<Curve> lines2) {
		this.lines = lines2;
	}
	
	public void addCurve(Curve c){
		lines.add(c);
	}
	
	public void zoom(double zoomFactor){
		g.scale(zoomFactor, zoomFactor);
		draw();
	}
	
	public void pan(double dx, double dy){
		g.translate(dx, dy);
		draw();
	}
	
	
}

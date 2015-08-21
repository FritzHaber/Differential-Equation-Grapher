package diffEqGrapherFX;

import java.util.ArrayList;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class GraphFX {

	//private double scale;
	private double xOffset;
	private double yOffset;
	
	private ArrayList<Curve> lines;
	
	
	public GraphFX(double xOffset, double yOffset, ArrayList<Curve> lines) {
		this.lines = lines;
		this.xOffset = xOffset;
		this.yOffset = yOffset;
	}

	public void draw(GraphicsContext g){
		//axis
		g.setStroke(Color.BLACK);
		g.strokeLine(0 + xOffset,-10000, 0+xOffset, 10000);
		g.strokeLine(-10000,0 + yOffset, 10000, 0 + yOffset);
		
		//curves
		g.setStroke(Color.RED);
		for(int i = 0; i < lines.size(); i++){
			g.strokePolyline(lines.get(i).getxVals(), lines.get(i).getyVals(), lines.get(i).getxVals().length);
		}
	}
	
	
}

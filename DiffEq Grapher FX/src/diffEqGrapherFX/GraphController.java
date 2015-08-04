package diffEqGrapherFX;

import java.util.Arrays;
import java.util.function.DoubleBinaryOperator;

//import javafx.scene.paint.*;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;


public class GraphController extends Application{


	public final int SCREEN_WIDTH = 1440;
	public final int SCREEN_HEIGHT = 900;
	public final int XMAX = 20, XMIN = -XMAX;
	public final int YMAX = 25, YMIN = -YMAX;
	public final int NUM_STEPS = 100;
	public final double STEP_LENGTH = 1.0/256;
	private DoubleBinaryOperator f = (x,y) -> (x*x*x*x + 6*x*x + x + 6) / (4*y*y*y + 2*y*y*y*y +10);
	private Canvas canvas = new Canvas(SCREEN_WIDTH,SCREEN_HEIGHT);
	
	public static void main(String[] args){
		launch(args);
	}
	
	@Override
	public void start(Stage stage) throws Exception {
		GridPane grid = new GridPane();
		TextField tf = new TextField(); //gets equation in;
		Group root = new Group();
		root.getChildren().add(canvas);
		Scene scene = new Scene(grid, 1000, 1000);
		grid.add(tf, 0, 0);
		grid.add(canvas, 0, 1);
		stage.setScene(scene);
		//make escape close window
		stage.addEventFilter(KeyEvent.KEY_PRESSED,e -> {
			if(e.getCode() == KeyCode.ESCAPE)
				System.exit(0);
		});
		stage.setFullScreen(true);
		stage.setFullScreenExitHint("");
		GraphicsContext g = canvas.getGraphicsContext2D();
		//puts 0,0 in center of display
		g.scale(1,-1);
		g.translate(0, (-1) * SCREEN_HEIGHT);
		g.translate(SCREEN_WIDTH / 2, SCREEN_HEIGHT / 2);
		g.scale(50,50);
		g.setLineWidth(0.05);
		
		double[][] x = new double[(YMAX-YMIN+1) * 2][];
		double[][] y = new double[(YMAX-YMIN+1) * 2][];

			for(int i = 0; i < x.length; i+=2){
				producePoints(0, (i/2+YMIN)/2, x, y, i, STEP_LENGTH);
				producePoints(0, (i/2+YMIN)/2, x, y, i + 1, -STEP_LENGTH);
			}

		GraphFX graph = new GraphFX(0,0,x, y);
		graph.draw(g, root);
		stage.show();


	}
	public void producePoints(double startX, double startY, double[][] x, double[][] y, int curveIndex, double stepLength){
		x[curveIndex] = new double[4096];
		y[curveIndex] = new double[4096];
		PointIterator p = Integration.IntegratorPath(f, startX, startY, stepLength);
		int index = 0;
		while(p.getX() <= XMAX && p.getX() >= XMIN){
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

}

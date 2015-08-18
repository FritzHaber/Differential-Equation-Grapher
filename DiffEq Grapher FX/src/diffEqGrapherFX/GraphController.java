package diffEqGrapherFX;

import java.io.File;
import java.util.Arrays;
import java.util.function.DoubleBinaryOperator;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;


public class GraphController extends Application{

	public final int SCREEN_WIDTH = 1440;
	public final int SCREEN_HEIGHT = 900;
	public final int XMAX = 20, XMIN = -XMAX;
	public final int YMAX = 25, YMIN = -YMAX;
	public final int NUM_STEPS = 100;
	public final double STEP_LENGTH = 1.0/256;
	public final String SAMPLE_EQ = "(x^4 + 6x^2 + x + 6) / (2y^4 + 4y^3 + 10)";
	private volatile DoubleBinaryOperator f = (x,y) -> (x*x*x*x + 6*x*x + x + 6) / (4*y*y*y + 2*y*y*y*y +10);
	public final int BORDER_WIDTH = 25;
	private Canvas canvas = new Canvas(SCREEN_WIDTH - 2*BORDER_WIDTH, SCREEN_HEIGHT - 9*BORDER_WIDTH);
	
	public static void main(String[] args){
		launch(args);
	}
	
	@Override
	public void start(Stage stage) throws Exception {
		Group root = new Group();
		root.getChildren().add(canvas);
		
		GraphicsContext g = canvas.getGraphicsContext2D();
		//puts 0,0 in center of display
		g.scale(1,-1);
		g.translate(0, (-1) * SCREEN_HEIGHT);
		g.translate(SCREEN_WIDTH / 2, SCREEN_HEIGHT / 2);
		g.scale(50,50);
		g.setLineWidth(0.05);
		//deals with padding and such
		g.translate(-0.5, 2);
		
		GridPane grid = new GridPane();
		grid.setVgap(10);
		grid.getColumnConstraints().add(new ColumnConstraints(SCREEN_WIDTH - 100));
		grid.getColumnConstraints().add(new ColumnConstraints(100));
		//grid.setBorder(null);
		
		
		TextField tf = new TextField(); //gets equation in;
		tf.setPromptText("Enter the differential equation");
		tf.setFocusTraversable(false); //makes it so its not auto-selected at application start (which hides the suggestion text)
		grid.add(tf, 0, 0);
		
		Label equationLabel = new Label(SAMPLE_EQ);
		VBox v = new VBox(equationLabel);
		v.setStyle("-fx-background-color: rgba(0, 0, 0, 0);");
		grid.add(v, 0, 2);
		
		Button submit = new Button("Submit");
		submit.setMinWidth(30);
		submit.setFocusTraversable(false);
		grid.add(submit, 1, 0);
		submit.setOnAction(e -> {
				equationLabel.setText(tf.getText());
//				ExpressionCompiler ec = new ExpressionCompiler(tf.getText());
//				f = ec.getDBO();
				f = Expressions.compile(tf.getText());
				draw(g, f);
			});
		
		Button settings = new Button();
		Image image = new Image(getClass().getResourceAsStream("gear.png"));
		settings.setGraphic(new ImageView(image));
		settings.setFocusTraversable(false);
		grid.add(settings, 1, 2);
		settings.setOnAction(e -> {								
				Stage settingsStage = new Stage();
				settingsStage.initModality(Modality.APPLICATION_MODAL);
				settingsStage.setHeight(300);
				settingsStage.setWidth(500);
				
				settingsStage.setX(SCREEN_WIDTH/2 - settingsStage.getWidth()/2);
				settingsStage.setY(SCREEN_HEIGHT/2 - settingsStage.getHeight()/2);
				settingsStage.show();
			});
		

		
		Scene scene = new Scene(grid, SCREEN_WIDTH, SCREEN_HEIGHT);
		scene.getStylesheets().add("Style.css");
		
//		canvas.setOnScroll(new EventHandler<ScrollEvent>(){
//
//			@Override
//			public void handle(ScrollEvent arg0) {
//				double zoomFactor = 1.05;
//                double deltaY = arg0.getDeltaY();
//                if (deltaY < 0){
//                  zoomFactor = 2.0 - zoomFactor;
//                }
//				XMAX *= zoomFactor;
//				YMAX *= zoomFactor;
//				
//				draw(g,f);
//			}
//			
//		});
		
		
		grid.add(canvas, 0, 1);
		grid.setPadding(new Insets(25,25,25,25));
		
		stage.setScene(scene);
		//make escape close window
		stage.addEventFilter(KeyEvent.KEY_PRESSED,e -> {
			if(e.getCode() == KeyCode.ESCAPE)
				System.exit(0);
		});
//		stage.setFullScreen(true);
		stage.setFullScreenExitHint("");
		
		
		
		draw(g, f);
		stage.show();


	}
	public void draw(GraphicsContext g, DoubleBinaryOperator f){
		g.clearRect(-100000,-100000,200000,200000); //intended to clear the graph
		double[][] x = new double[(int)((YMAX-YMIN+1) * 2)][];
		double[][] y = new double[(int)((YMAX-YMIN+1) * 2)][];
//		PointCalculator p;
		for(int i = 0; i < x.length; i+=2){
//			p = new PointCalculator(XMAX, XMIN, 0, (i/2+YMIN)/2, x, y, i, STEP_LENGTH, f);
//			p.start();
//			x = p.getX();
//			y = p.getY();
//			System.out.println(Arrays.toString(x[0]));
			producePoints(0, (i/2+YMIN)/2, x, y, i, STEP_LENGTH, f);
			producePoints(0, (i/2+YMIN)/2, x, y, i + 1, -STEP_LENGTH, f);
		}
		System.out.println(f.applyAsDouble(1, 0));
		System.out.println(f.applyAsDouble(0, 2));
//		System.out.println(Arrays.toString(y[0]));
//		System.out.println("---------------");
		GraphFX graph = new GraphFX(0,0,x, y);
		graph.draw(g);
	}
	
	//values of x and y are updated through references
	public void producePoints(double startX, double startY, double[][] x, double[][] y, int curveIndex, double stepLength, DoubleBinaryOperator f){
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

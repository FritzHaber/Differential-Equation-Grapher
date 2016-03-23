package diffEqGrapherFX;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.DoubleBinaryOperator;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
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
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import util.Util;


public class GraphController extends Application {

  public final int SCREEN_WIDTH = 1440;
  public final int SCREEN_HEIGHT = 900;
  public final int XMAX = 20;
  public final int XMIN = -XMAX;
  public final int YMAX = 25;
  public final int YMIN = -YMAX;
  public final int NUM_STEPS = 100;
  public final double STEP_LENGTH = 1.0 / 128;
  public final String SAMPLE_EQ = "(x^4 + 6x^2 + x + 6) / (2y^4 + 4y^3 + 10)";
  public final int BORDER_WIDTH = 25;
  public final Color DEFAULT_COLOR = Color.RED;
  public final Color USER_COLOR = Color.BLUE;
  public final int DEFAULT_POINT_RES = 20; // high number = lower resolution

  private volatile DoubleBinaryOperator f =
      (x, y) -> (x * x * x * x + 6 * x * x + x + 6) / (4 * y * y * y + 2 * y * y * y * y + 10);

  private Canvas canvas =
      new Canvas(SCREEN_WIDTH - 2 * BORDER_WIDTH, SCREEN_HEIGHT - 9 * BORDER_WIDTH);
  private ArrayList<Curve> lines;

  public static void main(String[] args) {
    launch(args);
    double d = Math.E;
  }

  @Override
  public void start(Stage stage) throws Exception {
    // Group root = new Group();
    // root.getChildren().add(canvas);

    GraphicsContext g = canvas.getGraphicsContext2D();
    // puts 0,0 in center of display
    g.scale(1, -1);
    g.translate(0, (-1) * SCREEN_HEIGHT);
    g.translate(SCREEN_WIDTH / 2, SCREEN_HEIGHT / 2);
    g.scale(50, 50);
    g.setLineWidth(0.05);
    // deals with padding and such
    g.translate(-0.5, 2);

    GridPane grid = new GridPane();
    grid.setVgap(10);
    grid.getColumnConstraints().add(new ColumnConstraints(SCREEN_WIDTH - 200));
    grid.getColumnConstraints().add(new ColumnConstraints(100));
    grid.getColumnConstraints().add(new ColumnConstraints(100));

    // grid.setBorder(null);


    GraphFX graph = new GraphFX(0, 0, lines, g);
    drawLines(g, f, graph);



    TextField tf = new TextField(); // gets equation in;
    tf.setPromptText("dy/dx = ");
    tf.setFocusTraversable(false); // makes it so its not auto-selected at application start (which
                                   // hides the suggestion text)
    grid.add(tf, 0, 0, 2, 1);

    Label equationLabel = new Label(SAMPLE_EQ);
    grid.add(equationLabel, 0, 2);

    Button submit = new Button("Submit");
    submit.setMinWidth(30);
    submit.setFocusTraversable(false);
    grid.add(submit, 2, 0);
    submit.setOnAction(e -> {
      final String newEq = tf.getText();
      equationLabel.setText(newEq);

      ListenableFuture<DoubleBinaryOperator> newDBO = Util.exec.submit(() -> {
        return Expressions.compile(newEq);
      });
      newDBO.addListener(() -> {
        try {
          System.out.println(f.hashCode());
          f = newDBO.get();
          System.out.println(f.hashCode());
        } catch (Exception e1) {
        }
        drawLines(g, f, graph);
      }, Util.exec);

    });

    Button settings = new Button();
    Image image = new Image(getClass().getResourceAsStream("gear.png"));
    settings.setGraphic(new ImageView(image));
    settings.setFocusTraversable(false);
    grid.add(settings, 2, 2);
    settings.setOnAction(e -> {
      Stage settingsStage = new Stage();
      settingsStage.initModality(Modality.APPLICATION_MODAL);
      settingsStage.setHeight(300);
      settingsStage.setWidth(500);

      settingsStage.setX(SCREEN_WIDTH / 2 - settingsStage.getWidth() / 2);
      settingsStage.setY(SCREEN_HEIGHT / 2 - settingsStage.getHeight() / 2);

      settingsStage.show();

    });

    Button addLine = new Button("Add Line");
    addLine.setOnAction(e -> {
      Stage addLinePrompt = new Stage();
      addLinePrompt.initModality(Modality.APPLICATION_MODAL);
      addLinePrompt.setHeight(200);
      addLinePrompt.setWidth(300);


      TextField xField = new TextField();
      TextField yField = new TextField();

      GridPane tempGrid = new GridPane();
      tempGrid.setAlignment(Pos.CENTER);
      Scene temp = new Scene(tempGrid, addLinePrompt.getWidth(), addLinePrompt.getHeight());
      Button add = new Button("Add");
      add.setOnAction(d -> {
        addLinePrompt.close();
        Curve c1 = computeOneWayLine(Double.parseDouble(xField.getText()),
            Double.parseDouble(yField.getText()), STEP_LENGTH, f, USER_COLOR, DEFAULT_POINT_RES);
        Curve c2 = computeOneWayLine(Double.parseDouble(xField.getText()),
            Double.parseDouble(yField.getText()), -STEP_LENGTH, f, USER_COLOR, DEFAULT_POINT_RES);
        graph.addCurve(c1);
        graph.addCurve(c2);
        graph.drawLine(c1);
        graph.drawLine(c2);
      });


      tempGrid.add(new Label("X Value: "), 0, 0);
      tempGrid.add(xField, 1, 0);
      tempGrid.add(new Label("Y Value: "), 0, 1);
      tempGrid.add(yField, 1, 1);
      tempGrid.add(add, 0, 2);

      addLinePrompt.setScene(temp);
      addLinePrompt.show();
    });

    grid.add(addLine, 1, 2);

    Scene scene = new Scene(grid, SCREEN_WIDTH, SCREEN_HEIGHT);
    // scene.getStylesheets().add("Style.css");


    grid.add(canvas, 0, 1);
    grid.setPadding(new Insets(25, 25, 25, 25));

    stage.setScene(scene);
    // make escape close window
    stage.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
      if (e.getCode() == KeyCode.ESCAPE)
        System.exit(0);
    });
    // stage.setFullScreen(true);
    stage.setFullScreenExitHint("");



    canvas.addEventFilter(ScrollEvent.SCROLL, e -> {
      graph.zoom(Math.exp(e.getDeltaY() / 100));
    });

    canvas.addEventFilter(MouseEvent.MOUSE_DRAGGED, e -> {
      graph.pan(e.getX() / -500, e.getY() / -500);
    });

    stage.show();


  }

  public void drawLines(GraphicsContext g, DoubleBinaryOperator f, GraphFX graph) {
    int curveNum = (int) ((YMAX - YMIN + 1));

    List<ListenableFuture<Curve>> lineFutures = new ArrayList<ListenableFuture<Curve>>();
    lines = new ArrayList<Curve>(curveNum);
    for (int i = 0; i < 2 * curveNum; i += 2) {
      final int index = i;
      ListenableFuture<Curve> forwardTemp = Util.compute.submit(() -> {
        return computeOneWayLine(0, (index / 2 + YMIN) / 2, STEP_LENGTH, f, DEFAULT_COLOR,
            DEFAULT_POINT_RES);
      });
      ListenableFuture<Curve> backTemp = Util.compute.submit(() -> {
        return computeOneWayLine(0, (index / 2 + YMIN) / 2, -STEP_LENGTH, f, DEFAULT_COLOR,
            DEFAULT_POINT_RES);
      });
      lineFutures.add(forwardTemp);
      lineFutures.add(backTemp);
    }
    ListenableFuture<List<Curve>> lineListFuture = Futures.allAsList(lineFutures);
    lineListFuture.addListener(() -> {
      try {
        graph.setLines(new ArrayList<Curve>(lineListFuture.get()));
      } catch (Exception e) {
        throw new RuntimeException();
      }
      graph.draw();
    }, Platform::runLater);

  }

  public Curve computeOneWayLine(double startX, double startY, double stepLength,
      DoubleBinaryOperator f, Color c, int resolution) {
    double[] x = new double[8192];
    double[] y = new double[8192];
    // 8192 is a prediction for the length, array can be extended later
    PointIterator p = Integration.IntegratorPath(f, startX, startY, stepLength);
    int index = 0;
    while (p.getX() <= XMAX && p.getX() >= XMIN) {
      x[index] = p.getX();
      y[index] = p.getY();
      p.advance();
      index++;
      // dynamic resizing
      if (index == x.length) {
        x = Arrays.copyOf(x, x.length * 2);
        y = Arrays.copyOf(y, y.length * 2);
      }
    }
    x = Arrays.copyOf(x, index);
    y = Arrays.copyOf(y, index);
    return new Curve(x, y, c, resolution);
  }


}

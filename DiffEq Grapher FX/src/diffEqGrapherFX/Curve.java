package diffEqGrapherFX;

import javafx.scene.paint.Color;

public class Curve {
  private double[] allXVals;
  private double[] allYVals;
  private Color lineColor;
  // TODO: scale based on zoom level
  private int pointRes; // refers to the step size for drawing, so if resolution = 3, and its points
                        // are
                        // (1,2,3,4,5), than only 1 and 4 would be drawn

  public Curve(double[] xCoords, double[] yCoords, Color color, int resolution) {
    this.allXVals = xCoords;
    this.allYVals = yCoords;
    this.lineColor = color;
    this.pointRes = resolution;
  }

  public void setRes(int res) {
    this.pointRes = res;
  }

  public double[] getxVals() {
    double[] corseXVals = new double[allXVals.length / pointRes];
    for (int i = 0; i < allXVals.length / pointRes; i++) {
      corseXVals[i] = allXVals[i * pointRes];
    }
    return corseXVals;
  }

  public double[] getyVals() {
    double[] corseYVals = new double[allYVals.length / pointRes];
    for (int i = 0; i < allYVals.length / pointRes; i++) {
      corseYVals[i] = allYVals[i * pointRes];
    }
    return corseYVals;
  }

  public Color getLineColor() {
    return lineColor;
  }

  public int getRes() {
    return pointRes;
  }

}

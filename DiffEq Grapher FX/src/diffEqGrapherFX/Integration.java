package diffEqGrapherFX;

import java.util.function.DoubleBinaryOperator;
import static java.lang.Math.*;

public class Integration {
  // @author Hank O'Brien unless otherwise noted

  public interface Cursor {

    void moveX(double xd);

    void moveY(double yd);

    void setPosition(double d1, double d2);

    double getX();

    double getY();
  }


  // the four parameter arrays are for the Runge-Kutta-feldmann 5th order integration math
  // these are the Cash-Karp values
  // D is C*, C* is an invalid variable name
  // all arrays have a padding row/column to make the arrays 1-based for readability
  // The k terms are 1 based in RKF5, so these are changed to 1-based to smooth their use
  public static final double[] A =
      new double[] {0, 0, (1.0 / 5), (3.0 / 10), (3.0 / 5), 1, (7.0 / 8)};
  public static final double[][] B = new double[][] {
      {0, 0, 0, 0, 0}, /* padding to make it 1-based */
      {0, 0, 0, 0, 0, 0}, {0, (1.0 / 5), 0, 0, 0, 0}, {0, (3.0 / 40), (9.0 / 40), 0, 0, 0},
      {0, (3.0 / 10), (-9.0 / 10), (6.0 / 5), 0, 0},
      {0, (-11.0 / 54), (5.0 / 2), (-70.0 / 27), (35.0 / 27), 0},
      {0, (1631.0 / 55296), (175.0 / 512), (575.0 / 13824), (44275.0 / 110592), (253.0 / 4096)}};
  public static final double[] C =
      new double[] {0, (37.0 / 378), 0, (250.0 / 621), (125.0 / 594), 0, (512.0 / 1771)};
  public static final double[] Cstar = new double[] {0, (2825.0 / 27648), 0, (18575.0 / 48384),
      (13525.0 / 55296), (277.0 / 14336), (1.0 / 4)};

  // seems WAY to small, but solves graphical issues
  public final static double DESIRED_ERROR = Math.pow(10, -14);

  // @author Rajan Troll
  public static double[] RK4(DoubleBinaryOperator f, double x, double y, double h, int steps) {
    double[] result = new double[steps + 1];
    result[0] = y;
    for (int i = 1; i <= steps; i++) {
      double halfStep = h / 2;
      double k1 = f.applyAsDouble(x, y);
      double k2 = f.applyAsDouble(x + halfStep, y + halfStep * k1);
      double k3 = f.applyAsDouble(x + halfStep, y + halfStep * k2);
      double k4 = f.applyAsDouble(x + h, y + h * k3);
      x += h;
      y += (h / 6) * (k1 + 2 * (k2 + k3) + k4);
      result[i] = y;
    }
    return result;
  }

  // @author Rajan Troll
  public static double RK4(DoubleBinaryOperator f, double x, double y, double h) {
    double halfStep = h / 2;
    double k1 = f.applyAsDouble(x, y);
    double k2 = f.applyAsDouble(x + halfStep, y + halfStep * k1);
    double k3 = f.applyAsDouble(x + halfStep, y + halfStep * k2);
    double k4 = f.applyAsDouble(x + h, y + h * k3);
    return y + (h / 6) * (k1 + 2 * (k2 + k3) + k4);
  }



  /**
   * controller method for RKF5 integration, uses adaptive step sizes
   * 
   * @param f the function
   * @param x starting x coordinate
   * @param y starting y coordinate
   * @param h step length
   * @return the new y coordinate based on the step length and the stating y coordinate ((y sub n+1)
   *         based on (y sub n))
   */
  public static double RKF5(DoubleBinaryOperator f, double x, double y, double h) {

    double[] k = getKArray(f, x, y, h);
    double error = getError(k);
    if (error != 0) { // assuming function is not exact
      h = h * Math.abs(fifthRoot(DESIRED_ERROR / error));
    } else {
      h *= 4; // attempt to speed up by increasing h when function is exact
    }
    k = getKArray(f, x, y, h);
    return y + C[1] * k[1] + C[2] * k[2] + C[3] * k[3] + C[4] * k[4] + C[5] * k[5] + C[6] * k[6];
  }

  /**
   * produces the k variables of the RKF5 integration tool
   * 
   * @param f the function to be evaluated
   * @param x X-coordinate of the integration start point
   * @param y Y-coordinate of the integration start point
   * @param h the step length of the integration
   * @return an array of the 6 k variables
   */
  public static double[] getKArray(DoubleBinaryOperator f, double x, double y, double h) {
    double[] k = new double[7];
    k[1] = h * f.applyAsDouble(x, y);
    k[2] = h * f.applyAsDouble(x + A[2] * h, y + B[2][1] * k[1]);
    k[3] = h * f.applyAsDouble(x + A[3] * h, y + B[3][1] * k[1] + B[3][2] * k[2]);
    k[4] = h * f.applyAsDouble(x + A[4] * h, y + B[4][1] * k[1] - B[4][2] * k[2] + B[4][3] * k[3]);
    k[5] = h * f.applyAsDouble(x + A[5] * h,
        y + B[5][1] * k[1] + B[5][2] * k[2] + B[5][3] * k[3] + B[5][4] * k[4]);
    k[6] = h * f.applyAsDouble(x + A[6] * h,
        y + B[6][1] * k[1] + B[6][2] * k[2] + B[6][3] * k[3] + B[6][4] * k[4] + B[6][5] * k[5]);
    return k;
  }

  /**
   * returns the error of the RKF5 Integration formula
   * 
   * @param k an array of 'k' variables, the key terms in the RKF5 evaluation
   * @return the error
   */
  public static double getError(double[] k) {
    double error = 0;
    for (int i = 1; i <= 6; i++) {
      error += (C[i] - Cstar[i]) * k[i];
    }
    return Math.abs(error);
  }

  /**
   * quickly approximates the fifth root of the parameter
   * 
   * @param x the value to be 5th-rooted
   * @return the fifth root of x
   * @author Rajan Troll
   */
  private static double fifthRoot(double x) {
    return (0.0133812 + x * (0.0413244 + x * (0.0162057 + (0.00124834 + 0.0000144931 * x) * x)))
        / (0.02112 + x * (0.0393412 + x * (0.011045 + (0.000609098 + 4.26539E-6 * x) * x)));
  }

  public static PointIterator IntegratorPath(DoubleBinaryOperator f, double x, double y, double h) {

    return new PointIterator() {
      double step = h;
      double newX = x;
      double newY = y;

      @Override
      public double getX() {
        return newX;
      }

      @Override
      public double getY() {
        return newY;
      }

      @Override
      public boolean advance() {
        newX += step;
        newY = RKF5(f, newX, newY, step); // RKF5 is more condensed vs. RK4
        return true; // change for invalid advance (on to asymptote)
      }

    };

  }

  private static double fastFourthRoot(double x) {
    return (0.00815176 + x * (0.57294 + x * (4.94898 + x * (8.37038 + 2.16289 * x))))
        / (0.0310471 + x * (1.15524 + x * (6.56725 + x * (7.31 + x))));
  }

  public static Cursor adaptiveRKC45Path(DoubleBinaryOperator f, double sx, double sy, double step,
      double err) {
    return new Integration.Cursor() {
      double x = sx, y = sy;
      double h = step;

      public double getX() {
        return x;
      }

      public double getY() {
        return y;
      }

      public void setPosition(double x, double y) {
        this.x = x;
        this.y = y;
      }

      private static final double a2 = 1.0 / 5, a3 = 3.0 / 10, a4 = 3.0 / 5, a5 = 1, a6 = 7.0 / 8;
      private static final double b21 = 1.0 / 5, b31 = 3.0 / 40, b32 = 9.0 / 40, b41 = 3.0 / 10,
          b42 = -9.0 / 10, b43 = 6.0 / 5, b51 = -11.0 / 54, b52 = 5.0 / 2, b53 = -70.0 / 27,
          b54 = 35.0 / 27, b61 = 1631.0 / 55296, b62 = 175.0 / 512, b63 = 575.0 / 13824,
          b64 = 44275.0 / 110592, b65 = 253.0 / 4096; // Numerical recepies
      private static final double c1 = 37.0 / 378, cs1 = 2825.0 / 27648, c2 = 0, cs2 = 0,
          c3 = 250.0 / 621, cs3 = 18575.0 / 48384, c4 = 125.0 / 594, cs4 = 13525.0 / 55296, c5 = 0,
          cs5 = 277.0 / 14336, c6 = 512.0 / 1771, cs6 = 1.0 / 4;
      private static final double cd1 = c1 - cs1, cd2 = c2 - cs2, cd3 = c3 - cs3, cd4 = c4 - cs4,
          cd5 = c5 - cs5, cd6 = c6 - cs6;
      private static final double S = 0.875;

      private static final double minRatio = 0.125 * 0.125 * 0.125 * 0.125;// 1/8^4
      private static final double maxRatio = 8 * 8 * 8 * 8 * 8;// 8^5


      double xC = 0, yC = 0;

      @Override
      public void moveX(double xd) {
        xd += x;
        while (x < xd) {
          while (true) {
            // System.out.println("step:"+ h);
            double k1 = h * f.applyAsDouble(x, y);
            double k2 = h * f.applyAsDouble(x + a2 * h, y + b21 * k1);
            double k3 = h * f.applyAsDouble(x + a3 * h, y + b31 * k1 + b32 * k2);
            double k4 = h * f.applyAsDouble(x + a4 * h, y + b41 * k1 + b42 * k2 + b43 * k3);
            double k5 =
                h * f.applyAsDouble(x + a5 * h, y + b51 * k1 + b52 * k2 + b53 * k3 + b54 * k4);
            double k6 = h * f.applyAsDouble(x + a6 * h,
                y + b61 * k1 + b62 * k2 + b63 * k3 + b64 * k4 + b65 * k5);

            double increment = c1 * k1 + c2 * k2 + c3 * k3 + c4 * k4 + c5 * k5 + c6 * k6;
            double error = abs(cd1 * k1 + cd2 * k2 + cd3 * k3 + cd4 * k4 + cd5 * k5 + cd6 * k6);
            // System.out.println(error);
            double desiredError = abs(increment) * err / xd; // What if predicted slope is 0, yet
                                                             // function isn't actually totally
                                                             // flat? Perhaps floor of 10^-9?
            // System.out.println("Desired:" + desiredError);
            double ratio = desiredError / error;
            if (error > desiredError) {
              if (ratio < minRatio)
                h *= S * 0.125;
              else
                h *= S * fastFourthRoot(ratio);
              if (xC + h == xC)
                throw new RuntimeException("Stepsize Underflow");
              continue;
            }
            // Kahan sum both x and y
            double dx = h - xC;
            double xt = x + dx;
            xC = (xt - x) - dx;
            x = xt;

            double dy = increment - yC;
            double yt = y + dy;
            yC = (yt - y) - dy;
            y = yt;
            h *= S * ((ratio >= maxRatio) ? 8 : fifthRoot(ratio));
            h = min(h, xd - x);
            break;
          }
        }
      }



      @Override
      public void moveY(double yd) {

      }
    };
  }

}

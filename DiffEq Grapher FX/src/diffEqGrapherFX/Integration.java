package diffEqGrapherFX;
 
import java.util.function.DoubleBinaryOperator;
 
public class Integration
{
	//the four parameter arrays are for the runge-kutta-feldmann 5th order integration math
	//these are the Cash-Karp values
	//D is C*, C* is an invalid variable name
	//all arays have a padding row/column to make the arrays 1-based for readability
	public static final double [] A = new double[]{0, 0, (1.0/5), (3.0/10), (3.0/5), 1, (7.0/8)};
	public static final double [][] B = new double [][] {
															{0, 0, 0, 0, 0}, /*padding to make it 1-based*/
															{0, 0, 0, 0, 0, 0},
															{0, (1.0/5), 0, 0, 0, 0},
															{0, (3.0/40), (9.0/40), 0, 0, 0},
															{0, (3.0/10), (-9.0/10), (6.0/5), 0, 0},
															{0, (-11.0/54), (5.0/2), (-70.0/27), (35.0/27), 0},
															{0, (1631.0/55296), (175.0/512), (575.0/13824), (44275.0/110592), (253.0/4096)}
	};
	public static final double [] C = new double[]{0, (37.0/378), 0, (250.0/621), (125.0/594), 0, (512.0/1771)};
	public static final double [] D = new double[]{0, (2825.0/27648), 0, (18575.0/48384), (13525.0/55296), (277.0/14336), (1.0/4)};
	
	//seems WAY to small, but solves graphical issues
	public final static double DESIRED_ERROR = Math.pow(10, -20);
	
	public static double[] RK4(DoubleBinaryOperator f, double x, double y, double h, int steps)
    {
		double[] result = new double[steps + 1];
        result[0] = y;
        for(int i = 1; i <= steps; i++)
        {
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
       
    public static double RK4(DoubleBinaryOperator f, double x, double y, double h)
    {
    	double halfStep = h / 2;
        double k1 = f.applyAsDouble(x, y);
        double k2 = f.applyAsDouble(x + halfStep, y + halfStep * k1);
        double k3 = f.applyAsDouble(x + halfStep, y + halfStep * k2);
        double k4 = f.applyAsDouble(x + h, y + h * k3);
        return y + (h / 6) * (k1 + 2 * (k2 + k3) + k4);
    }
    
    //uses the Runge-Kutta-Feldman 5th order integration equations
    //Uses adaptive stepsizes
    public static double RKF5(DoubleBinaryOperator f ,double x, double y, double h){
    	
    	double[] k = getKArray(f, x, y, h);
    	double error = getError(k);
    	h = h * Math.abs(fifthRoot(DESIRED_ERROR / error));
    	k = getKArray(f, x, y, h);
    	return y + C[1]*k[1] + C[2]*k[2] + C[3]*k[3] + C[4]*k[4] + C[5]*k[5] + C[6]*k[6];
    }
    
    public static double[] getKArray(DoubleBinaryOperator f, double x, double y, double h){
    	double[] k = new double[7];
    	k[1] = h * f.applyAsDouble(x,y);
    	k[2] = h * f.applyAsDouble(x + A[2] * h, y + B[2][1] * k[1]);
    	k[3] = h * f.applyAsDouble(x + A[3] * h, y + B[3][1] * k[1] + B[3][2] * k[2]);
    	k[4] = h * f.applyAsDouble(x + A[4] * h, y + B[4][1] * k[1] - B[4][2] * k[2] + B[4][3] * k[3]);
    	k[5] = h * f.applyAsDouble(x + A[5] * h, y + B[5][1] * k[1] + B[5][2] * k[2] + B[5][3] * k[3]
    			+ B[5][4] * k[4]);
    	k[6] = h * f.applyAsDouble(x + A[6] * h, y + B[6][1] * k[1] + B[6][2] * k[2]
    			+ B[6][3] * k[3] + B[6][4] * k[4] + B[6][5] * k[5]);
    	return k;
    }
    
    public static double getError(double[] k){
    	double error = 0;
    	for(int i = 1; i <= 6; i++){
    		error += (C[i] - D[i]) * k[i];
    	}
    	return error;
    }
    
    private static double fifthRoot(double x)
    {
    	return (0.0133812 + x *(0.0413244 + x *(0.0162057 + (0.00124834 + 0.0000144931* x) *x)))/(0.02112 + x *(0.0393412 + x *(0.011045 + (0.000609098 + 4.26539E-6 *x)* x)));
    }
    
    public static PointIterator IntegratorPath(DoubleBinaryOperator f, double x, double y, double h){
    	return new PointIterator(){
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
	    		newY = RKF5(f, newX, newY, step);
				return true; //change for invalid advance (on to asymptote)
			}
    		
    	};
    	
    }
       
 
}
package input;
import java.util.function.DoubleBinaryOperator;
public class Expression5 implements DoubleBinaryOperator
{
	public double applyAsDouble(double x, double y)
	{
		return Math.sin(x)/(y*x);
	}
}

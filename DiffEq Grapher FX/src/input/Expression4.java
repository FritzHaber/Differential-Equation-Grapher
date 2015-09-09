package input;
import java.util.function.DoubleBinaryOperator;
public class Expression4 implements DoubleBinaryOperator
{
	public double applyAsDouble(double x, double y)
	{
		return Math.asin(x)*y;
	}
}

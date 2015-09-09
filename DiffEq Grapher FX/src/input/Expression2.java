package input;
import java.util.function.DoubleBinaryOperator;
public class Expression2 implements DoubleBinaryOperator
{
	public double applyAsDouble(double x, double y)
	{
		return Math.sin(y);
	}
}

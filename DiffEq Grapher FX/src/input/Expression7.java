package input;
import java.util.function.DoubleBinaryOperator;
public class Expression7 implements DoubleBinaryOperator
{
	public double applyAsDouble(double x, double y)
	{
		return Math.sin(x)/(Math.cos(x))*Math.tan(x);
	}
}

package input;
import java.util.function.DoubleBinaryOperator;
import static java.lang.Math.*;
public class Expression implements DoubleBinaryOperator
{
	public double applyAsDouble(double x, double y)
	{
		return y;
	}
}

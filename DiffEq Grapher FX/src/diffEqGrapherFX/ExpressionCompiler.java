package diffEqGrapherFX;

import java.util.function.DoubleBinaryOperator;

public class ExpressionCompiler implements Runnable {

	private Thread t;
	private String expression;
	private DoubleBinaryOperator f;
	
	public ExpressionCompiler(String expression){
		this.expression = expression;
	}
	@Override
	public void run() {
		f = Expressions.compile(expression);

	}
	
	public void start(){
		if(t == null){
			t = new Thread(this);
			t.start();
		}
	}
	
	public DoubleBinaryOperator getDBO(){
		return f;
	}

}

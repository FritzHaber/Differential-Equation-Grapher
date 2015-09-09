package fxTest;

import java.util.function.DoubleBinaryOperator;

import diffEqGrapherFX.Expressions;

/**
 * @author Hank O'Brien
 *
 */
public class FXTest{
	
	public static void main(String[] args){
		DoubleBinaryOperator f1 = Expressions.compile("x");
		DoubleBinaryOperator f2 = Expressions.compile("2x");
		
		System.out.println(f1.applyAsDouble(1, 0));
		System.out.println(f2.applyAsDouble(1, 0));


	}

}

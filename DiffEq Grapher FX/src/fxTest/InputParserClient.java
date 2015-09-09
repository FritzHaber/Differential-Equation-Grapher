package fxTest;

import diffEqGrapherFX.InputParser;

public class InputParserClient {

	public static void main(String[] args){
		String eq = "";
//		eq = "(x^5 + 6x^2 - 10x + 9)/(2y^4 + 4y^2 + 10)ln(x)+2";
//		eq = "yln(x^2)";
//		eq = "(cos(x))^sin(y)";
//		eq = "e^x^2";
//		eq = "ln(ln(x))"
//		eq = "sin(sin(x))";
//		eq = "ln(sin(x^(e*cos(y)))";
//		eq = "x^x^x^x";
//		eq = "x^(x)(y)";
		System.out.println(InputParser.parse(eq));
		
	}
}

package diffEqGrapherFX;

import java.util.function.DoubleBinaryOperator;

/**
 * This class converts natural language input into machine readable math formats, such as (x)(y) goes to (x)*(y)
 * This applies to trig as well, as now sin(x) is replaced with Math.sin(x)
 * this lets other code use things like DoubleBinaryOperator.applyAsDouble() on user input
 * 
 * @author Hank O'Brien
 **/
public class InputParser {
	//TODO exponents, e^x, constants (e, pi, etc)
	private static String inputEq = "(5)^(6)*7";
	private String processedEq;
	private static char var1 = 'x';
	private static char var2 = 'y';
	
	public InputParser(String rawInput){
		this.inputEq = rawInput;
		parse();
	}
	
	public static void main(String[] args){
		parse();
		System.out.println(inputEq);
	}
	
	private static void parse(){
		parseImplicitMultiplication();
		parseTrig();
		parseExponents();
	}
	
	private static void parseImplicitMultiplication() {
		stage1Parse();
		stage2Parse();
		stage3Parse();		
	}
	//has issues with multiple consecutive parenthesis (2*(3))^(8)
	//if user input is mathematically correct, the only characters that can border the ^ sign is (, ), or the digits
	private static void parseExponents() {
		//assume ^ is not at index 0, or at index length
		if(inputEq.contains("^")){
			int i = inputEq.indexOf("^");
			int firstMarker = i-1;

			if(inputEq.charAt(i-1) == ')'){
				int parenCount = 0;
				do{
					if(inputEq.charAt(firstMarker) == ')'){
						parenCount++;
					}
					else if(inputEq.charAt(firstMarker) == '('){
						parenCount--;
					}
					firstMarker--;
					//compensates for if the firstMarker ends at position 0, the compensation below
					//over compensates in this case
					if(firstMarker == 0)
						firstMarker--;
				}while(parenCount > 0 && firstMarker > 0);
				//compensates for additional decrement before loop termination if firstMarker does not end at 0
				firstMarker++;
			}else{ //assume number in this case?
				while(firstMarker > 0 && Character.isDigit(inputEq.charAt(firstMarker))){
					firstMarker--;
				}
				
			}
			
			
			int lastMarker = i+1;
			if(inputEq.charAt(lastMarker) == '('){
				int parenCount = 0;
				do{
					if(inputEq.charAt(lastMarker) == ')'){
						parenCount--;
					}
					if(inputEq.charAt(lastMarker) == '('){
						parenCount++;
					}
					lastMarker++;
				}while(parenCount > 0 && lastMarker < inputEq.length());
				lastMarker--;
			}else{ //assume number
				while(lastMarker < inputEq.length() && Character.isDigit(inputEq.charAt(lastMarker))){
					lastMarker++;
				}
			}
			
			String nonExpFirst = inputEq.substring(0,firstMarker);
			String nonExpLast = inputEq.substring(lastMarker, inputEq.length());
			String term = inputEq.substring(firstMarker, i);
			String power = inputEq.substring(i+1, lastMarker);
			inputEq = nonExpFirst + "Math.pow(" + term + ","  + power + ")" + nonExpLast;
		}
		
	}

	private static int getPrecedence(char c) {
		//order of precedence follows Java's operator precedence order
		if(c == '(' || c == ')')
			return 3;
		else if(c == '*' || c == '/' || c == '%')
			return 2;
		else if(c == '+' || c == '-')
			return 1;
		else
			//case for c is a number, hence not an operator
			return 4;
	}

	private static void parseTrig() {
		inputEq = inputEq.replace("arcsin*(", "Math.asin(");
		inputEq = inputEq.replace("arccos*(", "Math.acos(");
		inputEq = inputEq.replace("arctan*(", "Math.atan(");
		//multiplication sign because multiplication parse things sin is multiplied by (, so it adds a multiplication symbol
		inputEq = inputEq.replace("sin*(", "Math.sin(");
		inputEq = inputEq.replace("cos*(", "Math.cos(");
		inputEq = inputEq.replace("tan*(", "Math.tan(");
	}

	//parses 6x to 6*x
	private static void stage3Parse() {
		for(int i = 1; i < inputEq.length(); i++){
			if(Character.isDigit(inputEq.charAt(i)) && isVariable(inputEq.charAt(i-1))){
				inputEq = addMultiplicationSymbol(inputEq, i);
			}
			if(Character.isDigit(inputEq.charAt(i-1)) && isVariable(inputEq.charAt(i))){
				inputEq = addMultiplicationSymbol(inputEq, i);
			}
			if(isVariable(inputEq.charAt(i)) && isVariable(inputEq.charAt(i-1))){
				inputEq = addMultiplicationSymbol(inputEq, i);
			}
		}
	}


	private static boolean isVariable(char c) {
		if(c == var1 || c == var2){
			return true;
		}
		return false;
	}

	private static String addMultiplicationSymbol(String s, int i) {
		String s1 = s.substring(0, i);
		String s2 = s.substring(i);
		return s = s1 + '*' + s2;
		
	}

	//parses (x)(y) to (x)*(y), adds the multiplication symbol
	private static void stage1Parse(){
		for(int i = 1; i < inputEq.length(); i++){
			if(inputEq.charAt(i - 1) == ')' && inputEq.charAt(i) == '('){
				inputEq = addMultiplicationSymbol(inputEq, i);
			}
		}
	}
	
	//parses 6(x) to 6*(x), adds multiplication sign between non-operators and numbers
	private static void stage2Parse(){
		for(int i = 1; i < inputEq.length(); i++){
			if(inputEq.charAt(i) == '(' && (Character.isDigit(inputEq.charAt(i-1)) || !isAnOpertor(inputEq.charAt(i-1)))){
				inputEq = addMultiplicationSymbol(inputEq, i);
			}
			if(inputEq.charAt(i-1) == ')' && (Character.isDigit(inputEq.charAt(i)) || !isAnOpertor(inputEq.charAt(i)))){
				inputEq = addMultiplicationSymbol(inputEq, i);
			}
		}
	}
	
	private static boolean isAnOpertor(char c) {
		String operators = "+-*/%^";
		return operators.contains(c + "");
	}

	public String getProcessedString(){
		return processedEq;
	}
}

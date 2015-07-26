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
	private static String inputEq = "5^6";
	private String processedEq;
	private static char var1 = 'x';
	private static char var2 = 'y';
	
	public InputParser(String rawInput){
		this.inputEq = rawInput;
		parse();
	}
	
	public static void main(String[] args){
		//parse();
		parseExponents();
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

	//if user input is mathematically correct, the only characters that can border the ^ sign is (, ), or the digits
	private static void parseExponents() {
		//assume ^ is not at index 0, or at index length
		if(inputEq.contains("^")){
			int i = inputEq.indexOf("^");
			int firstMarker = i;
			if(inputEq.charAt(i-1) == ')'){
				while(inputEq.charAt(firstMarker) != '('){
					firstMarker--;
				}
				firstMarker++;
			}else{ //assume number in this case?
				while(Character.isDigit(inputEq.charAt(firstMarker))){
					firstMarker--;
				}
			}
			int lastMarker = i;
			if(inputEq.charAt(lastMarker) == '('){
				while(inputEq.charAt(lastMarker) != ')'){
					lastMarker++;
				}
			}else{ //assume number
				while(Character.isDigit(inputEq.charAt(lastMarker))){
					lastMarker++;
				}
			}
			System.out.println(" i = " + i + "|firstMarker = " + firstMarker + "|lastMarker = " + lastMarker);
			String temp = inputEq.substring(0, firstMarker-1);
			temp = temp + "Math.pow(" + inputEq.substring(firstMarker-1, i) + "," + inputEq.substring(i+1, lastMarker) + ")" + inputEq.substring(lastMarker);
			inputEq = temp;
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

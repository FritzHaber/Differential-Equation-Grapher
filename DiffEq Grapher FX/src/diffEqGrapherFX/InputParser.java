package diffEqGrapherFX;

/**
 * This class converts natural language input into machine readable math formats, such as (x)(y) goes to (x)*(y)
 * This applies to trig as well, as now sin(x) is replaced with Math.sin(x)
 * this lets other code use things like DoubleBinaryOperator.applyAsDouble() on user input
 * 
 * @author Hank O'Brien
 **/
public class InputParser {
	//TODO: exponents, e^x, constants (e, pi, etc)
	//TODO: logs
//	private static String inputEq = "(5)^(6)*7";
//	private String processedEq;
	private static char var1 = 'x';
	private static char var2 = 'y';
	
//	private InputParser(String rawInput){
//		this.inputEq = rawInput;
//		//parse();
//	}
	
//	public static void main(String[] args){
//		//parse();
//		//System.out.println(inputEq);
//	}
	
	//used externally
	public static String parse(String natEq){
		natEq = parseImplicitMultiplication(natEq);
		natEq = parseTrig(natEq);
		natEq = parseExponents(natEq);
		return natEq;
	}
	
	private static String parseImplicitMultiplication(String natEq) {
		natEq = stage1Parse(natEq);
		natEq = stage2Parse(natEq);
		natEq = stage3Parse(natEq);	
		return natEq;
	}
	//has issues with multiple consecutive parenthesis (2*(3))^(8)
	//if user input is mathematically correct, the only characters that can border the ^ sign is (, ), or the digits
	private static String parseExponents(String natEq) {
		//assume ^ is not at index 0, or at index length
		if(natEq.contains("^")){
			int i = natEq.indexOf("^");
			int firstMarker = i-1;

			if(natEq.charAt(i-1) == ')'){
				int parenCount = 0;
				do{
					if(natEq.charAt(firstMarker) == ')'){
						parenCount++;
					}
					else if(natEq.charAt(firstMarker) == '('){
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
				while(firstMarker > 0 && Character.isDigit(natEq.charAt(firstMarker))){
					firstMarker--;
				}
				
			}
			
			
			int lastMarker = i+1;
			if(natEq.charAt(lastMarker) == '('){
				int parenCount = 0;
				do{
					if(natEq.charAt(lastMarker) == ')'){
						parenCount--;
					}
					if(natEq.charAt(lastMarker) == '('){
						parenCount++;
					}
					lastMarker++;
				}while(parenCount > 0 && lastMarker < natEq.length());
				lastMarker--;
			}else{ //assume number
				while(lastMarker < natEq.length() && Character.isDigit(natEq.charAt(lastMarker))){
					lastMarker++;
				}
			}
			
			String nonExpFirst = natEq.substring(0,firstMarker);
			String nonExpLast = natEq.substring(lastMarker, natEq.length());
			String term = natEq.substring(firstMarker, i);
			String power = natEq.substring(i+1, lastMarker);
			natEq = nonExpFirst + "Math.pow(" + term + ","  + power + ")" + nonExpLast;
		}
		return natEq;
	}

//	private static int getPrecedence(char c) {
//		//order of precedence follows Java's operator precedence order
//		if(c == '(' || c == ')')
//			return 3;
//		else if(c == '*' || c == '/' || c == '%')
//			return 2;
//		else if(c == '+' || c == '-')
//			return 1;
//		else
//			//case for c is a number, hence not an operator
//			return 4;
//	}

	private static String parseTrig(String natEq) {
		natEq = natEq.replace("arcsin*(", "Math.asin(");
		natEq = natEq.replace("arccos*(", "Math.acos(");
		natEq = natEq.replace("arctan*(", "Math.atan(");
		//multiplication sign because multiplication parse things sin is multiplied by (, so it adds a multiplication symbol
		natEq = natEq.replace("sin*(", "Math.sin(");
		natEq = natEq.replace("cos*(", "Math.cos(");
		natEq = natEq.replace("tan*(", "Math.tan(");
		return natEq;
	}

	//parses 6x to 6*x
	private static String stage3Parse(String natEq) {
		for(int i = 1; i < natEq.length(); i++){
			if(Character.isDigit(natEq.charAt(i)) && isVariable(natEq.charAt(i-1))){
				natEq = addMultiplicationSymbol(natEq, i);
			}
			if(Character.isDigit(natEq.charAt(i-1)) && isVariable(natEq.charAt(i))){
				natEq = addMultiplicationSymbol(natEq, i);
			}
			if(isVariable(natEq.charAt(i)) && isVariable(natEq.charAt(i-1))){
				natEq = addMultiplicationSymbol(natEq, i);
			}
		}
		return natEq;
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
	private static String stage1Parse(String natEq){
		for(int i = 1; i < natEq.length(); i++){
			if(natEq.charAt(i - 1) == ')' && natEq.charAt(i) == '('){
				natEq = addMultiplicationSymbol(natEq, i);
			}
		}
		return natEq;
	}
	
	//parses 6(x) to 6*(x), adds multiplication sign between non-operators and numbers
	private static String stage2Parse(String natEq){
		for(int i = 1; i < natEq.length(); i++){
			if(natEq.charAt(i) == '(' && (Character.isDigit(natEq.charAt(i-1)) || !isAnOpertor(natEq.charAt(i-1)))){
				natEq = addMultiplicationSymbol(natEq, i);
			}
			if(natEq.charAt(i-1) == ')' && (Character.isDigit(natEq.charAt(i)) || !isAnOpertor(natEq.charAt(i)))){
				natEq = addMultiplicationSymbol(natEq, i);
			}
		}
		return natEq;
	}
	
	private static boolean isAnOpertor(char c) {
		String operators = "+-*/%^";
		return operators.contains(c + "");
	}

//	public String getProcessedString(){
//		return processedEq;
//	}
}

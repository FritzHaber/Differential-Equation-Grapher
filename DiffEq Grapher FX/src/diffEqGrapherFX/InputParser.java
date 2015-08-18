package diffEqGrapherFX;

/**
 * This class converts natural language input into machine readable math formats, such as (x)(y) goes to (x)*(y)
 * This applies to trig as well, as now sin(x) is replaced with Math.sin(x)
 * this lets other code use things like DoubleBinaryOperator.applyAsDouble() on user input
 * 
 * @author Hank O'Brien
 **/
public class InputParser {
	private static char var1 = 'x';
	private static char var2 = 'y';
	
	
	//used externally
	public static String parse(String natEq){
		natEq = natEq.replaceAll("\\s",""); //removes whitespace
		String temp = natEq;

		temp = parseConstants(temp);
		temp = parseTrig(temp);
		temp = parseLogs(temp);
		temp = parseAbs(temp);
		do{
			natEq = temp;
			temp = parseImplicitMultiplication(temp);
			temp = parseExponents(temp);
		}while(!natEq.equals(temp));

		return temp;
	}
	
	private static String parseAbs(String natEq) {
		natEq = natEq.replace("abs(", "Math.abs(");
		return natEq;
	}

	private static String parseLogs(String natEq) {
		natEq = natEq.replace("log(", "Math.log10(");
		natEq = natEq.replace("ln(", "Math.log(");
		return natEq;
	}

	//handles e and pi
	private static String parseConstants(String natEq) {
		natEq =  natEq.replace("pi", "(" + Math.PI + ")");
		natEq = natEq.replace("e", "(" + Math.E + ")");
		return natEq;
	}

	private static String parseImplicitMultiplication(String natEq) {
		natEq = stage1Parse(natEq);
		natEq = stage2Parse(natEq);
		natEq = stage3Parse(natEq);	
		return natEq;
	}

	//if user input is mathematically correct, the only characters that can border the ^ sign is (, ), or the digits, or trig functions
	private static String parseExponents(String natEq) {
		//assume ^ is not at index 0, or at index length
		if(natEq.contains("^")){
			int i = natEq.lastIndexOf("^");
			
			//term below power
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
					if(parenCount == 0){
						if(Character.isAlphabetic(natEq.charAt(firstMarker))){
							while(natEq.charAt(firstMarker) != 'M'){
								firstMarker--;
							}
							firstMarker--;
						}
					}
				}while(parenCount > 0 && firstMarker > 0);
				//compensates for additional decrement before loop termination if firstMarker does not end at 0
				firstMarker++;
			}else if(natEq.charAt(firstMarker) == var1 || natEq.charAt(firstMarker) == var2){ //case of x^3 or y^3
				//do nothing, firstMarker is at a good location
			}else{ //assume number in this case?
				while(firstMarker > 0 && isDigit(natEq.charAt(firstMarker))){
					firstMarker--;
				}
				firstMarker++;
			}
			
			//power
			int endMarker = i+1;
			if(natEq.charAt(endMarker) == '('){
				int parenCount = 0;
				do{
					if(natEq.charAt(endMarker) == ')'){
						parenCount--;
					}
					if(natEq.charAt(endMarker) == '('){
						parenCount++;
					}
					endMarker++;
				}while(parenCount > 0 && endMarker < natEq.length());
				endMarker--;
			}else if(natEq.charAt(endMarker) == var1 || natEq.charAt(endMarker) == var2){
				endMarker = i + 2;
			}else if(natEq.charAt(endMarker) == 'M'){ //possibly means exponent or log power
				while(natEq.charAt(endMarker) != ')'){
					endMarker++;
				}
			}else{ //assume number
				while(endMarker < natEq.length() && isDigit(natEq.charAt(endMarker))){
					endMarker++;
				}
			}
			
			String nonExpFirst = natEq.substring(0,firstMarker);
			String nonExpLast = natEq.substring(endMarker, natEq.length());
			String term = natEq.substring(firstMarker, i);
			String power = natEq.substring(i+1, endMarker);
			natEq = nonExpFirst + "Math.pow(" + term + ","  + power + ")" + nonExpLast;
		}
		return natEq;
	}

	
	//need to have the decimal case, because decimals are always part of a number
	private static boolean isDigit(char c) {
		if(Character.isDigit(c) || c == '.')
			return true;
		else
			return false;
	}

	private static String parseTrig(String natEq) {
		natEq = natEq.replace("arcsin(", "Math.asin(");
		natEq = natEq.replace("arccos(", "Math.acos(");
		natEq = natEq.replace("arctan(", "Math.atan(");
		natEq = natEq.replace("sin(", "Math.sin(");
		natEq = natEq.replace("cos(", "Math.cos(");
		natEq = natEq.replace("tan(", "Math.tan(");
		return natEq;
	}

	//parses 6x to 6*x
	private static String stage3Parse(String natEq) {
		for(int i = 1; i < natEq.length(); i++){
			if(isDigit(natEq.charAt(i)) && isVariable(natEq.charAt(i-1))){		//2x -> 2*x
				natEq = addMultiplicationSymbol(natEq, i);
			}
			if(isVariable(natEq.charAt(i)) && isDigit(natEq.charAt(i-1))){		//x2 -> x*2
				natEq = addMultiplicationSymbol(natEq, i);
			}
			if(isVariable(natEq.charAt(i)) && isVariable(natEq.charAt(i-1))){	//xy -> x*y
				natEq = addMultiplicationSymbol(natEq, i);
			}
			if(isVariable(natEq.charAt(i)) && natEq.charAt(i - 1) == 'M'){
				natEq = addMultiplicationSymbol(natEq,i);
			}
			if(natEq.charAt(i) == 'M' && isVariable(natEq.charAt(i - 1))){
				natEq = addMultiplicationSymbol(natEq,i);
			}
			if(isDigit(natEq.charAt(i)) && natEq.charAt(i - 1) == 'M'){
				natEq = addMultiplicationSymbol(natEq,i);
			}
			if(natEq.charAt(i) == 'M' && isDigit(natEq.charAt(i - 1))){
				natEq = addMultiplicationSymbol(natEq,i);
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
	//M cases for Math.sin etc, enables processing of trig and log functions
	private static String stage2Parse(String natEq){
		for(int i = 1; i < natEq.length(); i++){
			if((isDigit(natEq.charAt(i-1)) || natEq.charAt(i-1) == 'M') && natEq.charAt(i) == '('){		//6(x) -> 6*(x)
				natEq = addMultiplicationSymbol(natEq, i);
			}
			if(natEq.charAt(i-1) == ')' && (isDigit(natEq.charAt(i)) || natEq.charAt(i) == 'M')){		//(x)6 -> (x)*6
				natEq = addMultiplicationSymbol(natEq, i);
			}
		}
		//fixes a small bug in the above code where a trig function was multiplied by another parenthesis, like cos(x)*)
		natEq = natEq.replace("(*(", "((");
		natEq = natEq.replace(")*)", "))");
		return natEq;
	}
	
}
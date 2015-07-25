package diffEqGrapherFX;
/**
 * This class converts natural language input into machine readable math formats, such as (x)(y) goes to (x)*(y)
 * This applies to trig as well, as now sin(x) is replaced with Math.sin(x)
 * 
 * 
 * @author Hank O'Brien
 **/
public class InputParser {
	//TODO exponents, e^x, and trig functions
	private static String inputEq = "sin(x)arccos(x)";
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
		stage1Parse();
		stage2Parse();
		stage3Parse();
		parseTrig();
		parseExponents();
	}
	
	private static void parseExponents() {
		if(inputEq.contains("^")){
			int i = inputEq.indexOf("^");
			
		}
		
	}

	private static void parseTrig() {
		inputEq = inputEq.replace("arcsin*(", "Math.asin(");
		inputEq = inputEq.replace("arccos*(", "Math.acos(");
		inputEq = inputEq.replace("arctan*(", "Math.atan(");
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

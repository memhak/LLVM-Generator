
import java.io.PrintWriter;

/**
 * This class analyzes single lines from the input and checks if there are any syntax errors.
 * 
 * @author Mehmet Hakan Kurtoglu
 */
public class SyntaxAnalyzer {
	
	/**
	 * Default constructor
	 */
	public SyntaxAnalyzer(){}
	
	/**
	 * This checks if a single line from the input file, represented as a String has syntax errors and if there are, 
	 * prints it to the output file along with line number.
	 * 
	 * @param inputLine		line from the input file
	 * @param fw			FileHandlerClass for printing errors if existing
	 * @return				true if it has errors, false otherwise
	 */
	boolean hasErrors(String inputLine,FileHandlerClass fw){
		int lineLength = inputLine.length();
		int numLeftPar=0;
		int numRightPar=0;
		char c0,c,c1;
		/*
		 * Input line, represented as a String, is checked index by index if it meets syntax criteria. 
		 * That is, if there is any extra parenthesis or there are any adjacent operator signs.
		 */
		for(int i=0;i<lineLength;i++){
			c=inputLine.charAt(i);
			if(c=='(')
				numLeftPar++;
			else if(c==')')
				numRightPar++;
			// if the inspected character is an operator
			else if(c=='+'||c=='-'||c=='/'||c=='*'||c=='='){
				// if the next character is also an operator, there is an error 
				if(i==0){ // if inspected character is at the first index
					c1=inputLine.charAt(i+1);
					if(c1=='+'||c1=='-'||c1=='/'||c1=='*'||c=='='){
						fw.output.println("Error: Line "+fw.lineNumb+": extra operator");
						return true;
					}
				}else if(i==lineLength-1){ // if inspected character is at the last index
					c0=inputLine.charAt(i-1);
					if(c0=='+'||c0=='-'||c0=='/'||c0=='*'||c=='='){
						fw.output.println("Error: Line "+fw.lineNumb+": extra operator");
						return true;
					}
				}else{ // for other cases of inspected character
					c0=inputLine.charAt(i-1);
					c1=inputLine.charAt(i+1);
					if(c0=='+'||c0=='-'||c0=='/'||c0=='*'||c0=='='||c1=='+'||c1=='-'||c1=='/'||c1=='*'||c1=='='){
						fw.output.println("Error: Line "+fw.lineNumb+": extra operator");
						return true;
					}
				}
			}
		}
		// If number of right and left parentheses isn't the same, then there is an error
		if(numRightPar!=numLeftPar){
			fw.output.println("Error: Line "+fw.lineNumb+": missing parenthesis");
			return true;
		}
		return false;
	}
	
}

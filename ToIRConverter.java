
import java.util.Stack;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * This produces the corresponding LLVM intermediate representation from the input assignment statements
 * 
 * @author Mehmet Hakan Kurtoglu
 */
public class ToIRConverter {
	/**
	 * This is for syntax checking before convertion
	 */
	public SyntaxAnalyzer sa;
	/**
	 * This holds the variable names, represented as Strings
	 */
	public ArrayList<String> varList;
	/**
	 * This number holds the operation number that will be used while the convertion.
	 * It represents the temporary variables used in the intermediate representation
	 */
	public int operationCounter;
	
	/**
	 * Default constructor, initializes fields and sets the operation counter to 1.
	 */
	public ToIRConverter(){
		sa=new SyntaxAnalyzer();
		varList=new ArrayList<String>();
		operationCounter=1; 
	}
	
	/**
	 * This converts the line to the intermediate representation, prints the representation to the output file. 
	 * If undeclared variable is found, it returns false
	 * 
	 * @param line		input line, represented as String
	 * @param fw		FileHandlerClass for output file
	 * @return			true if the operation is succesful, false otherwise, that is when undeclared variable is found
	 */
	boolean convertLine(String line, FileHandlerClass fw){
		// each blank in the input is removed
		line=line.replaceAll("\\s",""); 
		// This holds the left hand side of the input statement, that is, variable
		String var="";
		// This holds the right hand side of the input statement, that is, expression
		String expression="";
		// If line has errors no convertion is done, convertion fails and returns false
		if(sa.hasErrors(line, fw))
			return false;
		// This splits right and left hand sides of the statement, into a variable and an expression
		if(line.contains("=")){
			int i=line.indexOf("=");
			var=line.substring(0, i);
			expression=line.substring(i+1);
			// variable is added into the variable list
			if(!varList.contains(var)){
				varList.add(var);
				fw.output.println("%"+var+" = alloca i32");
			}
		}else{ // if there is no equals sign, then whole line is an expression
			expression=line;
		}
		// If expression is numeric, that is, it doesn't have any arithmetic operation, then intermediate representation 
		// is printed. Numericness is checked using a regex 
		if(!var.equals("")&&expression.matches("[-+]?\\d*\\.?\\d+")){
			fw.output.println("store i32 "+expression+", i32* %"+var);
			return true;
		}else{ // else, expression is first converted into postfix notation and corresponding LLVM representation is printed
			return postfixInterpreter(InfixToPostfix(expression),fw,var);
		}
	}
	
	/**
	 * This converts the expression, specified as String, into the postfix notation. Then postfix representation is stored in 
	 * an ArrayList that contains each operator and operator represented as String. Postfix convertion is done using a stack, 
	 * and each operand, that is token, is represented as String
	 * 
	 * @param infixString		expression in the infix notation
	 * @return					corresponding postfix notation, stored as ArrayList of Strings
	 */
	ArrayList<String> InfixToPostfix(String infixString){
		// postfix notation form is represented as an ArrayList
		ArrayList<String> postfix=new ArrayList<String>();
		// a stack is used for postfix convertion
		Stack<String> postfixStack = new Stack<String>();
		// infix form is read using a scanner
		Scanner stringScanner = new Scanner(infixString);
		int length=infixString.length();
		String token; 
		char c;
		// each character is inspected, index by index
		for(int i=0;i<length;i++){
			c=infixString.charAt(i);
			if(c==' ')continue; // if blank characters is found, for loop continues
			token=Character.toString(c); // token represents operands and operators, as a String
			if(!token.equals("+")&&!token.equals("-")&&!token.equals("*")&&!token.equals("/")&&!token.equals("(")&&!token.equals(")")){
				int a=i+1;char c1;
				postfix.add(token);
				// if the inspected character is the initial of an operand, this concatenates consequent characters 
				// to the token string
				while(a!=length){ // if next character isn't at last index
					c1=infixString.charAt(a);
					if(c1!=' '&&c1!='+'&&c1!='-'&&c1!='*'&&c1!='/'&&c1!='('&&c1!=')'){
						postfix.set(postfix.size()-1, postfix.get(postfix.size()-1)+c1);
						a++;
					}else 
						break;
				}
				i=a-1;
				continue;
			}// if stack is empty or top token is a right parenthesis, upcoming token is pushed to the stack
			if(postfixStack.isEmpty()||postfixStack.peek().equals("(")){
				postfixStack.push(token);
				continue;
			}// if upcoming token is a right parenthesis, it's pushed to the stack
			if(token.equals("(")){
				postfixStack.push(token);
				continue;
			// if upcoming token is a left parenthesis, all stored tokens are popped until a right parenthesis is found
			}else if(token.equals(")")){
				while(!postfixStack.peek().equals("("))
					postfix.add(postfixStack.pop());
				postfixStack.pop();
				continue;
			}// multiplication and division operations have precedence over addition and subtraction
			if(token.equals("*")||token.equals("/")){
				// if top element in the stack is addition or subtraction, token is pushed to the stack
				if(postfixStack.peek().equals("+")||postfixStack.peek().equals("-")){
					postfixStack.push(token);
					continue;
				// else, top element is popped 
				}else if(postfixStack.peek().equals("*")||postfixStack.peek().equals("/")){
					postfix.add(postfixStack.pop());
					i--; // index is decremented and for loop moves to the next character
					continue;
				}
			// if token represents an addition or subtraction operation, it's directly added to the postfix notation
			}else if(token.equals("+")||token.equals("-")){
				postfix.add(postfixStack.pop());
				i--; // index is decremented and for loop moves to the next character
				continue;
			}
		}
		// all elements left in the stack is popped
		while(!postfixStack.isEmpty())
			postfix.add(postfixStack.pop());
		stringScanner.close();
		return postfix;
	}
	
	
	/**
	 * This interprets the expression in the postfix notational form and prints the corresponding intermediate representation.
	 * If undefined variable error is found, interpretation process stops and error is printed
	 * 
	 * @param postfix		expression in postfix notational form, represented as list of Strings
	 * @param fw			FileHandlerClass for printing operations
	 * @param var			variable side from the input line
	 * @return				true if operation is successful and no error is found, false otherwise
	 */
	boolean postfixInterpreter(ArrayList<String> postfix,FileHandlerClass fw,String var){
		String token="";
		// each token is inspected and corresponding representation line is printed
		for(int i=1;i<postfix.size();i++){
			token=postfix.get(i+1);
			if(token.equals("+")||token.equals("-")||token.equals("/")||token.equals("*")){
				// if operands are variables, the value they are pointing to is loaded in the intermediate representation
				if(postfix.get(i-1).charAt(0)<'0' || postfix.get(i-1).charAt(0)>'9'){
					// if starts with an '%', it designates IR operations and it can be thought as temporary variable
					if(postfix.get(i-1).charAt(0)!='%'){ 
						// if variable isn't defined previously, error is printed
						if(!varList.contains(postfix.get(i-1))){
							fw.output.println("Error: Line "+fw.lineNumb+": undefined variable "+postfix.get(i-1));
							return false;
						}
						fw.output.println("%"+operationCounter+" = load i32* %"+postfix.get(i-1));
						postfix.set(i-1, "%"+operationCounter);
						operationCounter++; // an operation in IR is done, thus number is incremented
					}
				}
				if(postfix.get(i).charAt(0)<'0' || postfix.get(i).charAt(0)>'9'){
					// if starts with an '%', it designates IR operations and it isn't a variable
					if(postfix.get(i).charAt(0)!='%'){
						// if variable isn't defined previously, error is printed
						if(!varList.contains(postfix.get(i))){
							fw.output.println("Error: Line "+fw.lineNumb+": undefined variable "+postfix.get(i));
							return false;
						}
						fw.output.println("%"+operationCounter+" = load i32* %"+postfix.get(i));
						postfix.set(i, "%"+operationCounter);
						operationCounter++; // an operation in IR is done, thus number is incremented
					}
				}
				// corresponding LLVM representation lines are printed according to the operation
				switch(token){
					// For different cases of operators, their IR is printed. The substitute variables are then 
					// added to the postfix list. Because an operation is done, its counter is incremented and because two 
					// operands of this operator is removed from the postfix list, index "i" is decremented twice.
					// Postfix size is 1, i.e. "i" cannot be decremented twice, it means interpretation is done and loop stops
					// Above definition is same for all different cases below
					case "+":
						fw.output.println("%"+operationCounter+" = add i32 "+postfix.get(i-1)+","+postfix.get(i));
						postfix.set(i-1, "%"+operationCounter);
						operationCounter++;
						postfix.remove(i);postfix.remove(i);
						if(postfix.size()==1)break;
						i-=2;continue;
					case "-":
						fw.output.println("%"+operationCounter+" = sub i32 "+postfix.get(i-1)+","+postfix.get(i));
						postfix.set(i-1, "%"+operationCounter);
						operationCounter++;
						postfix.remove(i);postfix.remove(i);
						if(postfix.size()==1)break;
						i-=2;continue;
					case "/":
						fw.output.println("%"+operationCounter+" = sdiv i32 "+postfix.get(i-1)+","+postfix.get(i));
						postfix.set(i-1, "%"+operationCounter);
						operationCounter++;
						postfix.remove(i);postfix.remove(i);
						if(postfix.size()==1)break;
						i-=2;continue;
					case "*":
						fw.output.println("%"+operationCounter+" = mul i32 "+postfix.get(i-1)+","+postfix.get(i));
						postfix.set(i-1, "%"+operationCounter);
						operationCounter++;
						postfix.remove(i);postfix.remove(i);
						if(postfix.size()==1)break;
						i-=2;continue;
				}
			}
		}// if there is no variable, value is printed. After corresponding IR is printed, returns true, 
		 // this means operation has been successful
		if(var.equals("")){
			if(postfix.get(0).charAt(0)=='%'){
				fw.output.println("call i32 (i8*, ...)* @printf(i8* getelementptr ([4 x i8]* @print.str, i32 0, i32 0), i32 "+postfix.get(0)+" )");
				operationCounter++; // printing is also an operation, counter is thus incremented
				return true;
			}else{
				fw.output.println("%"+operationCounter+" = load i32* %"+postfix.get(0));
				fw.output.println("call i32 (i8*, ...)* @printf(i8* getelementptr ([4 x i8]* @print.str, i32 0, i32 0), i32 %"+operationCounter+" )");
				operationCounter+=2; // printing is also an operation, counter is incremented once for printing and once for loading
				return true;
			}
		}// otherwise value is stored in the variable, and returns true, this means operation has been successful
		fw.output.println("store i32 "+postfix.get(0)+", i32* %"+var);
		return true;
	}
}

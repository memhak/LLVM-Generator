
import java.io.*;
import java.util.*;
/**
 * This class handles all operations regarding reading input from a file and printing output to another file. It has FileWriter and PrintWriter objects for printing output
 * and uses a Scanner for getting the input.
 * 
 * @author Mehmet Hakan Kurtoglu
 */
public class FileHandlerClass {
	
	// Fields for output
	public PrintWriter output;
	public FileWriter fw;
	// Input is read line by line
	public String inputLine;
	public Scanner input;
	// This indicates which line in the input file is being processed
	public int lineNumb;
		
	/**
	 * Constructor for this class, lineNumb is assigned the value of 1, that is first line is being processed.
	 * @param iFile				input file
	 * @param oFile				output file
	 * @throws IOException		if one of the I/O files doesn't exist
	 */
	public FileHandlerClass(File iFile,File oFile) throws IOException{
		input = new Scanner(iFile);
		fw = new FileWriter(oFile);
		output = new PrintWriter(fw);
		lineNumb=1;
	}
	
	/**
	 * This prints default header info to the output file.
	 */
	void printHeader(){
		output.println("; ModuleID = 'stm2ir'");
		output.println("declare i32 @printf(i8*, ...)");
		output.println("@print.str = constant [4 x i8] c\"%d\\0A\\00\"");
		output.println("\ndefine i32 @main() {");
	}
	
	/**
	 * Returns the next line from the input file
	 * @return		next line, represented as a String
	 */
	String getInputLine(){
		return input.nextLine();
	}
	
	/**
	 * This moves the cursor to the next line in the input file
	 */
	void moveToNextLine(){
		inputLine = input.nextLine();
	}
	
	/**
	 * This increments the line number, that indicates current line has been processed and moving to the next line
	 */
	void incrementLineNumb(){
		lineNumb++;
	}
	
	/**
	 * Flushes and closes I/O objects
	 */
	void close(){
		output.flush();
		input.close();
	}
	
}

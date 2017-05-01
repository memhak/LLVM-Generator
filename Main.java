
import java.io.*;
import java.util.Scanner;

/**
 * This converts the input to the corresponding LLVM intermediate representation. Main class contains the main loop, which 
 * reads input line by line and produces the corresponding intermediate representation
 * 
 * @author Mehmet Hakan Kurtoglu
 */
public class Main {
	
	public static void main(String[]args) throws IOException, 
		FileNotFoundException{
		
		// input file is passed as an argument and corresponding output file name is created 
		String inputFile=args[0];
		int i=inputFile.indexOf(".");
		String outputFile=inputFile.substring(0, i)+".ll";
		// fileHandler object will handle all reading input and printing output
		FileHandlerClass fileHandler=new FileHandlerClass(new File(inputFile),new File(outputFile)); 
		fileHandler.printHeader();
		
		// this is for convertion into the intermediate representation
		ToIRConverter irConvert = new ToIRConverter();
		
		String line;
		// Input is read and interpreted line by line
		while(fileHandler.input.hasNextLine()){
			fileHandler.moveToNextLine();
			line=fileHandler.inputLine;
			// If convertion operation fails, this returns false printing errors and program stops,
			// i/o writers are closed
			if(!irConvert.convertLine(line,fileHandler)){
				fileHandler.close();
				Scanner errorScanner=new Scanner(new File(outputFile));
				String error="";
				while(errorScanner.hasNextLine()){
					error=errorScanner.nextLine();
				}
				fileHandler=new FileHandlerClass(new File(inputFile),new File(outputFile));
				fileHandler.output.println(error);
				fileHandler.close();
				return;
			}
			// after this line is interpreted, line number is incremented
			fileHandler.incrementLineNumb();
		}
		fileHandler.output.println("ret i32 0\n}");
		fileHandler.close();
	}
	
	
}

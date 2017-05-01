Student ID:	2014400078
Student Name:	Mehmet Hakan Kurtoglu

Description:
	This program takes an input file consisting of elementary arithmetic operations,assignment statements and print statements, interprets it and produces the corresponding LLVM intermediate representation printing it into an output file. Input file is passed as an argument, its name is in the format, "<FILENAME>.stm" and the output file produced has the name "<FILENAME>.ll". 
	Program reads input file line by line. First, checks if the line has syntax errors, i.e., if there are any adjacent operators without any operands in between and if there any left parenthesis without its right counterpart and vice versa.
	Secondly, lines that don't contain any errors are taken into the convertion process. If no undefined variable is found during this process, corresponding LLVM representation is printed into the output file. 
	In the case of any error program stops and error info along with the line number is printed.
	Compile with command:
		javac *.java
	Program can executed done without makefile with command:
		java Main <INPUT_FILE>
	With makefile use:
		make
		./stm2ir <INPUT_FILE>

Program Architecture:
	Program has 4 classes, namely "Main", "FileHandlerClass", "SyntaxAnalyzer", "ToIRConverter" classes. In the program runtime classes interact with each other. Below fields are written in the format "<TYPE> <FIELD_NAME>" and methods are written in the formt "<RETURN_TYPE> <METHOD_NAME>".


1.Main Class:

	This class has the "main" method of the program. It takes input file name as "args[0]" and creates the corresponding output file name. It creates "FileHandlerClass" and "ToIRConverter" objects. IRConverter does the IR production and FileHandler object handles the output operations.

2.FileHandlerClass:

	This class mainly handles input/output operations. An instance of this class is passed to the other classes and they use this instance for output printing and input reading.Its fields are:
	-Printwriter output, FileWriter fw
		* FileWriter object "fw" is created with ouput file name, it has PrintWriter object "output". Output printing is done using this.
	-String inputLine
	-Scanner input
		* this is for reading from input
	-int lineNumb
		* storing the number of the line currently being read in the input file
	Methods of this class include:
	-default Constructor
		* initializes line number as one
	-void readHeader
		* prints header info for the output file
	-void moveToNextLine
		* moves the cursor of the input scanner
	-String getInputLine
		* returns current line from the input file
	-void incrementLineNumb
		* increments the line number
	-void close
		* flushes and closes writer objects

3.SyntaxAnalyzer
	
	This class checks the input lines for syntax errors. ToIRConverter class creates an instance of SyntaxAnalyzer and passes each input line to this instance before processing them. 
	This class has no fields.
	Methods of this class include:
	-default constructor
	-boolean hasErrors
		* this takes input lines in the String form, checks if they have errors and if an error is found, error info is printed and returns true, otherwise 
		  returns false. It checks for adjacent operators and missing parentheses

4.ToIRConverter

	This class mainly produces the intermediate representation. Before convertion it uses an instance of SyntaxAnalyzer for error checking, moreover this class checks if the arithmetic operations includes undefined variables as operands, if yes prints error info.
	Arithmetic operations in the input file are written in infix notational form, these are converted into postfix notation before convertion. 
	Fields of this class include:
	-SyntaxAnalyzer sa
		* this is for error checking
	-ArrayList<String> varList
		* this stores variables in the input file, variables are represented as String and this is used to check for undefined variable errors
	-int operationCounter
		* this represents the temporary variables used in the LLVM representation
	Methods of this class include:
	-default constructor
		* initializes operation counter as 1
	-boolean convertLine
		* this method first uses SyntaxAnalyzer for error checking and uses other methods to print corresponding LLVM intermediate representation 
	-ArrayList<String> InfixToPostfix
		* this takes arithmetic expressions in infix notation and transforms into postfix notation
	-boolean postfixInterpreter
		* this takes postfix expressions, interprets them and produces corresponding IR. If any undefined variable error is found this returns false and 
		  program stops, otherwise returns true. If error is found, it prints error info.
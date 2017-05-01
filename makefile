CLASSES = Main.java FileHandlerClass.java SyntaxAnalyzer.java ToIRConverter.java

default: $(CLASSES)
	javac $(CLASSES)
	rm -f stm2ir
	echo java Main $$\@ >> stm2ir
	chmod +x stm2ir

stm2ir: default
	java Main ${ARGS}

clean: 
	rm *.class

run: default
	./stm2ir ${ARGS}

.PHONY: run

main: Main.class

SyntacticAnalyzer.java: parser_gen.py gramatica
	python parser_gen.py < gramatica > SyntacticAnalyzer.java

Main.class: Project.java SyntacticAnalyzer.java
	cat Project.java SyntacticAnalyzer.java > Main.java
	javac Main.java

run: Main.class
	java Main

tests: Main.class
	./tests.py

clean:
	rm -f *.class Main.java SyntacticAnalyzer.java

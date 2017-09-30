#OBJ     = Project.class


#%.class: %.java
#	javac $?

#build: $(OBJ)

#run: $(OBJ)
#	java Project

#tests: $(OBJ)
## TODO


.PHONY: run
run:
	python parser_gen.py < gramatica > SyntacticAnalyzer.java
	cat Project.java SyntacticAnalyzer.java > run/Project.java
	cd run && javac Project.java && java Project; cd ..
clean:
	rm -f *.class

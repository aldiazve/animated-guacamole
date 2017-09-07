OBJ     = Token.class Lexer.class Main.class


%.class: %.java
	javac $?

build: $(OBJ)

run: $(OBJ)
	java Main

tests: $(OBJ)
# TODO

clean:
	rm -f *.class

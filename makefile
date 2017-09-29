OBJ     = Project.class


%.class: %.java
	javac $?

build: $(OBJ)

run: $(OBJ)
	java Project

tests: $(OBJ)
# TODO

clean:
	rm -f *.class

.PHONY: all clean

all:
	javac participant/*.java
	javac coordinator/*.java



clean:
	-rm participant/*.class
	-rm coordinator/*.class

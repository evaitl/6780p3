.PHONY: all clean indent

SRCS := $(wildcard coordinator/*.java)
SRCS += $(wildcard participant/*.java)

all:
	javac participant/*.java
	javac coordinator/*.java



clean:
	-rm participant/*.class
	-rm coordinator/*.class

indent:
	uncrustify --no-backup -c docs/uncrustify.cfg $(SRCS)

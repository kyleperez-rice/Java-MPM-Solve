EXEC = Test
EXEC_FILE = $(EXEC).java

DEPS0 = Incrementation.java
DEPS1 = MaterialPoint.java
DEPS2 = Node.java


FILES = $(EXEC_FILE) $(DEPS0) $(DEPS1) $(DEPS2)


$(EXEC): $(FILES)
	javac \
	$(FILES)

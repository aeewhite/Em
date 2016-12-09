all: dist dpl

run: jar
	@echo "Starting program...\n"
	java -jar bin/dpl.jar

test: compileTestFiles
	java -cp "lib/*:bin" em.TestRunner

testJar: compileTestFiles
	cd bin && jar cfme tests.jar ../lib/manifest.txt em.TestRunner em

debug: jar
	java -jar -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=5005 bin/dpl.jar

bin:
	mkdir -p bin

jar: compileSrcFiles
	cd bin && jar cfe dpl.jar em.Runner em

clean:
	rm -rf bin
	rm -rf out
	rm -rf dist

compileSrcFiles: bin
	javac -Xlint:unchecked -cp "lib/*:bin:." -g -d bin src/main/java/em/*.java

compileTestFiles: compileSrcFiles
	javac -cp "lib/*:bin:." -g -d bin src/test/java/em/*.java

dist: clean jar
	mkdir dist
	(echo "#! /usr/bin/env java -jar "; cat bin/dpl.jar) > dist/emi
	chmod +x dist/emi

install: dist
	cp dist/emi /usr/local/bin

# Assignment Targets
error1:
	cat tests/error1.em

error1x:
	dist/emi tests/error1.em

error2:
	cat tests/error2.em

error2x:
	dist/emi tests/error2.em

error3:
	cat tests/error3.em

error3x:
	dist/emi tests/error3.em

arrays:
	cat tests/arrays.em

arraysx:
	dist/emi tests/arrays.em

conditionals:
	cat tests/conditionals.em

conditionalsx:
	dist/emi tests/conditionals.em

recursion:
	cat tests/recursion.em

recursionx:
	dist/emi tests/recursion.em

iteration:
	cat tests/iteration.em

iterationx:
	dist/emi tests/iteration.em

functions:
	cat tests/functions.em

functionsx:
	dist/emi tests/functions.em

dictionary:
	cat tests/dictionary.em

dictionaryx:
	dist/emi tests/dictionary.em

problem:
	cat tests/rpn.em
	cat tests/rpninput0.txt
	cat tests/rpninput1.txt
	cat tests/rpninput2.txt


problemx:
	@echo "Input file 0"
	cat tests/rpninput0.txt |  dist/emi tests/rpn.em
	@echo "Input file 1"
	cat tests/rpninput1.txt |  dist/emi tests/rpn.em
	@echo "Input file 2"
	cat tests/rpninput2.txt |  dist/emi tests/rpn.em

# Extra Rules for Special Features
precedence:
	cat tests/precedence.em

precedencex:
	dist/emi tests/precedence.em

objects:
	cat tests/objects.em

objectsx:
	dist/emi tests/objects.em

emoji:
	cat tests/emoji.em

emojix:
	dist/emi tests/emoji.em

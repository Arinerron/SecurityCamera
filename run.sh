set -e

javac -cp .:webcam-capture-0.3.10.jar:slf4j-api-1.7.2.jar:bridj-0.6.2.jar Main.java;
java -cp .:webcam-capture-0.3.10.jar:slf4j-api-1.7.2.jar:bridj-0.6.2.jar Main

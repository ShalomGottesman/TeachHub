This pom will compile the project directly into a standalone JAR file. use the command 
"mvn package"
and two JAR files will be created in the target folder (TeachHub/target). TeachHub.jar and TeachHub.one-jar.jar.
the TeachHub.one-jar.jar file will contain all dependencies and can thus be moved to any location to be used with the command line arguments:
java -jar TeachHub.one-jar.jar
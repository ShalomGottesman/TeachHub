This pom will compile the project directly into a standalone JAR file. use the command 
"mvn package"
and one JAR file will be created in the target folder (TeachHub/target): TeachHub.jar 
the TeachHub.jar file will contain all dependencies and can thus be moved to any location and can be used with the command line arguments:
java -jar TeachHub.jar

This POM file can be fed a custom directory to compile the TeachHub.jar file to in the following manner:
mvn package -DoutputDirectory=<custom/directory>

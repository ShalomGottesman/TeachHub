The POM file will compile the project in a way that the TeachHub.jar will only contain the 
TeachHub source code and be compiled to /TeachHub/target/TeachHub.jar. However, all the dependencies will be compiled to
TeachHub/target/dependency-jars directory. This means the the TeachHub.jar CANNOT be moved from its location unless the 
dependency-jars directory is moved with it. To compile with this setup, use the following:

mvn package

The applciation can be run with the following:
java -jar TeachHub.jar

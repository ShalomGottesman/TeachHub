To compile the project using Maven: the default POM.xml file contains only the dependencies of the TeachHub project.
If you want to compile this into a runnable JAR file, there are two options:

1. The one-jar.xml file will compile the project into a "fat jar" with all of the dependencies packaged into the same jar,
which means that the jar can be moved around in the file system. To compile, use the command
mvn -f one-jar.xml package
and the TeachHub.jar will be compiled into the TeachHub/target directory, from there it can be moved to elsewhere. 
Alternatively, the TeachHub.jar can be compiled to a custom location with the following command:

mvn -f one-jar.xml package -DoutputDirectory=<myAbsolutePath>


2. The seperate-dependencies.xml file will compile the project in a way that the TeachHub.jar will only contain the 
TeachHub source code and be compiled to TeachHub/target/TeachHub.jar. However, all the dependencies will be compiled to
TeachHub/target/dependency-jars directory. This means the the TeachHub.jar CANNOT be moved from its location unless the 
dependency-jars directory is moved with it. To compile with this setup, use the following:

mvn -f seperate-dependencies.xml package


For both of these, the applciation can be run with the following:
java -jar TeachHub.jar
# Simple Library Project

This project is simple library with simple functionalities.

## How to setup the project on your local machine 	

### JAVA

This project is written in JAVA. 
The app can be started only if your machine have installed and configured properly jdk or jre.

### Database

Besides the starting application, in order to run the project you will need also and running MySQL DB.
Before starting you have to create additional database in your SQL server.
The database name have to be called 'library'.
The user that will be given to the application have to have all permissions to the created database.

### MAVEN

This project is build with maven build tool. 
To get the app up and running you have to have installed and configured Apache Maven project management and comprehension tool.

### Download the source files
Using GIT
```
git clone https://github.com/paaavkata/library.git
```

Or just download source files from 
```
https://github.com/paaavkata/library/archives/master.zip
```
### Configure the library application

1. MySQL server host,credentials and other DB properties need to be configured in the application.properties file located in the src/main/resource folder.

```
spring.datasource.url = jdbc:mysql://localhost:3306/library?useSSL=false
spring.datasource.username = root
spring.datasource.password = root //if not using one, don't provide this field
spring.jpa.properties.hibernate.dialect = org.hibernate.dialect.MySQL5InnoDBDialect
spring.jpa.hibernate.ddl-auto = create //After running the application once, change this property to 'update' in order to keep the persisted data.
```

2. Configure the applications properties:
```
sessionDurationMinutes=5 //User session duration(If not provided default set to 5)
genres=Drama,Romance,Action,Satire,Horror,Comics,Art,Science fiction //Default genres that are loaded on first add of any book
server.port = 8080
bookImages.maxSize=5 //max size of the uploaded images(If not provided default set to 5)
bookImages.path=images //images path in static folder(If not provided default set to 'images')
```

### Build the jar file

The application is setup with Spring Boot, so you have to build the jar file that is going to be run:
1. Go into the folder of the project and run in the same folder your favourite cli(bash, power shell, cmd)

2. After you make sure you have installed Apache Maven, execute the following command in the cli:
```
mvn package
```

### Run the application

After you successfully built the jar, now you have to execute it:
Go into the folder where the jar is build(by default the folder is named 'target'):
```
cd target
```

Execute the jar file:
```
java -jar library-0.0.1-SNAPSHOT.jar
```

### API Documentation

All the APIs that the application exposes can be found in the Swagger user interface: http://localhost:8080/swagger-ui.html

### Library application in the browser

The application has very lame frontend only for development purposes. The frontend can be found here: http://localhost:8080

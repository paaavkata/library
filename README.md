# Simple Library Project

This project is simple library with simple functionalities.

## How to setup the project on your local machine 	

### Database

Besides the starting application, in order to run the project you will need also and running MySQL DB.
Before starting you have to create additional database in your SQL server.
The database name have to be called 'library'.
The user that will be given to the application have to have all permissions to the created database.

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

3. Build the jar file

The application is setup with Spring Boot, so you have to build the jar file that is going to be run:

```

## Running the tests

Explain how to run the automated tests for this system

### Break down into end to end tests

Explain what these tests test and why

```
Give an example
```

### And coding style tests

Explain what these tests test and why

```
Give an example
```

## Deployment

Add additional notes about how to deploy this on a live system

## Built With

* [Dropwizard](http://www.dropwizard.io/1.0.2/docs/) - The web framework used
* [Maven](https://maven.apache.org/) - Dependency Management
* [ROME](https://rometools.github.io/rome/) - Used to generate RSS Feeds

## Contributing

Please read [CONTRIBUTING.md](https://gist.github.com/PurpleBooth/b24679402957c63ec426) for details on our code of conduct, and the process for submitting pull requests to us.

## Versioning

We use [SemVer](http://semver.org/) for versioning. For the versions available, see the [tags on this repository](https://github.com/your/project/tags). 

## Authors

* **Billie Thompson** - *Initial work* - [PurpleBooth](https://github.com/PurpleBooth)

See also the list of [contributors](https://github.com/your/project/contributors) who participated in this project.

## License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details

## Acknowledgments

* Hat tip to anyone whose code was used
* Inspiration
* etc

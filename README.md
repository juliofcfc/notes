# Notes App

### Reference Documentation

#### Requirements:

* Java 17
* Maven 3.8 or newer

#### Instructions

* Build and start the application using Maven:
  
  `mvn spring-boot:run`
* Go to http://localhost:8080/swagger-ui/index.html and call the APIs using Swagger UI. You can also use Postman or other HTTP client to call the APIs.

#### Notes
The application is using MongoDB as database and by default it will use
embedded MongoDB. If you restart the application, all data will be lost.

To persist the data across restarts, install or run MongoDB and remove or comment all properties from src/main/resources/application.properties before starting the application.





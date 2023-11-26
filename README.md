# Accommodation Booking Service

The Booking Service is an application that provides functionality for managing property rent. As a customer you can easily join, search for appropriate option, create a reservation and pay by card. As a manager, you have a lot of tools to arrange accommodations, see all bookings, payments with its status of specific user, create new accommodations and update existing ones.    

### ⚡️Moreover, application provides

* automatic checking for expired bookings once a day (and sending a message to managers)
* 30 minutes to pay for created booking (otherwise it'll be cancelled)
* telegram notifications for users about creating bookings, payments and new accommodations available

<a name="table-of-contents"></a>
## Table of Contents
* [Features](#features)  
* [Technologies](#technologies)  
* [Entities](#entities)  
* [Endpoints](#endpoints)
* [Architecture](#architecture)
* [Class diagram](#class-diagram)
* [Project Launch with Docker](#project-launch-with-docker)

<a name="features"></a>
## Features:

### **🧑🏼Customer:**

* register in app
* log in (using username (email) and password) to search for accommodations, make bookings and pay

#### 🔎Accommodation searching:
* view all available accommodations
* find exact accommodation by id

#### 🧾Booking management:
* create new booking
* view all your bookings
* find exact booking by id
* update booking
* delete booking

#### 📂Profile management:
* view your profile
* update your profile

#### 🪪Payment management:
* create new payment
* cancel payment

#### 🔔Notifications:
* receiving telegram notification about creating booking  
* receiving telegram notification about cancelling booking  
* receiving telegram notification about success payment  
* receiving telegram notification when new accommodation is added  
* receiving telegram notification when new accommodation is available for booking  
* receiving telegram notification with booking info  
* get an info about accommodation in telegram   


### 🧑🏼‍💻Manager:

* register in app
* log in using username (email) and password

#### In addition, you can:

#### 🏠Accommodation management:
* create new accommodation
* update existing one
* delete accommodation

#### 🧾Booking management:
* view all bookings and it's status

#### 🪪Payment management:
* view all payments all payment of specific user

### 🦸🏼‍♂️Admin:

* log in using username (email) and password

#### In addition, you can:
* update user's role

[back to table of contents](#table-of-contents)

<a name="technologies"></a>
## 💻Technologies

* **Programming language:** Java 17
* **Spring Framework:** Spring Boot v3.1.5, Spring Data, Spring Security v6.1.5 (Authentication using JWT token)
* **Database Management:** PostgreSQL 42.7.0, Hibernate, Liquibase v4.20.0
* **Notification management:** Telegram bot 5.2.0
* **Payment processing:** Stripe 22.3.0 
* **Testing:** JUnit 5, Mockito, TestContainers v1.19.2, PostgreSQL 42.7.0
* **Deployment and Cloud Services:** Docker 3.8
* **Additional instruments:** Maven, Lombok, Mapstruct
* **Documentation:** Swagger

[back to table of contents](#table-of-contents)

<a name="entities"></a>
## Entities:

1. **User** - represents any user 
2. **Role** - represents user's role in app (customer, manager or admin)
3. **Accommodation** - represents any property
4. **Booking** - represents user's booking of specific property for some period
5. **Payment** - represents user's payment for booking

[back to table of contents](#table-of-contents)

<a name="endpoints"></a>
## Endpoints:

### Authentication Controller:

POST: /api/auth/register - register a new user   
POST: /api/auth/login - login registered user    

### Accommodation Controller:

POST: /api/accommodations - create new accommodation  `manager only`     
GET: /api/accommodations - get a list of all accommodations   
GET: /api/accommodations/{id} - get exact accommodation by id   
PUT: /api/accommodations/{id} - update exact accommodation by id `manager only`     
DELETE: /api/accommodations/{id} - delete exact accommodation by id `manager only`  

### Booking Controller:

POST: /api/bookings - create a new booking  
GET: /api/bookings - get all bookings `manager only`     
GET: /api/bookings/my - get user's bookings   
GET: /api/bookings/{id} - get booking by id  
PUT: /api/bookings/{id} - update booking by id  
DELETE: /api/bookings/{id} - delete booking by id  

### Payment Controller:

POST: /api/payments - create payment  
GET: /api/payments/success - redirect endpoint after success stripe payment  
GET: success/cancel - redirect endpoint after closing stripe session  
GET: /api/payments - find all payments or payment of specific user `manager only`  

### User Controller:

GET: /api/users/me - get user's profile  
PUT/PATCH: /api/users/me - update user's profile  
PUT: /api/users/{id}/role - update user's role  `admin only`  

[back to table of contents](#table-of-contents)

<a name="architecture"></a>
## Architecture:
![img.png](img.png)

[back to table of contents](#table-of-contents)

<a name="class-diagram"></a>
## Class diagram

![img_1.png](img_1.png)

[back to table of contents](#table-of-contents)

<a name="progect-launch-with-docker"></a>
## Project Launch with Docker

* Clone the repository from GitHub  
* Create a `.env` file with the necessary environment variables (as an example for filling - .env.sample).  
* Run `mvn clean package` command  
* Run `docker-compose build` command to build, and `docker-compose up` to start the Docker containers  
* The application should be running at http://localhost:8088. You can test the operation of the application using swagger http://localhost:8088/swagger-ui/index.html.

[back to table of contents](#table-of-contents)

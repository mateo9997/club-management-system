# club-management-system

### Introduction: 
The Club Management System is a web application designed to manage club operations, including managing club and player details, as well as handling user authentication. This system is built using Spring Boot, PostgreSQL, and utilizes JWT for secure authentication.

### Features:

*   Manage clubs and players.
*   Secure user login and access control.
*   Data validation and storage in a PostgreSQL database.
*   Deployment on Heroku for easy access and management.

* * *

### Prerequisites:

1.  Java 17
2.  Maven
3.  PostgreSQL
4.  Heroku CLI (for deployment)
5.  Postman (for API testing)

### Local Setup:

1.  **Clone the repository:**

    bash

    Copy code

    `git clone https://github.com/your-repository/club-management-system.git`

2.  **Navigate into the directory:**

    bash

    Copy code

    `cd club-management-system`

3.  **Install dependencies:**

    Copy code

    `mvn install`

4.  **Set up environment variables:**

    *   `DATABASE_URL` - Connection string for the PostgreSQL database.
    *   `JWT_SECRET` - Secret key for JWT generation.
5.  **Run the application:**

    arduino

    Copy code

    `mvn spring-boot:run`

* * *

### Testing:

Use Postman or any other API testing tool to test the endpoints. Ensure that the required headers for authentication (`Authorization: Bearer <token>`) are included in the requests that require authentication.



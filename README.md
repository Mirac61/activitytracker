[![Review Assignment Due Date](https://classroom.github.com/assets/deadline-readme-button-22041afd0340ce965d47ae6ef1cefeee28c7c493a6346c4f15d667ab976d596c.svg)](https://classroom.github.com/a/aJfDxjD8)

how to set up postgresql with docker:

1. Install docker and log in
2. Copy `.env.example` file and name it `.env`
3. Go to `.env` and insert our data in the empty space 
4. Copy `application.properties.example` file and name it `application.properties`
5. Go to `application.properties` and insert our data in the empty space
6. Insert into your terminal: "docker-compose up -d" 
7. Run `BackendApplication.java`
8. Press Database button on the right edge of IntelliJ
9. Add new Database:
   - Type in name in front of @localhost our database name
   - Type in the `Database` field our database name
   - Type in username and password and press apply

 Now you should see our two tables if you open up the public folder
 If so everything worked properly!

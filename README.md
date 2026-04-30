[![Review Assignment Due Date](https://classroom.github.com/assets/deadline-readme-button-22041afd0340ce965d47ae6ef1cefeee28c7c493a6346c4f15d667ab976d596c.svg)](https://classroom.github.com/a/aJfDxjD8)

how to set up postgresql with docker:

1. Install docker and log in
2. Copy `.env.example` file and name it `.env`
3. Go to `.env` and insert our data in the empty space
4. Install `EnvFile` PlugIn (Power supply Icon) 
5. Edit your Build configurations (3 dots on the upper right)
6. Enable EnvFile and Substitute Environment Variables
7. Click + and add our `.env` file which is in our backend directory
8. open docker-compose.yml and run it (first line on the left of services)
9. Insert into your terminal: "docker-compose up -d" 
10. Run `BackendApplication.java`
11. Press Database button on the right edge of IntelliJ
12. Add new Database:
    - Type in name in front of @localhost our database name
    - Type in the `Database` field our database name
    - Type in username and password and press apply

 Now you should see our two tables if you open up the public folder.

 If so everything worked properly!

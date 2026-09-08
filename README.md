# Activity Tracker

Offline-first activity tracker with an Android client and a Spring Boot backend.
The client stores entries locally and reconciles them with the server through
WorkManager, so the app stays usable without a network connection. Authentication
runs through Keycloak. releases are built and signed by CI on tag.

Mirror of a university team project (6 people, one semester). The original
repository is private. My work was on the offline sync between local and remote
database, the statistics feature, and the release pipeline.

## Setup

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

## Choosing local or server 

1. In Android Studio, click the three dots on the left sidebar
2. You should find "Build Variants" with a hammer icon
3. Under "Active Build Variant" switch between `localDebug` and `serverDebug`
4. For local testing, make sure the backend is running locally

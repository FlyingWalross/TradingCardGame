# Monster Trading Card Game Server

## SDK
The server uses the java 18 SDK.

## Set up

### 1. Set up database
#### Start postgresql docker:
    $ docker run --rm --d --name swe1db -e POSTGRES_USER=swe1user -e POSTGRES_PASSWORD=swe1pw -v data:/var/lib/postgresql/data -p 5432:5432 

#### Connect to db with docker (you can also connect to db in IntelliJ):
    $ docker exec -it swe1db bash

#### Login to postqresql:
    $ psql -p 5432 -h localhost -U swe1user

#### Create db and connect to db:
    CREATE DATABASE cardgamedb;
    \c swe1db

#### Create tables:
Run sql skript from file `database.sql` to create tables

### 2. (Optional) Run tests
Open the project in intellij and run all unit tests to confirm that everything is working correctly.

### 3. Start server
Open project in intellij and start the server.
The preconfigured port is 10001 to match the curl script.

### 4. Run curl script
You can now run the curl script `curl_script_modified.bat` to confirm that the server is working correctly!
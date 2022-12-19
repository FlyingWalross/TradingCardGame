package app.controllers;

import app.dtos.NewUser;
import app.models.User;
import app.repositories.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import http.ContentType;
import http.HttpStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import server.Response;

import java.sql.SQLException;
import java.util.ArrayList;

// Our User Controller is using the database with repositories, DAOs, Models, (DTOs)
public class UserController extends Controller {
    @Setter(AccessLevel.PRIVATE)
    @Getter(AccessLevel.PRIVATE)
    UserRepository userRepository;
    public UserController(UserRepository userRepository) {
        setUserRepository(userRepository);
    }

    public Response getUsers() {
        try {
            ArrayList<User> users = getUserRepository().getAll();
            String usersJSON = getObjectMapper().writeValueAsString(users);

            if (users.isEmpty()) {
                return new Response(
                    HttpStatus.NOT_FOUND,
                    ContentType.JSON,
                    "{ \"data\": null, \"error\": \"No users found\" }"
                );
            }

            return new Response(
                HttpStatus.OK,
                ContentType.JSON,
                "{ \"data\": " + usersJSON + ", \"error\": null }"
            );
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return new Response(
            HttpStatus.BAD_REQUEST,
            ContentType.JSON,
            "{ \"data\": null, \"error\": \"Error\" }"
        );
    }

    // GET /user-profiles/:username
    public Response getUser(String username) {
        try {
            // our userRepository returns a single UserProfile getById
            User user = getUserRepository().getById(username);
            // parse to JSON string
            String userJSON = getObjectMapper().writeValueAsString(user);

            if (user == null) {
                return new Response(
                    HttpStatus.NOT_FOUND,
                    ContentType.JSON,
                    "{ \"data\": null, \"error\": \"User does not exist\" }"
                );
            }

            return new Response(
                HttpStatus.OK,
                ContentType.JSON,
                "{ \"data\": " + userJSON + ", \"error\": null }"
            );
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return new Response(
            HttpStatus.BAD_REQUEST,
            ContentType.JSON,
            "{ \"data\": null, \"error\": \"Request was malformed\" }"
        );
    }

    // POST /user-profiles
    public Response createUser(String requestBody) {
        try {
            NewUser newUser = getObjectMapper().readValue(requestBody, NewUser.class);

            //TODO: hash password
            String passwordHash = newUser.getPasswordPlain();
            User user = new User(newUser.getUsername(), passwordHash);

            getUserRepository().create(user);

            return new Response(
                    HttpStatus.CREATED,
                    ContentType.JSON,
                    "{ \"data\": null, \"error\": null }"
            );

        } catch (SQLException e) {
            String sqlState = e.getSQLState();
            if (sqlState.equals("23000") || sqlState.equals("23505")) {
                // duplicate user name
                return new Response(
                        HttpStatus.DUPLICATE,
                        ContentType.JSON,
                        "{ \"data\": null, \"error\": \"Username already exists\" }"
                );
            } else {
                // Handle other SQL exceptions
                e.printStackTrace();
                return new Response(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        ContentType.JSON,
                        "{ \"data\": null, \"error\": \"Internal Server Error\" }"
                );

            }
        } catch (JsonProcessingException e) {
            return new Response(
                    HttpStatus.BAD_REQUEST,
                    ContentType.JSON,
                    "{ \"data\": null, \"error\": \"Request was malformed\" }"
            );
        }
    }
}


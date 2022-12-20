package app;

import app.controllers.UserController;
import app.daos.UserDao;
import app.models.User;
import app.repositories.UserRepository;
import app.services.AuthenticationService;
import app.services.DatabaseService;
import http.ContentType;
import http.HttpStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import server.Request;
import server.Response;
import server.ServerApp;

import java.sql.Connection;
import java.sql.SQLException;

import static http.Method.POST;

@Setter(AccessLevel.PRIVATE)
@Getter(AccessLevel.PRIVATE)
public class App implements ServerApp {
    private Connection connection;
    private AuthenticationService authenticationService;

    private UserDao userDao;
    private UserRepository userRepository;
    private UserController userController;

    // In our app we instantiate all of our DAOs, repositories, and controllers
    // we inject the DAOs to the repos
    // we inject the repos to the controllers
    public App() {
        try {
            setConnection(new DatabaseService().getConnection());
        } catch (SQLException e) {
            e.printStackTrace();
        }

        setUserDao(new UserDao(getConnection()));
        setUserRepository(new UserRepository(getUserDao()));
        setUserController(new UserController(getUserRepository()));

        setAuthenticationService(new AuthenticationService(getUserRepository()));
    }

    // the handleRequest Method is used in the server
    // it returns the response to the client
    public Response handleRequest(Request request) {

        User user = getAuthenticationService().authenticateWithToken(request.getHeaders().get("Authorization"));
        //user needs to be logged in for every request except login and register
        if(user == null && !request.getPathname().equals("/sessions") && !(request.getPathname().equals("/users") && request.getMethod() == POST)){
            return notAuthenticated();
        }
        // check method
        switch (request.getMethod()) {
            case GET: {
                // check path and path variables
                if (request.getPathname().matches("/users/.+")) {
                    String username = request.getPathname().split("/")[2];
                    if(username.equals(user.getUsername()) || user.getUsername().equals("admin")){
                        return getUserController().getUser(username);
                    }
                    return notAuthenticated();
                }
            }
            case POST: {
                if (request.getPathname().equals("/users")) {
                    return getUserController().createUser(request.getBody());
                }

                if (request.getPathname().equals("/sessions")) {
                    return getUserController().loginUser(request.getBody(), getAuthenticationService());
                }
            }
        }

        // default response
        return new Response(HttpStatus.NOT_FOUND, ContentType.JSON, "{ \"data\": null, \"error\": \"Route Not Found\" }");
    }

    private Response notAuthenticated() {
        return new Response(HttpStatus.UNAUTHORIZED, ContentType.JSON, "{ \"data\": null, \"error\": \"Not Authenticated or no permission\" }");
    }
}

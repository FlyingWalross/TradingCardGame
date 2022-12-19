package app;

import app.controllers.UserController;
import app.daos.UserDao;
import app.repositories.UserRepository;
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

import static java.lang.Integer.parseInt;

@Setter(AccessLevel.PRIVATE)
@Getter(AccessLevel.PRIVATE)
public class App implements ServerApp {
    private UserController userController;
    private Connection connection;

    // In our app we instantiate all of our DAOs, repositories, and controllers
    // we inject the DAOs to the repos
    // we inject the repos to the controllers
    public App() {
        try {
            setConnection(new DatabaseService().getConnection());
        } catch (SQLException e) {
            e.printStackTrace();
        }

        setUserController(new UserController(new UserRepository(new UserDao(getConnection()))));
    }

    // the handleRequest Method is used in the server
    // it returns the response to the client
    public Response handleRequest(Request request) {
        // check method
        switch (request.getMethod()) {
            case GET: {
                // check path
                if (request.getPathname().equals("/users")) {
                    return getUserController().getUsers();
                }
                // check path and path variables
                if (request.getPathname().matches("/users/.+")) {
                    String username = request.getPathname().split("/")[2];
                    return getUserController().getUser(username);
                }
            }
            case POST: {
                if (request.getPathname().equals("/users")) {
                    return getUserController().createUser(request.getBody());
                }
            }
        }

        // default response
        return new Response(HttpStatus.NOT_FOUND, ContentType.JSON, "{ \"data\": null, \"error\": \"Route Not Found\" }");
    }
}

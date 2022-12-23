package app;

import app.controllers.UserController;
import app.daos.*;
import app.dtos.UserProfile;
import app.repositories.UserProfileRepository;
import app.services.AuthenticationService;
import app.services.DatabaseService;
import http.ContentType;
import http.HttpStatus;
import http.Responses;
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
    private CardDao cardDao;
    private DeckDao deckDao;
    private StackDao stackDao;
    private PackDao packDao;

    private UserProfileRepository userProfileRepository;
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
        setCardDao(new CardDao(getConnection()));
        setDeckDao(new DeckDao(getConnection()));
        setStackDao(new StackDao(getConnection()));
        setPackDao(new PackDao(getConnection()));

        setUserProfileRepository(new UserProfileRepository(getUserDao(), getStackDao(), getDeckDao(), getCardDao()));

        setUserController(new UserController(getUserProfileRepository()));

        setAuthenticationService(new AuthenticationService(getUserProfileRepository()));
    }

    // the handleRequest Method is used in the server
    // it returns the response to the client
    public Response handleRequest(Request request) {

        UserProfile user = getAuthenticationService().authenticateWithToken(request.getHeaders().get("Authorization"));
        //user needs to be logged in for every request except login and register
        if(user == null && !request.getPathname().equals("/sessions") && !(request.getPathname().equals("/users") && request.getMethod() == POST)){
            return Responses.notAuthenticated();
        }
        // check method
        switch (request.getMethod()) {
            case GET: {
                // check path and path variables
                if (request.getPathname().matches("/users/.+")) {
                    String username = request.getPathname().split("/")[2];
                    if(username.equals(user.getUsername()) || user.getUsername().equals("admin")){
                        return getUserController().getUserInfo(username);
                    }
                    return Responses.notAuthenticated();
                }
                if (request.getPathname().matches("/stats")) {
                        return getUserController().getUserStats(user);
                }
                break;
            }
            case POST: {
                if (request.getPathname().equals("/users")) {
                    return getUserController().createUser(request.getBody());
                }

                if (request.getPathname().equals("/sessions")) {
                    return getUserController().loginUser(request.getBody(), getAuthenticationService());
                }
                break;
            }
            case PUT: {
                if (request.getPathname().matches("/users/.+")) {
                    String username = request.getPathname().split("/")[2];
                    if (username.equals(user.getUsername()) || user.getUsername().equals("admin")) {
                        return getUserController().updateUser(username, request.getBody());
                    }
                    return Responses.notAuthenticated();
                }
                break;
            }
            default: {
                return Responses.routeNotFound();
            }
        }
        return Responses.routeNotFound();
    }
}

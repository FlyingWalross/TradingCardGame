package app;

import app.controllers.CardController;
import app.controllers.PackController;
import app.controllers.TradeController;
import app.controllers.UserController;
import app.daos.*;
import app.dtos.UserProfile;
import app.repositories.PackRepository;
import app.repositories.TradeRepository;
import app.repositories.UserProfileRepository;
import app.services.AuthenticationService;
import app.services.DatabaseService;
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
    Connection connection;
    AuthenticationService authenticationService;

    UserDao userDao;
    CardDao cardDao;
    DeckDao deckDao;
    StackDao stackDao;
    PackDao packDao;
    TradeDao tradeDao;

    UserProfileRepository userProfileRepository;
    PackRepository packRepository;
    TradeRepository tradeRepository;

    UserController userController;
    PackController packController;
    CardController cardController;
    TradeController tradeController;

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
        setTradeDao(new TradeDao(getConnection()));

        setUserProfileRepository(new UserProfileRepository(getUserDao(), getStackDao(), getDeckDao(), getCardDao()));
        setPackRepository(new PackRepository(getPackDao(), getCardDao()));
        setTradeRepository(new TradeRepository(getTradeDao(), getCardDao()));

        setUserController(new UserController(getUserProfileRepository()));
        setPackController(new PackController(getUserProfileRepository(), getPackRepository()));
        setCardController(new CardController(getUserProfileRepository()));
        setTradeController(new TradeController(getUserProfileRepository(), getTradeRepository()));

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
            case GET -> {
                // check path and path variables
                if (request.getPathname().matches("/users/.+")) {
                    String username = request.getPathname().split("/")[2];
                    if (username.equals(user.getUsername()) || user.getUsername().equals("admin")) {
                        return getUserController().getUserInfo(username);
                    }
                    return Responses.notAuthenticated();
                }
                if (request.getPathname().matches("/stats")) {
                    return getUserController().getUserStats(user);
                }
                if (request.getPathname().matches("/scores")) {
                    return getUserController().getScoreboard();
                }
                if (request.getPathname().matches("/cards")) {
                    return getCardController().getUserCards(user);
                }
                if (request.getPathname().matches("/decks")) {
                    return getCardController().getUserDeck(user);
                }
                if (request.getPathname().matches("/tradings")) {
                    return getTradeController().getAllTrades();
                }
            }
            case POST -> {
                if (request.getPathname().equals("/users")) {
                    return getUserController().createUser(request.getBody());
                }
                if (request.getPathname().equals("/sessions")) {
                    return getUserController().loginUser(request.getBody(), getAuthenticationService());
                }
                if (request.getPathname().equals("/packages")) {
                    if (user.getUsername().equals("admin")) {
                        return getPackController().createPack(request.getBody());
                    }
                    return Responses.notAdmin();
                }
                if (request.getPathname().equals("/transactions/packages")) {
                    return getPackController().buyPack(user);
                }
                if (request.getPathname().matches("/tradings")) {
                    return getTradeController().createTrade(request.getBody(), user);
                }
                if (request.getPathname().matches("/tradings/.+")) {
                    String tradeId = request.getPathname().split("/")[2];
                    return getTradeController().acceptTrade(request.getBody(), tradeId, user);
                }
            }
            case PUT -> {
                if (request.getPathname().matches("/users/.+")) {
                    String username = request.getPathname().split("/")[2];
                    if (username.equals(user.getUsername()) || user.getUsername().equals("admin")) {
                        return getUserController().updateUser(username, request.getBody());
                    }
                    return Responses.notAuthenticated();
                }
                if (request.getPathname().equals("/decks")) {
                    return getCardController().configureDeck(request.getBody(), user);
                }
            }
            case DELETE -> {
                if (request.getPathname().matches("/tradings/.+")) {
                    String tradeId = request.getPathname().split("/")[2];
                    return getTradeController().deleteTrade(tradeId, user);
                }
            }
        }
        return Responses.routeNotFound();
    }
}

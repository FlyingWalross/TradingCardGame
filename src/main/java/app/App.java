package app;

import app.controllers.*;
import app.daos.*;
import app.dtos.UserProfile;
import app.repositories.PackRepository;
import app.repositories.ShopRepository;
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

    UserController userController;
    PackController packController;
    CardController cardController;
    TradeController tradeController;
    BattleController battleController;
    ShopController shopController;

    // In our app we instantiate all of our DAOs, repositories, and controllers
    // we inject the DAOs to the repos
    // we inject the repos to the controllers
    public App() {
        try {
            setConnection(new DatabaseService().getConnection());
        } catch (SQLException e) {
            e.printStackTrace();
        }

        UserDao userDao = new UserDao(getConnection());
        CardDao cardDao = new CardDao(getConnection());
        DeckDao deckDao = new DeckDao(getConnection());
        StackDao stackDao = new StackDao(getConnection());
        PackDao packDao = new PackDao(getConnection());
        TradeDao tradeDao = new TradeDao(getConnection());
        ShopDao shopDao = new ShopDao(getConnection());

        UserProfileRepository userProfileRepository = new UserProfileRepository(userDao, stackDao, deckDao, cardDao);
        PackRepository packRepository = new PackRepository(packDao, cardDao);
        TradeRepository tradeRepository = new TradeRepository(tradeDao, cardDao);
        ShopRepository shopRepository = new ShopRepository(shopDao, cardDao);

        setUserController(new UserController(userProfileRepository));
        setPackController(new PackController(userProfileRepository, packRepository));
        setCardController(new CardController(userProfileRepository));
        setTradeController(new TradeController(userProfileRepository, tradeRepository));
        setBattleController(new BattleController(userProfileRepository));
        setShopController(new ShopController(userProfileRepository, shopRepository));

        setAuthenticationService(new AuthenticationService(userProfileRepository));
    }

    // the handleRequest Method is used in the server
    // it returns the response to the client
    public Response handleRequest(Request request) {

        //auth user, null when no token is provided or user doesn't exist
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
                if (request.getPathname().equals("/stats")) {
                    return getUserController().getUserStats(user);
                }
                if (request.getPathname().equals("/scores")) {
                    return getUserController().getScoreboard();
                }
                if (request.getPathname().equals("/cards")) {
                    return getCardController().getUserCards(user);
                }
                if (request.getPathname().equals("/decks")) {
                    if(request.getParams().equals("format=plain")){
                        return getCardController().getUserDeckAsString(user);
                    } else {
                        return getCardController().getUserDeck(user);
                    }
                }
                if (request.getPathname().equals("/tradings")) {
                    return getTradeController().getAllTrades();
                }
                if (request.getPathname().equals("/shop")) {
                    return getShopController().getAllShopCards();
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
                if (request.getPathname().equals("/tradings")) {
                    return getTradeController().createTrade(request.getBody(), user);
                }
                if (request.getPathname().matches("/tradings/.+")) {
                    String tradeId = request.getPathname().split("/")[2];
                    return getTradeController().acceptTrade(request.getBody(), tradeId, user);
                }
                if (request.getPathname().equals("/battles")) {
                    return getBattleController().battle(user);
                }
                if (request.getPathname().equals("/shop/create")) {
                    if (user.getUsername().equals("admin")) {
                        return getShopController().createNewShopCard(request.getBody());
                    }
                    return Responses.notAdmin();
                }
                if (request.getPathname().equals("/shop/quote")) {
                    return getShopController().getPriceQuote(request.getBody(), user);
                }
                if (request.getPathname().equals("/shop/buy")) {
                    return getShopController().buyCard(request.getBody(), user);
                }
                if (request.getPathname().equals("/shop/sell")) {
                    return getShopController().sellCard(request.getBody(), user);
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

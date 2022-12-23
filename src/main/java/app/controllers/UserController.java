package app.controllers;

import app.dtos.UserCredentials;
import app.dtos.UserInfo;
import app.dtos.UserProfile;
import app.dtos.UserStats;
import app.repositories.UserProfileRepository;
import app.services.AuthenticationService;
import com.fasterxml.jackson.core.JsonProcessingException;
import http.Responses;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import server.Response;

import java.sql.SQLException;

@Setter(AccessLevel.PRIVATE)
@Getter(AccessLevel.PRIVATE)
public class UserController extends Controller {
    UserProfileRepository userProfileRepository;

    public UserController(UserProfileRepository userProfileRepository) {
        setUserProfileRepository(userProfileRepository);
    }

    // GET /user-profiles/:username
    public Response getUserInfo(String username) {

        try {
            UserInfo userInfo = getUserProfileRepository().getUserInfo(username);

            if (userInfo == null) {
                return Responses.userDoesNotExist();
            }

            // parse to JSON string
            String userInfoJSON = getObjectMapper().writeValueAsString(userInfo);

            return Responses.ok(userInfoJSON);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return Responses.internalServerError();
        }
    }

    // GET /stats
    public Response getUserStats(UserProfile user) {
        try {
            UserStats userStats = getUserProfileRepository().getUserStats(user);

            // parse to JSON string
            String userStatsJSON = getObjectMapper().writeValueAsString(userStats);

            return Responses.ok(userStatsJSON);

        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return Responses.internalServerError();
        }
    }

    // POST /user-profiles
    public Response createUser(String requestBody) {
        try {
            UserCredentials userCredentials = getObjectMapper().readValue(requestBody, UserCredentials.class);
            getUserProfileRepository().create(userCredentials);

            return Responses.created();

        } catch (SQLException e) {
            String sqlState = e.getSQLState();
            if (sqlState.equals("23000") || sqlState.equals("23505")) {
                // duplicate user name
                return Responses.duplicateUsername();
            } else {
                // Handle other SQL exceptions
                e.printStackTrace();
                return Responses.internalServerError();

            }
        } catch (JsonProcessingException e) {
            return Responses.requestMalformed();
        }
    }

    // POST /user-profiles
    public Response updateUser(String username, String requestBody) {
        try {
            UserInfo userInfo = getObjectMapper().readValue(requestBody, UserInfo.class);
            if(userProfileRepository.update(userInfo, username) == null) {
                return Responses.userDoesNotExist();
            }

            return Responses.ok();

        } catch (SQLException e) {
                e.printStackTrace();
                return Responses.internalServerError();
        } catch (JsonProcessingException e) {
            return Responses.requestMalformed();
        }
    }

    // POST /user-profiles
    public Response loginUser(String requestBody, AuthenticationService authenticationService) {
        try {
            UserCredentials userCredentials = getObjectMapper().readValue(requestBody, UserCredentials.class);

            UserProfile user = authenticationService.authenticateWithPassword(userCredentials.getUsername(), userCredentials.getPasswordPlain());

            if(user == null) {
                return Responses.invalidCredentials();
            }

            String token = authenticationService.generateToken(user);

            return Responses.token(token);

        } catch (JsonProcessingException e) {
            return Responses.requestMalformed();
        }
    }
}


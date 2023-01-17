package app.controllers;

import app.dtos.UserCredentials;
import app.dtos.UserInfo;
import app.dtos.UserProfile;
import app.dtos.UserStats;
import app.exceptions.AlreadyExistsException;
import app.exceptions.UserDoesNotExistException;
import app.repositories.UserProfileRepository;
import app.services.AuthenticationService;
import com.fasterxml.jackson.core.JsonProcessingException;
import http.Responses;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import server.Response;

import java.util.ArrayList;

@Setter(AccessLevel.PRIVATE)
@Getter(AccessLevel.PRIVATE)
@AllArgsConstructor
public class UserController extends Controller {
    UserProfileRepository userProfileRepository;

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


    // GET /scores
    public Response getScoreboard() {
        try {
            ArrayList<UserStats> scoreboard = getUserProfileRepository().getScoreboard();

            // parse to JSON string
            String scoreboardJSON = getObjectMapper().writeValueAsString(scoreboard);

            return Responses.ok(scoreboardJSON);

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

        } catch (AlreadyExistsException e) {
            return Responses.duplicateUsername();
        } catch (JsonProcessingException e) {
            return Responses.requestMalformed();
        }
    }

    // POST /user-profiles
    public Response updateUser(String username, String requestBody) {
        try {
            UserInfo userInfo = getObjectMapper().readValue(requestBody, UserInfo.class);
            getUserProfileRepository().update(userInfo, username);

            return Responses.ok();
        } catch (UserDoesNotExistException e) {
            return Responses.userDoesNotExist();
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


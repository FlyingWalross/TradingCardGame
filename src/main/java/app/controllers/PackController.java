package app.controllers;

import app.dtos.*;
import app.exceptions.AlreadyExistsException;
import app.exceptions.NoPacksAvailableException;
import app.models.Card;
import app.repositories.PackRepository;
import app.repositories.UserProfileRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
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
public class PackController extends Controller {
    UserProfileRepository userProfileRepository;
    PackRepository packRepository;

    // POST /packages
    public Response createPack(String requestBody) {
        try {
            TypeReference<ArrayList<NewCard>> newCardTypeReference = new TypeReference<ArrayList<NewCard>>() {};
            ArrayList<NewCard> newCards = getObjectMapper().readValue(requestBody, newCardTypeReference);

            getPackRepository().create(newCards);

            return Responses.created();
        } catch (AlreadyExistsException e) {
            return Responses.duplicateCard();
        } catch (JsonProcessingException e) {
            return Responses.requestMalformed();
        }
    }

    // POST /transactions/packages
    public Response buyPack(UserProfile user) {
        try {
            if (user.getCoins() < 5) {
                return Responses.notEnoughCoins();
            }

            PackDTO randomPack = getPackRepository().getPack();

            user.getStack().addAll(randomPack.getCards());
            user.setCoins(user.getCoins() - 5);
            getUserProfileRepository().update(user);
            getPackRepository().delete(randomPack);

            ArrayList<NewCard> acquiredCards = new ArrayList<>();
            for(Card card: randomPack.getCards()) {
                acquiredCards.add(new NewCard(card.getId(), card.getName(), card.getDamage()));
            }

            // parse to JSON string
            String acquiredCardsJSON = getObjectMapper().writeValueAsString(acquiredCards);

            return Responses.ok(acquiredCardsJSON);
        } catch (NoPacksAvailableException e) {
            return Responses.noPacksAvailable();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return Responses.internalServerError();
        }

    }

}


package app.controllers;

import app.dtos.NewCard;
import app.dtos.UserProfile;
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
public class CardController extends Controller {
    UserProfileRepository userProfileRepository;

    // GET /cards
    public Response getUserCards(UserProfile user) {
        try {
            ArrayList<NewCard> userCards = new ArrayList<>();

            //convert database Card model to DTO NewCard
            for(Card card: user.getStack()) {
                userCards.add(new NewCard(card.getId(), card.getName(), card.getDamage()));
            }

            for(Card card: user.getDeck()) {
                userCards.add(new NewCard(card.getId(), card.getName(), card.getDamage()));
            }

            if(userCards.isEmpty()) {
                return Responses.userHasNoCards();
            }

            String userCardsJSON = getObjectMapper().writeValueAsString(userCards);
            return Responses.ok(userCardsJSON);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return Responses.internalServerError();
        }
    }

    // GET /decks
    public Response getUserDeck(UserProfile user) {
        try {
            ArrayList<NewCard> userDeck = new ArrayList<>();

            //convert database Card model to DTO NewCard
            for(Card card: user.getDeck()) {
                userDeck.add(new NewCard(card.getId(), card.getName(), card.getDamage()));
            }

            if(userDeck.isEmpty()) {
                return Responses.deckHasNoCards();
            }

            String userDeckJSON = getObjectMapper().writeValueAsString(userDeck);
            return Responses.ok(userDeckJSON);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return Responses.internalServerError();
        }
    }

    // PUT /decks
    public Response configureDeck(String requestBody, UserProfile user) {
        try {
            TypeReference<ArrayList<String>> typeReference = new TypeReference<ArrayList<String>>() {};
            ArrayList<String> cardIDs = getObjectMapper().readValue(requestBody, typeReference);

            if(cardIDs.size() != 4) {
                return Responses.deckHasWrongSize();
            }

            ArrayList<Card> newDeck = new ArrayList<>();
            ArrayList<Card> userCards = new ArrayList<>();
            userCards.addAll(user.getStack());
            userCards.addAll(user.getDeck());

            //see if all cards are owned by user and add found cards to newDeck
            for(String cardID: cardIDs) {
                Card card = userCards.stream()
                        .filter(c -> c.getId().equals(cardID))
                        .findFirst()
                        .orElse(null);
                if (card == null) {
                    return Responses.cardNotOwned();
                }
                if(newDeck.contains(card)) {
                    return Responses.cardAlreadyInDeck();
                }
                newDeck.add(card);
            }

            //Remove old cards from deck and add them to stack
            user.getStack().addAll(user.getDeck());
            user.getDeck().clear();

            //Add new cards to deck and remove them from stack
            user.getDeck().addAll(newDeck);
            user.getStack().removeAll(newDeck);

            //save to database
            getUserProfileRepository().update(user);
            return Responses.ok();
        } catch (JsonProcessingException e) {
            return Responses.requestMalformed();
        }
    }

}


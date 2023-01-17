package app.controllers;

import app.dtos.NewShopCard;
import app.dtos.ShopCardDTO;
import app.dtos.UserProfile;
import app.exceptions.AlreadyExistsException;
import app.models.Card;
import app.repositories.ShopRepository;
import app.repositories.UserProfileRepository;
import app.services.CardPriceCalculationService;
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
public class ShopController extends Controller {
    UserProfileRepository userProfileRepository;
    ShopRepository shopRepository;

    // GET /shop
    public Response getAllShopCards() {
        try {
            ArrayList<ShopCardDTO> shopCardDTOS = getShopRepository().getAll();

            if(shopCardDTOS.isEmpty()) {
                return Responses.noShopOffersAvailable();
            }

            String shopCardsJSON= getObjectMapper().writeValueAsString(shopCardDTOS);
            return Responses.ok(shopCardsJSON);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return Responses.internalServerError();
        }
    }

    // POST /shop/sell
    public Response sellCard(String requestBody, UserProfile user) {
        try {
            String cardID = getObjectMapper().readValue(requestBody, String.class);

            //check if user has card in Stack (not in Deck)
            Card sellCard = user.getStack().stream()
                    .filter(c -> c.getId().equals(cardID))
                    .findFirst()
                    .orElse(null);

            if (sellCard == null) {
                return Responses.cardNotInStack();
            }

            //calculate price
            int price = CardPriceCalculationService.calculatePrice(sellCard);

            user.getStack().remove(sellCard);
            user.setCoins(user.getCoins() + price);
            getUserProfileRepository().update(user);

            getShopRepository().createWithExistingCard(sellCard, price);

            return Responses.ok("Card sold for " + price + " coins");
        } catch (AlreadyExistsException e) {
            throw new RuntimeException(e);
        } catch (JsonProcessingException e) {
            return Responses.requestMalformed();
        }
    }

    // POST /shop/quote
    public Response getPriceQuote(String requestBody, UserProfile user) {
        try {
            String cardID = getObjectMapper().readValue(requestBody, String.class);

            //check if user has card in Stack (not in Deck)
            Card sellCard = user.getStack().stream()
                    .filter(c -> c.getId().equals(cardID))
                    .findFirst()
                    .orElse(null);

            if (sellCard == null) {
                return Responses.cardNotInStack();
            }

            //calculate price
            int price = CardPriceCalculationService.calculatePrice(sellCard);

            return Responses.ok("The price for this card is " + price + " coins");
        } catch (JsonProcessingException e) {
            return Responses.requestMalformed();
        }
    }

    // POST /shop/buy
    public Response buyCard(String requestBody, UserProfile user) {
        try {
            String cardID = getObjectMapper().readValue(requestBody, String.class);

            ShopCardDTO shopCard = getShopRepository().getById(cardID);

            if(shopCard == null) {
                return Responses.cardNotInShop();
            }

            if (user.getCoins() < shopCard.getPrice()) {
                return Responses.notEnoughCoinsForCard();
            }

            user.getStack().add(shopCard.getCard());
            user.setCoins(user.getCoins() - shopCard.getPrice());
            getUserProfileRepository().update(user);

            getShopRepository().delete(shopCard);

            return Responses.ok("Card bought for " + shopCard.getPrice() + " coins");
        } catch (JsonProcessingException e) {
            return Responses.requestMalformed();
        }
    }

    // POST /shop/create
    public Response createNewShopCard(String requestBody) {  //enables admin to create new cards to be offered in shop
        try {
            NewShopCard newShopCard = getObjectMapper().readValue(requestBody, NewShopCard.class);

            getShopRepository().createWithNewCard(newShopCard);

            return Responses.created();
        } catch (AlreadyExistsException e) {
            return Responses.duplicateCard();
        } catch (JsonProcessingException e) {
            return Responses.requestMalformed();
        }
    }

}


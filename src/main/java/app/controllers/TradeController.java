package app.controllers;

import app.dtos.NewCard;
import app.dtos.PackDTO;
import app.dtos.TradeDTO;
import app.dtos.UserProfile;
import app.enums.card_type;
import app.exceptions.AlreadyExistsException;
import app.exceptions.NoPacksAvailableException;
import app.models.Card;
import app.models.Trade;
import app.models.User;
import app.repositories.TradeRepository;
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
public class TradeController extends Controller {
    UserProfileRepository userProfileRepository;
    TradeRepository tradeRepository;

    // GET /tradings
    public Response getAllTrades() {
        try {
            ArrayList<Trade> trades = getTradeRepository().getAll();

            if(trades.isEmpty()) {
                return Responses.noTradesAvailable();
            }

            String tradesJSON = getObjectMapper().writeValueAsString(trades);
            return Responses.ok(tradesJSON);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return Responses.internalServerError();
        }
    }

    // POST /tradings
    public Response createTrade(String requestBody, UserProfile user) {
        try {

            //create trade
            Trade trade = getObjectMapper().readValue(requestBody, Trade.class);
            trade.setUsername(user.getUsername());

            //check if user has card in Stack (not in Deck)
            Card card = user.getStack().stream()
                    .filter(c -> c.getId().equals(trade.getCardId()))
                    .findFirst()
                    .orElse(null);

            if (card == null) {
                return Responses.cardNotInStack();
            }

            //create trade
            getTradeRepository().create(trade);

            //remove card from user stack
            user.getStack().remove(card);
            getUserProfileRepository().update(user);

            return Responses.created();
        } catch (AlreadyExistsException e) {
            return Responses.duplicateTradeId();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return Responses.requestMalformed();
        }
    }

    // DELETE /tradings/{id}
    public Response deleteTrade(String id, UserProfile user) {
            TradeDTO trade = getTradeRepository().getById(id);
            if (trade == null) {
                return Responses.tradeNotFound();
            }

            if(!trade.getUsername().equals(user.getUsername())) {
                return Responses.tradeDoesNotBelongToYou();
            }

            user.getStack().add(trade.getCard());
            getUserProfileRepository().update(user);

            getTradeRepository().delete(trade);
            return Responses.ok();
    }

    // POST /tradings/{id}
    public Response acceptTrade(String requestBody, String tradeId, UserProfile tradeAcceptUser) {
        try {
            TradeDTO trade = getTradeRepository().getById(tradeId);
            if (trade == null) {
                return Responses.tradeNotFound();
            }

            if(trade.getUsername().equals(tradeAcceptUser.getUsername())) {
                return Responses.cannotAcceptOwnTrade();
            }

            String cardID = getObjectMapper().readValue(requestBody, String.class);

            //check if user has card in Stack (not in Deck)
            Card tradeAcceptCard = tradeAcceptUser.getStack().stream()
                    .filter(c -> c.getId().equals(cardID))
                    .findFirst()
                    .orElse(null);

            if (tradeAcceptCard == null) {
                return Responses.cardNotInStack();
            }

            //if trade requires spell, check if tradeAcceptCard is a spell
            if(trade.getType() == card_type.spell && tradeAcceptCard.getType() != card_type.spell) {
                return Responses.requirementsNotMet();
            }

            //if trade requires any monster, check if tradeAcceptCard is not a spell
            if(trade.getType() == card_type.monster && tradeAcceptCard.getType() == card_type.spell) {
                return Responses.requirementsNotMet();
            }

            //if trade requires specific monster, check if tradeAcceptCard is the same monster
            if(trade.getType() != card_type.monster && trade.getType() != card_type.spell && trade.getType() != tradeAcceptCard.getType()) {
                return Responses.requirementsNotMet();
            }

            //check if tradeAcceptCard meets minimum damage requirement
            if(tradeAcceptCard.getDamage() < trade.getMin_damage()) {
                return Responses.requirementsNotMet();
            }

            //remove tradeAcceptCard from tradeAcceptUser stack and add tradeOfferCard
            tradeAcceptUser.getStack().remove(tradeAcceptCard);
            tradeAcceptUser.getStack().add(trade.getCard());
            getUserProfileRepository().update(tradeAcceptUser);

            //add tradeAcceptCard to tradeOfferUser stack (tradeOfferCard was already removed on creation of trade)
            UserProfile tradeOfferUser = getUserProfileRepository().getById(trade.getUsername());
            tradeOfferUser.getStack().add(tradeAcceptCard);
            getUserProfileRepository().update(tradeOfferUser);

            //delete trade
            getTradeRepository().delete(trade);

            return Responses.ok();
        } catch (JsonProcessingException e) {
            return Responses.requestMalformed();
        }
    }

}


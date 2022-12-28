package http;

import server.Response;

public class Responses {
    public static Response userDoesNotExist() {
        return new Response(
                HttpStatus.NOT_FOUND,
                ContentType.JSON,
                "{ \"error\": \"User does not exist\" }"
        );
    }

    public static Response ok(String json) {
        return new Response(
                HttpStatus.OK,
                ContentType.JSON,
                json
        );
    }

    public static Response ok() {
        return new Response(
                HttpStatus.OK,
                ContentType.JSON,
                "{ \"message\": \"Successful\" }"
        );
    }

    public static Response created() {
        return new Response(
                HttpStatus.CREATED,
                ContentType.JSON,
                "{ \"message\": \"Successful\" }"
        );
    }

    public static Response token(String token) {
        return new Response(
                HttpStatus.OK,
                ContentType.JSON,
                token
        );
    }

    public static Response invalidCredentials() {
        return new Response(
                HttpStatus.UNAUTHORIZED,
                ContentType.JSON,
                "{ \"error\": \"Invalid username or password\" }"
        );
    }

    public static Response duplicateUsername() {
        return new Response(
                HttpStatus.DUPLICATE,
                ContentType.JSON,
                "{ \"error\": \"Username already exists\" }"
        );
    }

    public static Response duplicateCard() {
        return new Response(
                HttpStatus.DUPLICATE,
                ContentType.JSON,
                "{ \"error\": \"At least one card already exists\" }"
        );
    }

    public static Response internalServerError() {
        return new Response(
                HttpStatus.INTERNAL_SERVER_ERROR,
                ContentType.JSON,
                "{ \"error\": \"Internal Server Error\" }"
        );
    }

    public static Response requestMalformed() {
        return new Response(
                HttpStatus.BAD_REQUEST,
                ContentType.JSON,
                "{ \"error\": \"Request was malformed\" }"
        );
    }

    public static Response routeNotFound() {
        return new Response(
                HttpStatus.NOT_FOUND,
                ContentType.JSON,
                "{ \"error\": \"Route not Found\" }"
        );
    }

    public static Response noPacksAvailable() {
        return new Response(
                HttpStatus.NOT_FOUND,
                ContentType.JSON,
                "{ \"error\": \"No packs available\" }"
        );
    }

    public static Response notAuthenticated() {
        return new Response(
                HttpStatus.UNAUTHORIZED,
                ContentType.JSON,
                "{ \"error\": \"Not authenticated or no permission\" }"
        );
    }

    public static Response notEnoughCoins() {
        return new Response(
                HttpStatus.FORBIDDEN,
                ContentType.JSON,
                "{ \"error\": \"Not enough coins to buy pack\" }"
        );
    }

    public static Response notAdmin() {
        return new Response(
                HttpStatus.FORBIDDEN,
                ContentType.JSON,
                "{ \"error\": \"Only admin can create packs\" }"
        );
    }

    public static Response userHasNoCards() {
        return new Response(
                HttpStatus.NO_CONTENT,
                ContentType.JSON,
                "{ \"error\": \"You don't have any cards\" }"
        );
    }

    public static Response deckHasNoCards() {
        return new Response(
                HttpStatus.NO_CONTENT,
                ContentType.JSON,
                "{ \"error\": \"There are no cards in your deck\" }"
        );
    }

    public static Response deckHasWrongSize() {
        return new Response(
                HttpStatus.BAD_REQUEST,
                ContentType.JSON,
                "{ \"error\": \"The deck must include exactly 4 cards\" }"
        );
    }

    public static Response cardNotOwned() {
        return new Response(
                HttpStatus.FORBIDDEN,
                ContentType.JSON,
                "{ \"error\": \"At least one of the specified cards doesn't belong to you or doesn't exist\" }"
        );
    }

    public static Response cardAlreadyInDeck() {
        return new Response(
                HttpStatus.FORBIDDEN,
                ContentType.JSON,
                "{ \"error\": \"You cannot add the same card to your deck twice\" }"
        );
    }

    public static Response noTradesAvailable() {
        return new Response(
                HttpStatus.NO_CONTENT,
                ContentType.JSON,
                "{ \"error\": \"There are no trades available\" }"
        );
    }

    public static Response cardNotInStack() {
        return new Response(
                HttpStatus.FORBIDDEN,
                ContentType.JSON,
                "{ \"error\": \"You don't own this card or it is locked in your deck or another trade\" }"
        );
    }

    public static Response duplicateTradeId() {
        return new Response(
                HttpStatus.DUPLICATE,
                ContentType.JSON,
                "{ \"error\": \"A trade with this id already exists\" }"
        );
    }

    public static Response tradeNotFound() {
        return new Response(
                HttpStatus.NOT_FOUND,
                ContentType.JSON,
                "{ \"error\": \"The deal with the requested id was not found\" }"
        );
    }

    public static Response tradeDoesNotBelongToYou() {
        return new Response(
                HttpStatus.FORBIDDEN,
                ContentType.JSON,
                "{ \"error\": \"The requested trade doesn't belong to you\" }"
        );
    }

    public static Response cannotAcceptOwnTrade() {
        return new Response(
                HttpStatus.FORBIDDEN,
                ContentType.JSON,
                "{ \"error\": \"You cannot accept your own trade offer\" }"
        );
    }

    public static Response requirementsNotMet() {
        return new Response(
                HttpStatus.FORBIDDEN,
                ContentType.JSON,
                "{ \"error\": \"Your offered card does not meet the requirements of the trade\" }"
        );
    }
}

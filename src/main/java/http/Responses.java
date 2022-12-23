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
                "{ \"message\": \"successful\" }"
        );
    }

    public static Response created() {
        return new Response(
                HttpStatus.CREATED,
                ContentType.JSON,
                "{ \"message\": \"successful\" }"
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
                "{ \"error\": \"Route Not Found\" }"
        );
    }

    public static Response notAuthenticated() {
        return new Response(
                HttpStatus.UNAUTHORIZED,
                ContentType.JSON,
                "{ \"error\": \"Not authenticated or no permission\" }"
        );
    }
}

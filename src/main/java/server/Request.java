package server;

import http.Method;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


@Getter
@Setter(AccessLevel.PROTECTED)
public class Request {
    private Method method;
    private String pathname;
    private String params;
    private String contentType;
    private Integer contentLength;
    private String body = "";
    private Map<String, String> headers;

    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.NONE)
    private final String CONTENT_TYPE = "Content-Type: ";
    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.NONE)
    private final String CONTENT_LENGTH = "Content-Length: ";

    public Request(BufferedReader inputStream) {
        buildRequest(inputStream);
    }

    private void buildRequest(BufferedReader inputStream) {
        try {
            String line = inputStream.readLine();

            if (line != null) {
                String[] splitFirstLine = line.split(" ");
                Boolean hasParams = splitFirstLine[1].contains("?");


                setMethod(getMethodFromInputLine(splitFirstLine));
                setPathname(getPathnameFromInputLine(splitFirstLine, hasParams));
                setParams(getParamsFromInputLine(splitFirstLine, hasParams));

                headers = new HashMap<>();

                line = inputStream.readLine();
                while (!line.isEmpty()) {
                    if (line.startsWith(CONTENT_LENGTH)) {
                        setContentLength(getContentLengthFromInputLine(line));
                    }
                    if (line.startsWith(CONTENT_TYPE)) {
                        setContentType(getContentTypeFromInputLine(line));
                    }
                    // Add the header to the headers field
                    int colonIndex = line.indexOf(":");
                    String key = line.substring(0, colonIndex).trim();
                    String value = line.substring(colonIndex + 1).trim();
                    headers.put(key, value);
                    line = inputStream.readLine();
                }

                if ((getMethod() == Method.POST || getMethod() == Method.PUT) && getContentLength() != null) {
                    int asciChar;
                    for (int i = 0; i < getContentLength(); i++) {
                        asciChar = inputStream.read();
                        String body = getBody();
                        setBody(body + ((char) asciChar));
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private Method getMethodFromInputLine(String[] splitFirstLine) {
        return Method.valueOf(splitFirstLine[0].toUpperCase(Locale.ROOT));
    }

    private String getPathnameFromInputLine(String[] splitFirstLine, Boolean hasParams) {
        if (hasParams) {
            return splitFirstLine[1].split("\\?")[0];
        }

        return splitFirstLine[1];
    }


    private String getParamsFromInputLine(String[] splittedFirstLine, Boolean hasParams) {
        if (hasParams) {
            return splittedFirstLine[1].split("\\?")[1];
        }

        return "";
    }

    private Integer getContentLengthFromInputLine(String line) {
        return Integer.parseInt(line.substring(CONTENT_LENGTH.length()));
    }

    private String getContentTypeFromInputLine(String line) {
        return line.substring(CONTENT_TYPE.length());
    }
}

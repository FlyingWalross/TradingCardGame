package server;

import app.App;
import http.ContentType;
import http.HttpStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

@Getter(AccessLevel.PRIVATE)
@Setter(AccessLevel.PRIVATE)
public class RequestHandler implements Runnable{

    private App app;
    private Socket clientSocket;
    private PrintWriter outputStream;
    private BufferedReader inputStream;
    private Request request;
    private Response response;

    RequestHandler(App app, Socket clientSocket){
        setApp(app);
        setClientSocket(clientSocket);
    }

    public void run() {
        try{
            //System.out.println("Current thread name:" + Thread.currentThread().getName());

            setInputStream(new BufferedReader(new InputStreamReader(clientSocket.getInputStream())));
            setRequest(new Request(getInputStream()));
            setOutputStream(new PrintWriter(clientSocket.getOutputStream(), true));

            handleRequest();

        } catch (IOException e) {
            e.printStackTrace();

        } finally {
            closeStreams();

        }
    }

    private void handleRequest() throws IOException{

        if (request.getPathname() == null) {
            setResponse(new Response(
                    HttpStatus.BAD_REQUEST,
                    ContentType.TEXT,
                    ""
            ));
        } else {
            setResponse(getApp().handleRequest(request));
        }

        getOutputStream().write(getResponse().build());
    }

    private void closeStreams(){
        try {
            if (getOutputStream() != null) {
                getOutputStream().close();
            }
            if (getInputStream() != null) {
                getInputStream().close();
                getClientSocket().close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

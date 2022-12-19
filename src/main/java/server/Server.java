package server;

import app.App;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.io.*;
import java.net.*;

@Getter(AccessLevel.PRIVATE)
@Setter(AccessLevel.PRIVATE)
public class Server {

    private ServerSocket serverSocket;
    private App app;
    private int port;

    public Server(App app, int port) throws IOException {
        setApp(app);
        setPort(port);

        setServerSocket(new ServerSocket(getPort()));

        System.out.println("Server is running on localhost:" + getPort());

        start();
    }

    private void start() {
        while (true) {
            try {

                RequestHandler requestHandler = new RequestHandler(getApp(), getServerSocket().accept());
                Thread thread = new Thread(requestHandler);
                thread.start();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
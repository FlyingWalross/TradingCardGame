import app.App;
import server.Server;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        App app = new App();
        try {
            Server server = new Server(app, 10001);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
package app.models;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class User {
    String username;
    String passwordHash;
    String name;
    int elo;
    int coins;
    int wins;
    int losses;
    String bio;
    String image;

    public User(String username, String passwordHash) {
        this(username, passwordHash, username, 0, 0, 0, 0, "", "");
    }
}

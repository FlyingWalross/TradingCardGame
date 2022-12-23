package app.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import app.Settings;

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
        this.username = username;
        this.passwordHash = passwordHash;
        this.name = username;
        this.elo = Settings.STARTING_ELO;
        this.coins = Settings.STARTING_COINS;
        this.wins = 0;
        this.losses = 0;
        this.bio = null;
        this.image = null;
    }
}

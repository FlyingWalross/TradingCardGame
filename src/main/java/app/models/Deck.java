package app.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
@AllArgsConstructor
public class Deck {
    String username;
    ArrayList<String> cardIDs;

    public Deck(String username) {
        this.username = username;
    }
}

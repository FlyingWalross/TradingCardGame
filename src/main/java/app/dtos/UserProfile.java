package app.dtos;

import app.models.Card;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
@AllArgsConstructor
//userprofile is like database user model but has deck and stack
public class UserProfile {
    String username;
    String passwordHash;
    String name;
    int elo;
    int coins;
    int wins;
    int losses;
    String bio;
    String image;
    ArrayList<Card> stack;
    ArrayList<Card> deck;

    public String stringifyDeck() {
        String deckString = "---------" + username + "'s deck---------\n\n";
        int i = 1;
        for (Card card : deck) {
            deckString += "Card " + i + ":\n" + card.stringify() + "\n";
            i++;
        }
        return deckString;
    }
}
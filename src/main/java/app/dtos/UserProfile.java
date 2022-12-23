package app.dtos;

import app.models.Card;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
@AllArgsConstructor
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
}
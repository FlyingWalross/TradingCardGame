package app.testModels;

import app.models.Deck;

import java.util.ArrayList;

public class TestDeck {

    public static Deck getTestObject() {
        ArrayList<String> cardIds = new ArrayList<>();
        cardIds.add("1");
        cardIds.add("2");
        cardIds.add("3");
        cardIds.add("4");

        return new Deck(
                "test",
                cardIds
        );
    }
}

package app.testModels;

import app.models.Pack;

import java.util.ArrayList;

public class TestPack {

    public static Pack getTestObject() {
        ArrayList<String> cardIds = new ArrayList<>();
        cardIds.add("1");
        cardIds.add("2");
        cardIds.add("3");
        cardIds.add("4");
        cardIds.add("5");

        return new Pack(
                cardIds
        );
    }
}

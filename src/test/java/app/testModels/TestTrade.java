package app.testModels;

import app.models.Trade;

import static app.enums.card_type.*;

public class TestTrade {

    public static Trade getTestObject() {
        return new Trade(
                "test",
                "test",
                "5",
                monster,
                10
        );
    }
}

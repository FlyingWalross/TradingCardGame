package app.testModels;

import app.models.Card;

import static app.enums.card_element.*;
import static app.enums.card_type.*;

public class TestCard {

    public static Card getTestObject() {
        return new Card(
                "1",
                "WaterSpell",
                spell,
                water,
                0.0f
        );
    }
}

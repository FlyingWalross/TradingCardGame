package app.testModels;

import app.daos.CardDao;
import app.daos.Dao;
import app.enums.card_element;
import app.enums.card_type;
import app.models.Card;
import app.models.User;

import java.sql.Connection;

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

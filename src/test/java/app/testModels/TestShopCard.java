package app.testModels;

import app.models.ShopCard;

import static app.enums.card_element.water;
import static app.enums.card_type.spell;

public class TestShopCard {
    public static ShopCard getTestObject() {
        return new ShopCard(
                "1",
                2
        );
    }
}

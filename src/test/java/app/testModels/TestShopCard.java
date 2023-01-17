package app.testModels;

import app.models.ShopCard;

public class TestShopCard {
    public static ShopCard getTestObject() {
        return new ShopCard(
                "1",
                2
        );
    }
}

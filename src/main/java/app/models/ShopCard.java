package app.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ShopCard {
    String cardId;
    int price;

    public ShopCard() {}
}


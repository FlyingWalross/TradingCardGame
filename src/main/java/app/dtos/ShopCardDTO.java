package app.dtos;

import app.models.Card;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ShopCardDTO {
    Card card;
    int price;

    public ShopCardDTO() {}
}


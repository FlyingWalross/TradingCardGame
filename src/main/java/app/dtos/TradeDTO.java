package app.dtos;

import app.enums.card_type;
import app.models.Card;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class TradeDTO {
    String id;
    String username;
    Card card;
    card_type type;
    float min_damage;
}

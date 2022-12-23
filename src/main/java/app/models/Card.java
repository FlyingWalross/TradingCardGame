package app.models;

import app.enums.card_element;
import app.enums.card_type;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Card {
    String id;
    String name;
    card_type type;
    card_element element;
    float damage;
}

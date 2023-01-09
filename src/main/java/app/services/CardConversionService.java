package app.services;

import app.dtos.NewCard;
import app.enums.card_element;
import app.enums.card_type;
import app.models.Card;

public class CardConversionService {
    public static Card convertCard(NewCard newCard){
        //convert newCard to Database card model
        card_element element = card_element.normal;
        if (newCard.getName().contains("Water")) {
            element = card_element.water;
        } else if (newCard.getName().contains("Fire")) {
            element = card_element.fire;
        }

        card_type type = card_type.valueOf(newCard.getName().replaceAll("(Water|Fire|Regular)", "").toLowerCase());
        return new Card(
                newCard.getId(),
                newCard.getName(),
                type,
                element,
                newCard.getDamage()
        );
    }
}

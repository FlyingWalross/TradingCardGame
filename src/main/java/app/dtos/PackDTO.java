package app.dtos;

import app.models.Card;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
@AllArgsConstructor
public class PackDTO {
    int id;
    ArrayList<Card> cards;

    public PackDTO() {
        this.cards = new ArrayList<>();
    }
    public PackDTO(int id) {
        this.id = id;
        this.cards = new ArrayList<>();
    }
}
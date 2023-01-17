package app.models;

import app.enums.card_element;
import app.enums.card_type;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Card {
    @JsonProperty("Id")
    String id;
    @JsonProperty("Name")
    String name;
    @JsonIgnore
    card_type type;
    @JsonIgnore
    card_element element;
    @JsonProperty("Damage")
    float damage;

    public String stringify() {
        return  "   Id: " + id + "\n" +
                "   Name: " + name + "\n" +
                "   Type: " + type.name() + "\n" +
                "   Element: " + element.name() + "\n" +
                "   Damage: " + damage + "\n";
    }
}

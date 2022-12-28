package app.dtos;

import app.models.Card;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
@AllArgsConstructor
public class NewCard {
    @JsonAlias({"Id"})
    @JsonProperty("Id")
    String id;
    @JsonAlias({"Name"})
    @JsonProperty("Name")
    String name;
    @JsonAlias({"Damage"})
    @JsonProperty("Damage")
    float damage;

    public NewCard() {}
}
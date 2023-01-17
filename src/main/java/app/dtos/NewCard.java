package app.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class NewCard {
    @JsonProperty("Id")
    String id;
    @JsonProperty("Name")
    String name;
    @JsonProperty("Damage")
    float damage;

    public NewCard() {}
}
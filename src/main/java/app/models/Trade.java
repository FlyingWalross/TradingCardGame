package app.models;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import app.enums.card_type;

@Getter
@Setter
@AllArgsConstructor
public class Trade {
    @JsonAlias({"Id"})
    @JsonProperty("Id")
    String id;
    String username;
    @JsonAlias({"CardToTrade"})
    @JsonProperty("CardToTrade")
    String cardId;
    @JsonAlias({"Type"})
    @JsonProperty("Type")
    card_type type;
    @JsonAlias({"MinimumDamage"})
    @JsonProperty("MinimumDamage")
    float min_damage;

    public Trade(){}

}

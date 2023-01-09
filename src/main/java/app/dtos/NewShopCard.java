package app.dtos;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class NewShopCard {
    @JsonAlias({"Id"})
    @JsonProperty("Id")
    String id;
    @JsonAlias({"Name"})
    @JsonProperty("Name")
    String name;
    @JsonAlias({"Damage"})
    @JsonProperty("Damage")
    float damage;
    @JsonAlias({"Price"})
    @JsonProperty("Price")
    int price;
    public NewShopCard() {}
}
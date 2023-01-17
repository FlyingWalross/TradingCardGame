package app.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
public class UserStats {
        @JsonProperty("Name")
        String name;
        @JsonProperty("Elo")
        int elo;
        @JsonProperty("Wins")
        int wins;
        @JsonProperty("Losses")
        int losses;

        public UserStats() {}
}

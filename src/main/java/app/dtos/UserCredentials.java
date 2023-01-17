package app.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
public class UserCredentials {
        @JsonProperty("Username")
        String username;
        @JsonProperty("Password")
        String passwordPlain;

        public UserCredentials() {}
}

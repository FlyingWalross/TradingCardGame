package app.dtos;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
public class UserCredentials {
        @JsonAlias({"Username"})
        String username;
        @JsonAlias({"Password"})
        String passwordPlain;

        public UserCredentials() {}
}

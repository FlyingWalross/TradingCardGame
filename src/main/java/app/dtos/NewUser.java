package app.dtos;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
public class NewUser {
        @JsonAlias({"username"})
        String username;
        @JsonAlias({"password"})
        String passwordPlain;

        public NewUser() {}
}

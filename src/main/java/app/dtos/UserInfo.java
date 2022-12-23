package app.dtos;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
public class UserInfo {
        @JsonAlias({"Name"})
        @JsonProperty("Name")
        String name;
        @JsonAlias({"Bio"})
        @JsonProperty("Bio")
        String bio;
        @JsonAlias({"Image"})
        @JsonProperty("Image")
        String image;
        public UserInfo() {}
}

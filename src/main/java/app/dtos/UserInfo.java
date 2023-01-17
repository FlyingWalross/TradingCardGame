package app.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
public class UserInfo {
        @JsonProperty("Name")
        String name;
        @JsonProperty("Bio")
        String bio;
        @JsonProperty("Image")
        String image;
        public UserInfo() {}
}

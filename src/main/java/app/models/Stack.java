package app.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
@AllArgsConstructor
public class Stack {
    String username;
    ArrayList<String> cardIDs;

    public Stack(String username) { this.username = username; }
}

package app.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
@AllArgsConstructor
public class Pack {
    int id;
    ArrayList<String> cardIDs;

    public Pack() {
        this.cardIDs = new ArrayList<>();
    }
}

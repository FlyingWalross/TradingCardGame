package app.testModels;

import app.daos.Dao;
import app.daos.StackDao;
import app.models.Stack;

import java.sql.Connection;
import java.util.ArrayList;

public class TestStack {

    public static Stack getTestObject() {
        ArrayList<String> cardIds = new ArrayList<>();
        cardIds.add("5");

        return new Stack(
                "test",
                cardIds
        );
    }
}

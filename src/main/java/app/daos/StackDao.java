package app.daos;

import app.models.Stack;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;


public class StackDao implements Dao<Stack, String> {
    @Getter(AccessLevel.PRIVATE)
    @Setter(AccessLevel.PRIVATE)
    private Connection connection;

    public StackDao(Connection connection) {
        setConnection(connection);
    }

    @Override
    public Stack create(Stack stack) throws SQLException {

        saveStackCards(stack);

        return stack;
    }

    @Override
    public HashMap<String, Stack> read() throws SQLException {
        HashMap<String, Stack> stacks = new HashMap<>();
        String query = "SELECT * FROM user_cards_stack";

        try(PreparedStatement stmt = getConnection().prepareStatement(query)) {
            ResultSet result = stmt.executeQuery();
            while (result.next()) {
                Stack stack = new Stack(
                        result.getString(1), new ArrayList<String>()
                );
                stacks.put(stack.getUsername(), stack);
            }
        }

        query = "SELECT card_id FROM user_cards_stack WHERE username = ?";
        for (Stack stack : stacks.values()) {
            try (PreparedStatement stmt = getConnection().prepareStatement(query)) {
                stmt.setString(1, stack.getUsername());
                ResultSet result = stmt.executeQuery();
                while (result.next()) {
                    stack.getCardIDs().add(result.getString(1));
                }
            }
        }
        return stacks;
    }

    @Override
    public Stack readById(String username) throws SQLException {
        String query = "SELECT card_id FROM user_cards_stack WHERE username=?";
        try (PreparedStatement stmt = getConnection().prepareStatement(query)) {
            stmt.setString(1, username);
            ResultSet result = stmt.executeQuery();
            Stack stack = new Stack(
                    username, new ArrayList<String>()
            );
            while (result.next()) {
                stack.getCardIDs().add(result.getString(1));
            }
            return stack;
        }
    }

    @Override
    public void update(Stack stack) throws SQLException {
        deleteStackCards(stack);
        saveStackCards(stack);
    }

    @Override
    public void delete(Stack stack) throws SQLException {
        deleteStackCards(stack);
    }

    private void saveStackCards(Stack stack) throws SQLException {
        String query = "INSERT INTO user_cards_stack (username, card_id) VALUES (?, ?)";

        for (String cardID : stack.getCardIDs()) {
            try (PreparedStatement stmt = getConnection().prepareStatement(query)) {
                stmt.setString(1, stack.getUsername());
                stmt.setString(2, cardID);
                stmt.executeUpdate();
            }
        }
    }

    private void deleteStackCards(Stack stack) throws SQLException {
        String query = "DELETE FROM user_cards_stack WHERE username = ?";

        try (PreparedStatement stmt = getConnection().prepareStatement(query)) {
            stmt.setString(1, stack.getUsername());
            stmt.executeUpdate();
        }
    }
}

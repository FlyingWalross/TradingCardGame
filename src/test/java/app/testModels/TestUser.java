package app.testModels;

import app.daos.Dao;
import app.daos.UserDao;
import app.models.User;

import java.sql.Connection;
public class TestUser {

    public static User getTestObject() {
        return new User(
                "test",
                "testPw",
                "testName",
                100,
                20,
                0,
                0,
                "testBio",
                "testImage"
        );
    }
}

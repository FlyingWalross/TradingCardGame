package app.testModels;

import app.models.User;

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

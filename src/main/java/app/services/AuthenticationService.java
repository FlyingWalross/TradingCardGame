package app.services;

import app.models.User;
import app.repositories.UserRepository;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import static app.services.EncryptionService.checkPassword;

@Setter(AccessLevel.PRIVATE)
@Getter(AccessLevel.PRIVATE)
public class AuthenticationService {
    private UserRepository userRepository;

    public AuthenticationService(UserRepository userRepository) {
        setUserRepository(userRepository);
    }

    public User authenticateWithPassword(String username, String password){
        User user = getUserRepository().getById(username);
        if(user == null){
            return null;
        }

        if(checkPassword(password, user.getPasswordHash())){
            return user;
        }
        return null;
    }

    public User authenticateWithToken(String token){
        String username = token.split("Bearer ")[1];
        username = username.split("-mtcgToken")[0];
        return getUserRepository().getById(username);
    }

    public String generateToken(User user){
        return user.getUsername() + "-mtcgToken";
    }

}

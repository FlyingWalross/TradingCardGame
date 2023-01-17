package app.services;

import app.dtos.UserProfile;
import app.repositories.UserProfileRepository;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import static app.services.EncryptionService.checkPassword;

@Setter(AccessLevel.PRIVATE)
@Getter(AccessLevel.PRIVATE)
public class AuthenticationService {
    private UserProfileRepository userProfileRepository;

    public AuthenticationService(UserProfileRepository userProfileRepository) {
        setUserProfileRepository(userProfileRepository);
    }

    public UserProfile authenticateWithPassword(String username, String password){
        UserProfile userProfile = getUserProfileRepository().getById(username);
        if(userProfile == null){
            return null;
        }

        if(checkPassword(password, userProfile.getPasswordHash())){
            return userProfile;
        }
        return null;
    }

    public UserProfile authenticateWithToken(String token){
        if(token == null){
            return null;
        }
        String username = token.split("Bearer ")[1];
        username = username.split("-mtcgToken")[0];
        return getUserProfileRepository().getById(username);
    }

    public String generateToken(UserProfile userProfile){
        return userProfile.getUsername() + "-mtcgToken";
    }

}

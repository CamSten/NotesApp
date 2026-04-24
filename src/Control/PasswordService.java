package Control;

import at.favre.lib.crypto.bcrypt.BCrypt;

public class PasswordService {

    public String hashPassword(String userInput){
       return BCrypt.withDefaults().hashToString(12, userInput.toCharArray());
    }
    //boolean verified = passwordService.validatePassword(passwordInput, passwordHash);
    public boolean validatePassword(String userInput, String storedHash){
        BCrypt.Result result = BCrypt.verifyer().verify(userInput.toCharArray(), storedHash);
        return result.verified;
    }
}
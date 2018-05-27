package models.dto;

import models.interfaces.validation.SignUpCheck;
import play.data.validation.Constraints;
import play.data.validation.ValidationError;

import java.util.ArrayList;
import java.util.List;

@Constraints.Validate(groups = {SignUpCheck.class})
public class RegisterDto implements Constraints.Validatable<List<ValidationError>> {

    @Constraints.Required(groups = {SignUpCheck.class})
    @Constraints.MinLength(groups = {SignUpCheck.class}, value = 4, message = "You need at least 4 chars for the username")
    @Constraints.MaxLength(groups = {SignUpCheck.class}, value = 20, message = "Only 20 characters are allowed")
    private String username;

    @Constraints.Required(groups = {SignUpCheck.class}, message = "We need a valid TweeterScreenName")
    private String twitterScreenName;

    @Constraints.MinLength(groups = {SignUpCheck.class}, value = 6, message = "You need at least 6 chars for the password")
    @Constraints.MaxLength(groups = {SignUpCheck.class}, value = 42, message = "Only 42 characters are allowed")
    @Constraints.Required(groups = {SignUpCheck.class})
    @Constraints.Pattern(groups = {SignUpCheck.class}, value = "^(?=.*\\d)(?=.*[a-zA-Z])(?!\\w*$).{8,}", message = "We need a strong password with a minimum length of 6 and at least one uppercase letter, one lowercase letter, one number and one special character.")
    private String password;

    @Constraints.MinLength(groups = {SignUpCheck.class}, value = 6, message = "You need at least 6 chars for the password")
    @Constraints.MaxLength(groups = {SignUpCheck.class}, value = 42, message = "Only 42 characters are allowed")
    @Constraints.Required(groups = {SignUpCheck.class})
    @Constraints.Pattern(groups = {SignUpCheck.class}, value = "^(?=.*\\d)(?=.*[a-zA-Z])(?!\\w*$).{8,}", message = "We need a strong password with a minimum length of 6 and at least one uppercase letter, one lowercase letter, one number and one special character.")
    private String repeatPassword;

    @Constraints.Required(groups = {SignUpCheck.class})
    @Constraints.Email(groups = {SignUpCheck.class})
    private String email;

    @Constraints.Required(groups = {SignUpCheck.class})
    @Constraints.Email(groups = {SignUpCheck.class})
    private String repeatEmail;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRepeatPassword() {
        return repeatPassword;
    }

    public void setRepeatPassword(String repeatPassword) {
        this.repeatPassword = repeatPassword;
    }

    public String getRepeatEmail() {
        return repeatEmail;
    }

    public void setRepeatEmail(String repeatEmail) {
        this.repeatEmail = repeatEmail;
    }

    public String getTwitterScreenName() {
        return twitterScreenName;
    }

    public void setTwitterScreenName(String twitterScreenName) {
        this.twitterScreenName = twitterScreenName;
    }

    @Override
    public List<ValidationError> validate() {
        List<ValidationError> validationErrorList = new ArrayList<>();
        if (!isEqual(password, repeatPassword)) {
            validationErrorList.add(new ValidationError("repeatPassword", "Passwords do not match"));
        }
        if (!isEqual(email, repeatEmail)) {
            validationErrorList.add(new ValidationError("repeatEmail", "Email do not match"));
        }
        return validationErrorList;
    }

    private boolean isEqual(String first, String second) {
        return first.equals(second);
    }

}

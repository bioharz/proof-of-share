package models.dto;

import models.interfaces.validation.SignUpCheck;
import play.data.validation.Constraints;
import play.data.validation.ValidationError;

import javax.validation.groups.Default;
import java.util.ArrayList;
import java.util.List;

@Constraints.Validate(groups = {SignUpCheck.class})
public class Register  implements Constraints.Validatable<List<ValidationError>> {

    @Constraints.Required(groups = {Default.class, SignUpCheck.class/*, LoginCheck.class*/})
    @Constraints.MinLength(value = 4, message ="You need at least 4 chart for the username")
    @Constraints.MaxLength(value = 20, message ="Only 20 characters are allowed")
    //@Constraints.Required
    private String username;


    //@Constraints.Email(groups = {Default.class, SignUpCheck.class})
    //@Constraints.Required

    @Constraints.Required(groups = {SignUpCheck.class /*, LoginCheck.class*/})
    private String password;

    //@Constraints.Required
    @Constraints.Required(groups = {SignUpCheck.class})
    private String repeatPassword;

    @Constraints.Required(groups = {SignUpCheck.class})
    //@Constraints.Required
    @Constraints.Email
    private String email;

    @Constraints.Required(groups = {SignUpCheck.class})
    //@Constraints.Required
    @Constraints.Email
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

    @Override
    public List<ValidationError> validate() {
        List<ValidationError> validationErrorList = new ArrayList<>();
        //ValidationError validationError = new ValidationError("", "");
        if (!isEqual(password, repeatPassword)) {
            validationErrorList.add(new ValidationError("repeatPassword", "Passwords do not match"));
            //return new ValidationError("repeatPassword", "Passwords do not match");
        } if(!isEqual(email, repeatEmail)) {
            validationErrorList.add(new ValidationError("repeatEmail", "Email do not match"));
        }
        return validationErrorList;
    }

    private boolean isEqual(String first, String secound) {
        return first.equals(secound);
    }


}

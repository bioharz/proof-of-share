package models.dto;
import dao.UserDao;
import models.entities.User;
import models.interfaces.validation.SignUpCheck;
import models.interfaces.validation.ValidatableWithDB;
import models.interfaces.validation.ValidateWithDB;
import play.Logger;
import play.data.validation.Constraints;
import play.data.validation.ValidationError;
import play.db.Database;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@Constraints.Validate(groups = {SignUpCheck.class})
@ValidateWithDB
public class Register  implements Constraints.Validatable<List<ValidationError>>, ValidatableWithDB<ValidationError> {

    //@Inject
    //UserDao userDao;

    //@Constraints.Required(groups = {Default.class, SignUpCheck.class/*, LoginCheck.class*/})
    @Constraints.Required(groups = {SignUpCheck.class})
    @Constraints.MinLength(groups = {SignUpCheck.class}, value = 4, message ="You need at least 4 chars for the username")
    @Constraints.MaxLength(groups = {SignUpCheck.class}, value = 20, message ="Only 20 characters are allowed")
    private String username;

    @Constraints.MinLength(groups = {SignUpCheck.class}, value = 6, message ="You need at least 6 chars for the password")
    @Constraints.MaxLength(groups = {SignUpCheck.class}, value = 42, message ="Only 42 characters are allowed")
    @Constraints.Required(groups = {SignUpCheck.class})
    @Constraints.Pattern(groups = {SignUpCheck.class}, value="^(?=.*\\d)(?=.*[a-zA-Z])(?!\\w*$).{8,}", message = "We need a strong password with a minimum length of 6 and at least one uppercase letter, one lowercase letter, one number and one special character.")
    private String password;

    @Constraints.MinLength(groups = {SignUpCheck.class}, value = 6, message ="You need at least 6 chars for the password")
    @Constraints.MaxLength(groups = {SignUpCheck.class}, value = 42, message ="Only 42 characters are allowed")
    @Constraints.Required(groups = {SignUpCheck.class})
    @Constraints.Pattern(groups = {SignUpCheck.class}, value="^(?=.*\\d)(?=.*[a-zA-Z])(?!\\w*$).{8,}", message = "We need a strong password with a minimum length of 6 and at least one uppercase letter, one lowercase letter, one number and one special character.")
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

    @Override
    public List<ValidationError> validate() {



        List<ValidationError> validationErrorList = new ArrayList<>();
        if (!isEqual(password, repeatPassword)) {
            validationErrorList.add(new ValidationError("repeatPassword", "Passwords do not match"));
            //return new ValidationError("repeatPassword", "Passwords do not match");
        } if(!isEqual(email, repeatEmail)) {
            validationErrorList.add(new ValidationError("repeatEmail", "Email do not match"));
        }
        /*if(usernameTaken(username)) {
            validationErrorList.add(new ValidationError("username", "Username is already taken, please use another one"));
        }*/

        return validationErrorList;
    }


    @Override
    public ValidationError validate(final Database db) {
        /*
        TODO:
        I know, this is ugly.. very ugly... And it's actually broken...
         */
        List<User> users = User.find.query().where()
                .ilike("username", username)
                .findPagedList()
                .getList();


        if (!users.isEmpty()) {
            Logger.error("username: "+username+" is already taken, please use another one");
            return new ValidationError("username", "Username is already taken, please use another one.");
        } else {
            Logger.info("username: "+username+" is still available");
        }
        return null;
    }


    private boolean isEqual(String first, String secound) {
        return first.equals(secound);
    }

    /*
    private boolean usernameTaken(String username){

        User user = new User();
        user.setUsername(username);
        user.setPassword("123456Df!");
        this.userDao.updateUser(user);

        if(this.userDao.getUserByUsername(username) != null) {
            return true;
        } else {
            return false;
        }
    }

*/


}

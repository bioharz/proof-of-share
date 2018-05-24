package controllers;

import dao.UserDao;
import middlewares.SessionAuthenticationMiddleware;
import models.dto.ChangePw;
import models.dto.Login;
import models.dto.Register;
import models.entities.User;
import models.interfaces.validation.SignUpCheck;
import play.Logger;
import play.data.Form;
import play.data.FormFactory;
import play.data.validation.ValidationError;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;

import javax.inject.Inject;
import java.util.List;

//@With(BasicAuthenticationMiddleware.class)
public class HomeController extends Controller {

    protected UserDao userDao;
    protected Form<Login> loginForm;
    protected Form<ChangePw> changePwForm;
    protected Form<Register> registerForm;

    @Inject
    public HomeController(
            UserDao userDao,
            FormFactory formFactory) {
        this.userDao = userDao;
        this.loginForm = formFactory.form(Login.class);
        this.changePwForm = formFactory.form(ChangePw.class);
        //this.registerForm = formFactory.form(Register.class);
        //Form<PartialUserForm> form = formFactory().form(PartialUserForm.class, SignUpCheck.class).bindFromRequest();
        this.registerForm = formFactory.form(Register.class, SignUpCheck.class);
    }


    public Result index() {

        User user = getSessionUser(false); //TODO: What's that???
        return ok(views.html.index.render());
    }

    public Result signUpPage() {
        return ok(views.html.signUp.render(registerForm.fill(new Register())));

    }

    public Result signUpPost() {

        User user = getSessionUser(false);
        if (user == null) {
            Form<Register> form = registerForm.bindFromRequest();
            if (!form.hasErrors()) {
                //flash("success", "Data submitted");
                if(userDao.getUserByUsername(form.get().getUsername()) != null) {
                    flash("error", "Username is already taken, please use another one.");
                    return badRequest(views.html.signUp.render(form)); //WTF???????
                }
                User newUser = new User();
                newUser.setUsername(form.get().getUsername());
                newUser.setPasswordInClear(form.get().getPassword());
                newUser.setAdmin(false);
                newUser.setEmail(form.get().getEmail());

                try {
                    userDao.updateUser(newUser);
                    if (newUser.getId() >= 0) {
                        Logger.info("User ID >= : "+newUser.getId());
                    } else {
                        Logger.info("User ID <= : "+newUser.getId()+"!!!");
                    }
                } catch (Exception e) {
                    Logger.error("Error: "+e);
                    flash("error", "Something goes wrong while creating a new user.");
                    return badRequest(views.html.signUp.render(form));
                }
                flash("success", "User created");
                setUserSession(newUser);
                return redirect("/dashboard");
            } else {
                List<ValidationError> errors = form.allErrors();
                StringBuilder errorMessages = new StringBuilder();
                for (ValidationError validationError : errors
                        ) {
                    String errorMessage = validationError.message();
                    Logger.error("signUpPost validation error: " + errorMessage);
                    errorMessages.append(errorMessage + ", ");

                }
                if (errorMessages.length() > 1) {
                    errorMessages.deleteCharAt(errorMessages.length() - 2);
                }
                flash("error", "Data validation error: " + errorMessages.toString());
                return badRequest(views.html.signUp.render(form));
            }
        } else {
            flash("error", "You can't create an new account while you are logged in");
            return badRequest(views.html.signUp.render(registerForm.fill(new Register())));
        }
    }


    public Result login() {
        return ok(views.html.login.render(loginForm.fill(new Login())));
    }

    public Result authenticate() {
        Form<Login> form = loginForm.bindFromRequest();
        if (!form.hasErrors()) {
            Login login = form.get();
            User user = userDao.getUserByUsername(login.username);
            if (user != null) {
                if (user.comparePasswords(login.password)) {
                    setUserSession(user);
                    return redirect(
                            routes.HomeController.index()
                    );
                }
            }
        }
        flash("error", "Bad credentials.");
        return redirect(routes.HomeController.login());
    }


    private void setUserSession(User user){
        session().clear();
        session("username", user.getUsername());
        session("isAdmin", user.getAdmin().toString());
    }


    public Result logout() {
        session().clear();
        flash("success", "You've been logged out");
        return redirect(
                routes.HomeController.login()
        );
    }


    public User getSessionUser(boolean errorMessage) {
        String username = session().get("username");
        User user = userDao.getUserByUsername(username);
        if (user != null) {
            return user;
        }
        if (errorMessage) {
            flash("error", "User not found.");
        }
        return null;
    }

    @Security.Authenticated(SessionAuthenticationMiddleware.class)
    public Result changePwPage() {

        User user = getSessionUser(true);
        if (user != null) {
            return ok(views.html.changePassword.render(changePwForm.fill(new ChangePw())));
        }
        return badRequest(views.html.index.render());
    }

    public Result changePw() {

        Form<ChangePw> form = changePwForm.bindFromRequest();
        if (!form.hasErrors()) {
            ChangePw changePw = form.get();
            User user = getSessionUser(true);
            if (user != null) {
                if (user.comparePasswords(changePw.getPasswordOld())) {

                    if (!changePw.getPasswordNew().equals("") && changePw.getPasswordNew().equals(changePw.getPasswordNewCheck())) {
                        user.setPasswordInClear(changePw.getPasswordNew());
                        userDao.updateUser(user);
                        flash("success", "Password successfully changed.");
                        return redirect(routes.HomeController.index());
                    } else {
                        flash("error", "New password doesn't match...");
                        return redirect(routes.HomeController.changePwPage());
                    }
                } else {
                    flash("error", "Old password doesn't match...");
                    return redirect(routes.HomeController.changePwPage());
                }
            }

        } else {
            flash("error", "Password Change Error.");
            return redirect(routes.HomeController.changePwPage());
        }
        flash("error", "Password Change Error...");
        return redirect(routes.HomeController.changePwPage());
    }

}

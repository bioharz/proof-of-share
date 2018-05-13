package controllers;

import middlewares.SessionAuthenticationMiddleware;
import models.*;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import services.EbeanUserRepository;

import javax.inject.Inject;
import java.util.List;

//@With(BasicAuthenticationMiddleware.class)
public class HomeController extends Controller {

    protected EbeanUserRepository userRepository;
    protected Form<Login> loginForm;
    protected Form<ChangePw> changePwForm;
    protected Form<Register> registerForm;


    @Inject
    public HomeController(
            EbeanUserRepository userRepository,
            FormFactory formFactory) {
        this.userRepository = userRepository;
        this.loginForm = formFactory.form(Login.class);
        this.changePwForm = formFactory.form(ChangePw.class);
        this.registerForm = formFactory.form(Register.class);
    }


    public Result index() {
        User user = getSessionUser(false);
        return ok(views.html.index.render());
    }

    public Result signUp() {
        return ok(views.html.signUp.render(registerForm.fill(new Register())));
    }

    public Result login() {
        return ok(views.html.login.render(loginForm.fill(new Login())));
    }

    public Result authenticate() {
        Form<Login> form = loginForm.bindFromRequest();
        if (!form.hasErrors()) {
            Login login = form.get();
            User user = userRepository.getUserByUsername(login.username);
            if (user != null) {
                if (user.comparePasswords(login.password)) {
                    session().clear();
                    session("username", user.getUsername());
                    session("isAdmin", user.getAdmin().toString());
                    return redirect(
                            routes.HomeController.index()
                    );
                }
            }
        }
        flash("error", "Bad credentials.");
        return redirect(routes.HomeController.login());
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
        User user = userRepository.getUserByUsername(username);
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
                        userRepository.updateUser(user);
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

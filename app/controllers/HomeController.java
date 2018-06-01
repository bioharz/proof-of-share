package controllers;

import controllers.twitter.TwitterGetAPI;
import dao.TwitterBountyCampaignDao;
import dao.UserDao;
import middlewares.SessionAuthenticationMiddleware;
import models.dto.ChangePw;
import models.dto.ClaimSatoshiDto;
import models.dto.Login;
import models.dto.RegisterDto;
import models.entities.TwitterBountyCampaign;
import models.entities.User;
import models.interfaces.validation.SignUpCheck;
import play.Logger;
import play.data.Form;
import play.data.FormFactory;
import play.data.validation.ValidationError;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import twitter4j.Status;

import javax.inject.Inject;
import java.util.List;

//@With(BasicAuthenticationMiddleware.class)
public class HomeController extends Controller {

    protected TwitterBountyCampaignDao twitterBountyCampaignDao;
    protected UserDao userDao;
    protected Form<TwitterBountyCampaign> twitterBountyCampaignForm;
    protected Form<Login> loginForm;
    protected Form<ChangePw> changePwForm;
    protected Form<RegisterDto> registerForm;
    protected TwitterGetAPI twitterGetAPI;
    protected Form<ClaimSatoshiDto> claimSatoshiForm;

    @Inject
    public HomeController(
            TwitterBountyCampaignDao twitterBountyCampaignDao,
            UserDao userDao,
            FormFactory formFactory, TwitterGetAPI twitterGetAPI) {
        this.twitterBountyCampaignDao = twitterBountyCampaignDao;
        this.userDao = userDao;
        this.twitterBountyCampaignForm = formFactory.form(TwitterBountyCampaign.class);
        this.loginForm = formFactory.form(Login.class);
        this.changePwForm = formFactory.form(ChangePw.class);
        //this.registerForm = formFactory.form(RegisterDto.class);
        //Form<PartialUserForm> form = formFactory().form(PartialUserForm.class, SignUpCheck.class).bindFromRequest();
        this.registerForm = formFactory.form(RegisterDto.class, SignUpCheck.class);
        this.twitterGetAPI = twitterGetAPI;
        this.claimSatoshiForm = formFactory.form(ClaimSatoshiDto.class);
    }


    public Result index() {

        User user = getSessionUser(false); //TODO: What's that???
        return ok(views.html.index.render());
    }

    public Result signUpPage() {
        return ok(views.html.signUp.render(registerForm.fill(new RegisterDto())));

    }

    public Result signUpPost() {

        User user = getSessionUser(false);
        if (user == null) {
            Form<RegisterDto> form = registerForm.bindFromRequest();
            if (!form.hasErrors()) {
                if (userDao.getUserByUsername(form.get().getUsername()) != null) {
                    String errorMessage = "Username is already taken, please use another one.";
                    flash("error", errorMessage);
                    Logger.error(errorMessage);
                    return badRequest(views.html.signUp.render(form));
                }
                User newUser = new User();
                newUser.setUsername(form.get().getUsername());
                twitter4j.User twitterUser = twitterGetAPI.getUser(form.get().getTwitterScreenName());
                if (twitterUser == null) {
                    String errorMessage = "Can't find TwitterScreenName.";
                    flash("error", errorMessage);
                    Logger.error(errorMessage);
                    return badRequest(views.html.signUp.render(form));
                }
                newUser.setTwitterScreenName(twitterUser.getScreenName());
                newUser.setPasswordInClear(form.get().getPassword());
                newUser.setAdmin(false);
                newUser.setEmail(form.get().getEmail());

                try {
                    userDao.updateUser(newUser);
                    if (newUser.getId() >= 0) {
                        Logger.info("User ID >= : " + newUser.getId());
                    } else {
                        Logger.info("User ID <= : " + newUser.getId() + "!!!");
                    }
                } catch (Exception e) {
                    Logger.error("Error: " + e);
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
            return badRequest(views.html.signUp.render(registerForm.fill(new RegisterDto())));
        }
    }

    @Security.Authenticated(SessionAuthenticationMiddleware.class)
    public Result dashboard() {
        User user = getSessionUser(true);
        if (user != null) {
            List<TwitterBountyCampaign> twitterBountyCampaign = twitterBountyCampaignDao.getTweets();
            return ok(views.html.dashboard.render(twitterBountyCampaign, user));
        }
        return badRequest(views.html.dashboard.render(null, null));
    }


    @Security.Authenticated(SessionAuthenticationMiddleware.class)
    public Result form(int id) {

        User user = getSessionUser(true);
        if (user != null) {
            TwitterBountyCampaign TwitterBountyCampaign = twitterBountyCampaignDao.getTweet(id, user);
            if (TwitterBountyCampaign == null) {
                TwitterBountyCampaign = new TwitterBountyCampaign();
            }
            return ok(views.html.newTwitterBountyCampaign.render(twitterBountyCampaignForm.fill(TwitterBountyCampaign), user));
        }
        return badRequest(views.html.dashboard.render(null, null));
    }

    @Security.Authenticated(SessionAuthenticationMiddleware.class)
    public Result save() {
        User user = getSessionUser(true);
        if (user != null) {
            Form<TwitterBountyCampaign> form = twitterBountyCampaignForm.bindFromRequest();
            if (form.hasErrors()) {
                return badRequest(views.html.newTwitterBountyCampaign.render(form, user));
            }
            Status status = twitterGetAPI.getStatus(form.get().getTweetId());
            if (status == null) {
                String errorMessage = "Bad Twitter status-ID.";
                flash("error", errorMessage);
                Logger.error(errorMessage);
                return badRequest(views.html.newTwitterBountyCampaign.render(form, user));
            }
            TwitterBountyCampaign twitterBountyCampaign = form.get();
            twitterBountyCampaign.setTwitterScreenName(status.getUser().getScreenName());
            twitterBountyCampaignDao.saveTweets(twitterBountyCampaign, user);
            flash("success", "The TwitterBountyCampaign was successfully saved.");
            return redirect("/dashboard");
        }
        return badRequest(views.html.newTwitterBountyCampaign.render(null, user));
    }

    @Security.Authenticated(SessionAuthenticationMiddleware.class)
    public Result delete(int id) {
        User user = getSessionUser(true);
        if (user != null) {
            twitterBountyCampaignDao.deleteTweet(id, user);
            return ok();
        }
        return badRequest(views.html.dashboard.render(null, null));
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
                            routes.HomeController.dashboard()
                    );
                }
            }
        }
        flash("error", "Bad credentials.");
        return redirect(routes.HomeController.login());
    }


    private void setUserSession(User user) {
        session().clear();
        session("username", user.getUsername());
        session("isAdmin", user.getAdmin().toString());
        session("balance", Long.toString(user.getSatoshiBalance()));
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
            return ok(views.html.changePassword.render(changePwForm.fill(new ChangePw()), user));
        }
        return badRequest(views.html.dashboard.render(null, null));
    }

    @Security.Authenticated(SessionAuthenticationMiddleware.class)
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

    @Security.Authenticated(SessionAuthenticationMiddleware.class)
    public Result balance() {
        User user = getSessionUser(true);
        if (user != null) {
            return ok(views.html.balance.render(claimSatoshiForm, user));
        }
        flash("error", "Can't fetch balance status.");
        return badRequest(views.html.balance.render(claimSatoshiForm, null));
    }

    @Security.Authenticated(SessionAuthenticationMiddleware.class)
    public Result claimSatoshi() {
        User user = getSessionUser(true);
        if (user != null) {
            Form<ClaimSatoshiDto> form = claimSatoshiForm.bindFromRequest();
            if (form.hasErrors()) {
                return badRequest(views.html.balance.render(form, user));
            }
            long satAdded = form.get().getSatoshi();
            long newBalance = user.getSatoshiBalance()+satAdded;
            user.setSatoshiBalance(newBalance);
            userDao.updateUser(user);
            flash("success", satAdded+" Sat added to your account");
            return ok(views.html.balance.render(claimSatoshiForm, user));
        }
        return badRequest(views.html.newTwitterBountyCampaign.render(null, null));
    }

    @Security.Authenticated(SessionAuthenticationMiddleware.class)
    public Result resetBalance() {
        User user = getSessionUser(true);
        if (user != null) {
            user.setSatoshiBalance(0);
            userDao.updateUser(user);
            flash("success", " account balance set to 0 Satoshi");
            return ok(views.html.balance.render(claimSatoshiForm, user));
        }
        return badRequest(views.html.newTwitterBountyCampaign.render(null, null));
    }

}

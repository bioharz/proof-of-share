package controllers;

import middlewares.SessionAuthenticationMiddleware;
import models.Login;
import models.Note;
import models.User;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import services.EbeanCategoryRepository;
import services.EbeanNoteRepository;
import services.EbeanUserRepository;

import javax.inject.Inject;
import java.util.List;

//@With(BasicAuthenticationMiddleware.class)
public class HomeController extends Controller {

    protected EbeanNoteRepository noteRepository;
    protected EbeanCategoryRepository categoryRepository;
    protected EbeanUserRepository userRepository;
    protected Form<Note> noteForm;
    protected Form<Login> loginForm;

    @Inject
    public HomeController(
            EbeanNoteRepository noteRepository,
            EbeanCategoryRepository categoryRepository,
            EbeanUserRepository userRepository,
            FormFactory formFactory) {
        this.noteRepository = noteRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
        this.noteForm = formFactory.form(Note.class);
        this.loginForm = formFactory.form(Login.class);
    }

    @Security.Authenticated(SessionAuthenticationMiddleware.class)
    public Result index() {
        List<Note> notes = noteRepository.getNotes();
        return ok(views.html.index.render(notes));
    }

    @Security.Authenticated(SessionAuthenticationMiddleware.class)
    public Result form(int id) {
        Note note = noteRepository.getNote(id);

        if (note == null) {
            note = new Note();
        }

        return ok(views.html.form.render(noteForm.fill(note), categoryRepository.getCategories()));
    }

    @Security.Authenticated(SessionAuthenticationMiddleware.class)
    public Result save() {
        Form<Note> form = noteForm.bindFromRequest();

        if (form.hasErrors()) {
            return badRequest(views.html.form.render(form, categoryRepository.getCategories()));
        } else {
            noteRepository.saveNote(form.get());
            flash("success", "The note was successfully saved.");
            return redirect("/");
        }
    }

    @Security.Authenticated(SessionAuthenticationMiddleware.class)
    public Result delete(int id) {
        noteRepository.deleteNote(id);
        return ok();
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
}

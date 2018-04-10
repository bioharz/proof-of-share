package controllers;

import middlewares.BasicAuthenticationMiddleware;
import models.Note;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.With;
import services.EbeanCategoryRepository;
import services.EbeanNoteRepository;

import javax.inject.Inject;
import java.util.List;

@With(BasicAuthenticationMiddleware.class)
public class HomeController extends Controller {

    protected EbeanNoteRepository noteRepository;
    protected EbeanCategoryRepository categoryRepository;
    protected Form<Note> noteForm;

    @Inject
    public HomeController(EbeanNoteRepository noteRepository, EbeanCategoryRepository categoryRepository, FormFactory formFactory) {
        this.noteRepository = noteRepository;
        this.categoryRepository = categoryRepository;
        this.noteForm = formFactory.form(Note.class);
    }

    public Result index() {
        List<Note> notes = noteRepository.getNotes();
        return ok(views.html.index.render(notes));
    }

    public Result form(int id) {
        Note note = noteRepository.getNote(id);

        if (note == null) {
            note = new Note();
        }

        return ok(views.html.form.render(noteForm.fill(note), categoryRepository.getCategories()));
    }

    public Result save() {
        Form<Note> form = noteForm.bindFromRequest();

        if(form.hasErrors()) {
            return badRequest(views.html.form.render(form, categoryRepository.getCategories()));
        } else {
            noteRepository.saveNote(form.get());
            flash("success", "The note was successfully saved.");
            return redirect( "/");
        }
    }

    public Result delete(int id) {
        noteRepository.deleteNote(id);
        return ok();
    }

}

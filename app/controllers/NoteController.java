package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import models.entities.Category;
import models.entities.Note;
import play.Logger;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import dao.CategoryDao;
import dao.NoteDao;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

public class NoteController extends Controller {

    @Inject
    protected NoteDao noteRepository;

    @Inject
    protected CategoryDao categoryRepository;


    public Result list(String sort, String query) {
        Stream<Note> note = noteRepository.getNotes().stream();
        if (sort != null && !sort.isEmpty()) {
            if (sort.equals("asc")) {
                note = note.sorted(Comparator.comparing(Note::getTitle));
            } else if (sort.equals("desc")) {
                note = note.sorted(Comparator.comparing(Note::getTitle).reversed());
            }
        }
        if (query != null && !query.isEmpty()) {
            note = note.filter(c -> c.getTitle().toLowerCase().matches("(?i)" + query + ".*"));
        }
        return ok(Json.toJson(note));
    }

    public Result get(int id) {
        Note note = noteRepository.getNote(id);

        if (note == null) {
            return badRequest("note does not exist");
        } else {
            return ok(Json.toJson(note));
        }
    }


    public Result delete(int id) {
        noteRepository.deleteNote(id);
        return ok();
    }


    @BodyParser.Of(BodyParser.Json.class)
    public Result save() {
        JsonNode json = request().body().asJson();

        List<String> required = new ArrayList<>();
        required.add("title");
        required.add("description");
        required.add("categoryId");

        for (String r : required
                ) {
            if (json.findPath(r).textValue() == null ||
                    json.findPath(r).textValue().isEmpty()) {
                return badRequest("a " + r + " is required for notes");
            }
        }

        if (json.findPath("id").textValue() == null
                && json.findPath("id").intValue() > 0
                && noteRepository.getNote(json.findPath("id").intValue()) == null) {
            return badRequest("note does not exist");
        }

        //TODO: filter out last edit...

        Note note = Json.fromJson(json, Note.class);

        Category category = null;
        try {
            category = categoryRepository.getCategory(Integer.parseInt(json.findPath("categoryId").textValue()));

        } catch (Exception e) {
            Logger.error("Error parsing categoryId: " + e);
        }
        if (category == null) {
            return badRequest("Invalid category. Tip: I'm buggy, so I need categoryId as a string");
        } else {
            note.setCategory(category);
        }

        //note.setId(id);
        noteRepository.saveNote(note);

        return ok(Json.toJson(note));

    }

}

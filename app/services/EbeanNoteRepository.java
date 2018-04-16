package services;

import io.ebean.Ebean;
import models.Note;
import models.User;

import java.util.List;

public class EbeanNoteRepository {


    public List<Note> getNotes() {
        return Ebean.find(Note.class).findList();
    }


    public List<Note> getNotes(User user) {
        return Ebean.find(Note.class)
                .where()
                .eq("user", user)
                .findList();
    }

    public Note getNote(int id) {
        return Ebean.find(Note.class)
                    .where()
                    .eq("id", id)
                    .findOne();
    }

    public Note getNote(int id, User user) {
        return Ebean.find(Note.class)
                .where()
                .eq("id", id)
                .eq("user", user)
                .findOne();
    }

    public void saveNote(Note note) {
        note.setLastEdited( (int) (System.currentTimeMillis() / 1000L));

        if (note.getId() > 0) {
            Ebean.update(note);
        } else {
            Ebean.save(note);
        }
    }

    public void deleteNote(int id) {
        Ebean.delete(Note.class, id);
    }

}

package dao;

import io.ebean.Ebean;
import models.entities.Note;
import models.entities.User;
import play.Logger;

import java.util.List;

public class NoteDao {

    @Deprecated
    public List<Note> getNotes() {
        return Ebean.find(Note.class).findList();
    }


    public List<Note> getNotes(User user) {
        if (user.getAdmin()) {
            return Ebean.find(Note.class)
                    .where()
                    .findList();
        } else {
            return Ebean.find(Note.class)
                    .where()
                    .eq("user", user)
                    .findList();
        }
    }

    @Deprecated
    public Note getNote(int id) {
        return Ebean.find(Note.class)
                .where()
                .eq("id", id)
                .findOne();
    }

    public Note getNote(int id, User user) {
        if (user.getAdmin()) {
            return Ebean.find(Note.class)
                    .where()
                    .eq("id", id)
                    .findOne();
        } else {
            return Ebean.find(Note.class)
                    .where()
                    .eq("id", id)
                    .eq("user", user)
                    .findOne();
        }
    }

    @Deprecated
    public void saveNote(Note note) {
        note.setLastEdited((int) (System.currentTimeMillis() / 1000L));

        if (note.getId() > 0) {
            Ebean.update(note);
        } else {
            Ebean.save(note);
        }
    }

    public void saveNote(Note note, User user) {

        note.setLastEdited((int) (System.currentTimeMillis() / 1000L));
        if (user.getAdmin()) {
            if (note.getId() > 0) {
                Ebean.update(note);
            } else {
                Ebean.save(note);
            }
        } else {
            if (note.getId() > 0) {
                Note noteOld = getNote(note.getId());
                if (noteOld.getUser().equals(user)) { //TODO: I have no clue if this will work
                    Ebean.update(note);
                } else {
                    Logger.error("Don't get me wrong, but i think your code is broken. See EbeanNoteRepo, saveNote(id, user)");
                }
            } else {
                note.setUser(user);
                Ebean.save(note);
            }
        }
    }

    @Deprecated
    public void deleteNote(int id) {
        Ebean.delete(Note.class, id);
    }

    public void deleteNote(int id, User user) {
        if (user != null) {
            Note note = getNote(id);
            if (note.getUser().equals(user)) { //TODO: I have no clue if this will work
                Ebean.delete(Note.class, id);
            } else {
                Logger.error("Don't get me wrong, but i think your code is broken. See EbeanNoteRepo, deleteNote(id, user)");
            }
        }
    }

}

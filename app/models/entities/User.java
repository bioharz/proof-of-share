package models.entities;

import io.ebean.Finder;
import io.ebean.Model;
import play.Logger;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import java.security.MessageDigest;
import java.util.List;

@Entity
public class User extends Model {

    @Id
    protected int id;

    @Column(unique = true)
    protected String username;
    protected String password;

    @OneToMany
    protected List<Note> notes;

    @NotNull
    protected Boolean isAdmin = false;

    //TODO
    public void setPasswordInClear(String password) {
        setPassword(getHash(password));
    }

    public boolean comparePasswords(String clearPassword) {
        return getHash(clearPassword).equals(getPassword());
    }

    public static String getHash(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");

            return new String(md.digest(input.getBytes()));

        } catch (Exception e) {
            Logger.error("getHash Error: " + e);
        }

        return input;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

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

    public List<Note> getNotes() {
        return notes;
    }

    public void setNotes(List<Note> notes) {
        this.notes = notes;
    }

    public Boolean getAdmin() {
        return isAdmin;
    }

    public void setAdmin(Boolean admin) {
        isAdmin = admin;
    }

    public static final Finder<Long, User> find = new Finder<>(User.class);



}

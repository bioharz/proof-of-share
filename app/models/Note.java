package models;

import play.data.validation.Constraints;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import java.text.SimpleDateFormat;
import java.util.Date;

@Entity
public class Note {

    @Id
    protected int id = 0;

    @Constraints.Required(message = "Please add a title")
    @Constraints.MaxLength(value = 20, message ="Only 20 characters are allowed")
    protected String title = "";

    @Constraints.Required(message = "Please add a description")
    @Constraints.MaxLength(value = 200, message ="Only 200 characters are allowed")
    protected String description = "";
    protected int lastEdited;

    @NotNull
    @ManyToOne
    protected Category category;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getLastEdited() {
        return lastEdited;
    }

    public void setLastEdited(int lastEdited) {
        this.lastEdited = lastEdited;
    }

    public String getLastEditedFormatted() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date(this.lastEdited * 1000L));
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
}

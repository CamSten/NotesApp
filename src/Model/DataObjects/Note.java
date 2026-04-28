package Model.DataObjects;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Note {
    private final int id;
    private String username;
    private String title;
    private String contents;
    private final LocalDateTime submitDate;
    private LocalDateTime lastEditDate;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM yyyy HH:mm");

    public Note(int id, String title, String contents, LocalDateTime date) {
        this.id = id;
        this.title = title;
        this.contents = contents;
        this.submitDate = date;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public String getSubmitDate() {
        return formatter.format(submitDate);
    }

    public String getLastEditDate() {
        return formatter.format(lastEditDate);
    }

    public void setLastEditDate(LocalDateTime lastEditDate) {
        this.lastEditDate = lastEditDate;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
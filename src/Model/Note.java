package Model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Note {
    private int id;
    private String title;
    private String contents;
    private LocalDateTime date;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM yyyy HH:mm");

    public Note(int id, String title, String contents, LocalDateTime date) {
        this.id = id;
        this.title = title;
        this.contents = contents;
        this.date = date;
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

    public String getDate() {
        return formatter.format(date);
    }
}
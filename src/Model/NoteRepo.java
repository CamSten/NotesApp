package Model;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class NoteRepo {
    private static Connection c;

    public NoteRepo(Connection c){
        this.c = c;
    }
    private List<Note> getNotes(int thisUserId) throws SQLException {
        ResultSet rs = null;
        List<Note> notes = new ArrayList<>();
        PreparedStatement s = c.prepareStatement("SELECT * FROM Note WHERE userId = id = ");
        s.setInt(1, thisUserId);
        rs = s.executeQuery();
        while (rs.next()) {
            String title = rs.getString("title");
            String contents = rs.getString("contents");
            LocalDateTime date = rs.getTimestamp("date").toLocalDateTime();
            notes.add(new Note(title, contents, date));
        }
        return notes;
    }
    private boolean editNote(int thisUserId, int thisNoteId, String newTitle, String newContents) throws SQLException {
        boolean success = false;
        CallableStatement s = c.prepareCall("CALL editNote(?, ?, ?, ?, ?, ?)");
        s.setInt(1, thisUserId);
        s.setInt(2, thisNoteId);
        s.setString(3, newTitle);
        s.setString(4, newContents);
        boolean noSuchUser = s.getBoolean(5);
        boolean noSuchNote = s.getBoolean(6);
        return success = (!noSuchUser && !noSuchNote);
    }

    private boolean deleteNotes(int thisUserId, int thisNoteId, boolean deleteAll) throws SQLException {
        boolean success = false;
        CallableStatement s = c.prepareCall("CALL deleteNoteForUser(?, ?, ?, ?, ?)");
        s.setInt(1, thisUserId);
        s.setInt(2, thisNoteId);
        s.setBoolean(3, deleteAll);
        boolean noSuchUser = s.getBoolean(4);
        boolean noSuchNote = s.getBoolean(5);
        return success = (!noSuchUser && !noSuchNote);
    }

    private boolean deleteAllNotes(int userId) throws SQLException {
        // move admin validation to Java?

        CallableStatement s = c.prepareCall("CALL deleteNoteForUser(?, ?)");
        s.setInt(1, userId);
        return s.getBoolean(2);
    }
}

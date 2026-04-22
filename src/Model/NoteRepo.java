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

    public boolean addNote(int userId, String title, String contents) throws SQLException {
        CallableStatement s = c.prepareCall("CALL addNote(?, ?, ?)");
        s.setInt(1, userId);
        s.setString(2, title);
        s.setString(3, contents);
        return s.execute();
    }
    public List<Note> getNotes(int thisUserId) throws SQLException {
        List<Note> notes = new ArrayList<>();
        PreparedStatement s = c.prepareStatement("SELECT * FROM Note WHERE userId = ? ");
        s.setInt(1, thisUserId);
        ResultSet rs = s.executeQuery();
        while (rs.next()) {
            int id = rs.getInt("id");
            String title = rs.getString("title");
            String contents = rs.getString("contents");
            LocalDateTime date = rs.getTimestamp("submitDate").toLocalDateTime();
            notes.add(new Note(id, title, contents, date));
        }
        return notes;
    }
    public boolean editNote(int thisUserId, int thisNoteId, String newTitle, String newContents) throws SQLException {
        CallableStatement s = c.prepareCall("CALL editNote(?, ?, ?, ?, ?, ?)");
        s.setInt(1, thisUserId);
        s.setInt(2, thisNoteId);
        s.setString(3, newTitle);
        s.setString(4, newContents);
        s.execute();
        boolean noSuchUser = s.getBoolean(5);
        boolean noSuchNote = s.getBoolean(6);
        return (!noSuchUser && !noSuchNote);
    }
    public boolean deleteNotes(int thisUserId, int thisNoteId, boolean deleteAll) throws SQLException {
        CallableStatement s = c.prepareCall("CALL deleteNotesForUser(?, ?, ?)");
        s.setInt(1, thisUserId);
        s.setInt(2, thisNoteId);
        s.setBoolean(3, deleteAll);
        int affectedRows = s.executeUpdate();
        return affectedRows >0;
    }

    public boolean deleteAllNotes(int userId) throws SQLException {
        CallableStatement s = c.prepareCall("CALL deleteNoteForUser(?, ?)");
        s.setInt(1, userId);
        return s.getBoolean(2);
    }
}
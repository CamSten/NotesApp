package Model.Repositories;

import Model.DataObjects.Note;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

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
        int affectedRows = s.executeUpdate();
        return affectedRows > 0;
    }
    public List<Note> getNotes(int thisUserId, boolean allNotes) throws SQLException {
        List<Note> notes = new ArrayList<>();
        PreparedStatement  s = c.prepareStatement("SELECT * from notes_view");
        if (!allNotes){
            s = c.prepareStatement("SELECT * from notes_view where userId = ?");
            s.setInt(1, thisUserId);
        }
        ResultSet rs =  s.executeQuery();
        while (rs.next()) {
            int id = rs.getInt("noteId");
            String name = rs.getString("username");
            String title = rs.getString("title");
            String contents = rs.getString("contents");
            LocalDateTime submitDate = rs.getTimestamp("entryDate").toLocalDateTime();
            LocalDateTime editDate = rs.getTimestamp("editDate").toLocalDateTime();
            Note note = new Note(id, title, contents, submitDate);
            note.setUsername(name);
            note.setLastEditDate(editDate);
            notes.add(note);
        }
        return notes;
    }

    public int editNote(int thisUserId, int thisNoteId, String newTitle, String newContents) throws SQLException {
        CallableStatement s = c.prepareCall("CALL editNote(?, ?, ?, ?)");
        s.setInt(1, thisUserId);
        s.setInt(2, thisNoteId);
        s.setString(3, newTitle);
        s.setString(4, newContents);
        return s.executeUpdate();
    }
    public boolean deleteNotes(int thisUserId, int thisNoteId, boolean deleteAll) throws SQLException {
        CallableStatement s = c.prepareCall("CALL deleteNotesForUser(?, ?, ?)");
        s.setInt(1, thisUserId);
        s.setInt(2, thisNoteId);
        s.setBoolean(3, deleteAll);
        int affectedRows = s.executeUpdate();
        return affectedRows > 0;
    }
    public boolean deleteAllNotes() throws SQLException {
        CallableStatement s = c.prepareCall("CALL deleteAllNotes");
        int affectedRows = s.executeUpdate();
        return affectedRows > 0;
    }
}
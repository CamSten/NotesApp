package Model.Repositories;

import Control.Enums.LoginStatus;
import Model.DataObjects.LogPost;
import Model.DataObjects.NoteLog;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

public class AdminRepo {
    private static Connection c;

    public AdminRepo(Connection c){
        this.c = c;
    }

    public List<LogPost> getLoginData() throws SQLException {
        List<LogPost> logPosts = new ArrayList<>();
        PreparedStatement s = c.prepareStatement("SELECT * from Login");
        ResultSet rs = s.executeQuery();
        while (rs.next()){
            LocalDateTime date = rs.getTimestamp("date").toLocalDateTime();
            int userId = rs.getInt("userId");
            String status = rs.getString("loginStatus");
            logPosts.add(new LogPost(date, userId, status));
        }
        return logPosts;
    }

    public List<LogPost> getLoginDataForUser(int id) throws SQLException {
        List<LogPost> logPosts = new ArrayList<>();
        PreparedStatement s = c.prepareStatement("SELECT * from Login where userId = ?");
        s.setInt(1, id);
        ResultSet rs = s.executeQuery();
        while (rs.next()){
            LocalDateTime date = rs.getTimestamp("date").toLocalDateTime();
            String status = rs.getString("loginStatus");
            logPosts.add(new LogPost(date, id, status));
        }
        return logPosts;
    }

    public List<LogPost> getLoginDataForStatus(LoginStatus status) throws SQLException {
        List<LogPost> logPosts = new ArrayList<>();
        PreparedStatement s = c.prepareStatement("SELECT * from Login where loginStatus = ?");
        s.setString(1, String.valueOf(status));
        ResultSet rs = s.executeQuery();
        while (rs.next()){
            LocalDateTime date = rs.getTimestamp("date").toLocalDateTime();
            int userId = rs.getInt("userId");
            logPosts.add(new LogPost(date, userId, String.valueOf(status)));
        }
        return logPosts;
    }

    public List<NoteLog> getNotesLog() throws SQLException {
        List<NoteLog> noteLogs = new ArrayList<>();
        PreparedStatement s = c.prepareStatement("SELECT * from NoteLog");
        ResultSet rs = s.executeQuery();
        while (rs.next()){
            LocalDateTime date = rs.getTimestamp("date").toLocalDateTime();
            int actorUserId = rs.getInt("actorUserId");
            int noteId = rs.getInt("noteId");
            String action = rs.getString("noteAction");
            noteLogs.add(new NoteLog(date, actorUserId, noteId, action));
        }
        return noteLogs;
    }
    public List<NoteLog> getNotesLogForActor(int id) throws SQLException {
        List<NoteLog> noteLogs = new ArrayList<>();
        PreparedStatement s = c.prepareStatement("SELECT * from NoteLog where actorUserId = ?");
        s.setInt(1, id);
        ResultSet rs = s.executeQuery();
        while (rs.next()){
            LocalDateTime date = rs.getTimestamp("date").toLocalDateTime();
            int noteId = rs.getInt("noteId");
            String action = rs.getString("noteAction");
            noteLogs.add(new NoteLog(date, id, noteId, action));
        }
        return noteLogs;
    }
}
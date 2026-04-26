package Model;

import Control.NoteAction;
import View.UserMenu;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class NoteLog {
    private LocalDateTime date;
    private int actorUserId;
    private int noteId;
    private NoteAction noteAction;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM yyyy HH:mm");


    public NoteLog(LocalDateTime date, int actorUserId, int noteId, String action) {
        this.date = date;
        this.actorUserId = actorUserId;
        this.noteId = noteId;
        setNoteAction(action);
    }

    public String getDate() {
        return formatter.format(date);
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public int getActorUserId() {
        return actorUserId;
    }

    public void setActorUserId(int actorUserId) {
        this.actorUserId = actorUserId;
    }

    public int getNoteId() {
        return noteId;
    }

    public void setNoteId(int noteId) {
        this.noteId = noteId;
    }

    public NoteAction getNoteAction() {
        return noteAction;
    }

    public void setNoteAction(String noteAction) {
        NoteAction action = null;
        switch (noteAction.toLowerCase()){
            case "read" -> action = NoteAction.READ;
            case "add" -> action = NoteAction.ADD;
            case "edit" -> action = NoteAction.EDIT;
            case "delete" -> action = NoteAction.REMOVE;
            case "remove_all"-> action = NoteAction.REMOVE_ALL;
        }
    this.noteAction = action;
    }
}

package View;

import Model.DataObjects.LogPost;
import Model.DataObjects.Note;
import Model.DataObjects.NoteLog;
import Model.DataObjects.User;

public class ConsoleOutput {

    public void printHeader(String headerText){
        StringBuilder header = new StringBuilder();
        header.append("\n=== ");
        header.append(headerText);
        header.append(" ===\n");
        System.out.println(header);
    }
    public String getUserPostString(User u, boolean allInfo){
        StringBuilder post = new StringBuilder();
        if (!allInfo){
            post.append("User nr.");
            post.append(u.getId());
            post.append(":\n  Username: ");
            post.append(u.getUsername());
            post.append("\n  Date of registration: ");
            post.append(u.getRegDate());
            post.append("\n");
        }
        return post.toString();
    }

    public String getLogPostString(LogPost log){
        StringBuilder post = new StringBuilder();
        post.append("Date of action: ");
        post.append(log.getDate());
        post.append("\n  Action performed by user with id: ");
        post.append(log.getUserId());
        post.append("\n  Status: ");
        post.append(log.getLoginStatus());
        post.append("\n");
        return post.toString();
    }

    public String getNoteLogString(NoteLog log){
        StringBuilder post = new StringBuilder();
        post.append("Action performed by user with id: ");
        post.append(log.getActorUserId());
        post.append("\n  Date of action: ");
        post.append(log.getDate());
        post.append("\n  Note id:");
        post.append(log.getNoteId());
        post.append("\n  Action: ");
        post.append(log.getNoteAction());
        post.append("\n");
        return post.toString();
    }
    public String getNotePostString(Note n, int index, boolean allInfo, boolean allNotes, boolean isAdmin){
        StringBuilder post = new StringBuilder();
        String numberLabel = isAdmin ? "Note id: " : "Note nr. ";
        int noteNumber = allNotes ? n.getId() : index ;
        if (index >-1) {
            post.append(numberLabel);
            post.append(noteNumber);
            post.append(":");
        }
        if (allNotes) {
            post.append("  User: ");
            post.append(n.getUsername());
        }
        post.append("\n  Date: ");
        post.append(n.getSubmitDate());
        post.append("\n  Title: ");
        post.append(n.getTitle());
        if (allInfo) {
            post.append("\n  Contents: ");
            post.append(n.getContents());
        }
        post.append("\n");
        return post.toString();
    }
    public void prompt(String prompt) {
        System.out.println("\n" +prompt);
    }

    public void promptInvalid(){
        System.out.println(invalidChoice);
    }
    private final String invalidChoice = "You haven't submitted av valid choice. Please try again.\n";
}
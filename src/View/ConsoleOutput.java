package View;

import Control.Prompts;
import Model.LogPost;
import Model.Note;
import Model.NoteLog;
import Model.User;

public class ConsoleOutput {

    public String getUserPostString(User u, boolean allInfo){
        StringBuilder post = new StringBuilder();
        if (!allInfo){
            post.append("User nr.");
            post.append(u.getId());
            post.append(":\nUsername: ");
            post.append(u.getUsername());
            post.append("\nDate of registration: ");
            post.append(u.getRegDate());
            post.append("\n");
        }
        return post.toString();
    }

    public String getLogPostString(LogPost log){
        StringBuilder post = new StringBuilder();
        post.append("Date of action: ");
        post.append(log.getDate());
        post.append("\nAction performed by user with id: ");
        post.append(log.getUserId());
        post.append("\nStatus: ");
        post.append(log.getLoginStatus());
        post.append("\n");
        return post.toString();
    }

    public String getNoteLogString(NoteLog log){
        StringBuilder post = new StringBuilder();
        post.append("Action performed by user with id: ");
        post.append(log.getActorUserId());
        post.append("\nDate of action: ");
        post.append(log.getDate());
        post.append("\nNote id:");
        post.append(log.getNoteId());
        post.append("\nAction: ");
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
            post.append("User: ");
            post.append(n.getUsername());
        }
        post.append("\nDate: ");
        post.append(n.getSubmitDate());
        post.append("\nTitle: ");
        post.append(n.getTitle());
        if (allInfo) {
            post.append("\nContents: ");
            post.append(n.getContents());
        }
        post.append("\n");
        return post.toString();
    }
    public void prompt(Prompts prompt) {
        System.out.println(prompt.toString());
    }

    public void promptInvalid(){
        System.out.println(invalidChoice);
    }
    private final String invalidChoice = "You haven't submitted av valid choice. Please try again.\n";
}
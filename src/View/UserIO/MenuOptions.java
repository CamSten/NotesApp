package View.UserIO;

import java.util.*;

public class MenuOptions {
    public MenuOptions() {
    }
    public Map<String, SQLRunnable> createMenu(List<String> keys, List<SQLRunnable> toPerform){
        Map<String, SQLRunnable> menuOptions = new HashMap<>();
        if(keys.size() != toPerform.size()){
            throw new IllegalArgumentException("Keys and actions must match");
        }
        for (int i = 0; i < keys.size(); i++) {
            SQLRunnable action = toPerform.get(i);
            menuOptions.put(keys.get(i), action);
        }
        return menuOptions;
    }

    public Map<String, SQLRunnableVoid> createMenuVoid(List<String> keys, List<SQLRunnableVoid> toPerformVoid){
        Map<String, SQLRunnableVoid> menuOptionsVoid = new HashMap<>();
        if(keys.size() != toPerformVoid.size()){
            throw new IllegalArgumentException("Keys and actions must match");
        }
        for (int i = 0; i < keys.size(); i++) {
            SQLRunnableVoid action = toPerformVoid.get(i);
            menuOptionsVoid.put(keys.get(i), action);
        }
        return menuOptionsVoid;
    }
}
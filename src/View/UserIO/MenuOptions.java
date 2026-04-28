package View.UserIO;

import java.util.*;

public class MenuOptions {
    private Map<String, SQLRunnable> menuOptions;
    private Map<String, SQLRunnableVoid> menuOptionsVoid;
    private List<String> keys;
    private List<String> keysVoid;
    private List<SQLRunnable> toPerform;
    private List<SQLRunnableVoid> toPerformVoid;

    public MenuOptions(List<String> input, List<SQLRunnable> toPerform) {
        this.keys = input;
        this.toPerform = toPerform;
        if(keys.size() != toPerform.size()){
            throw new IllegalArgumentException("Keys and actions must match");
        }
    }
    public MenuOptions() {

    }
    public Map<String, SQLRunnableVoid> getMenuOptions(List<String> input, List<SQLRunnableVoid> toPerformVoid){
        this.keysVoid = input;
        this.toPerformVoid = toPerformVoid;
        if(keysVoid.size() != toPerformVoid.size()){
            throw new IllegalArgumentException("Keys and actions must match");
        }
        return createMenuVoid();
    }

    public Map<String, SQLRunnable> createMenu() {
        this.menuOptions = new HashMap<>();
        for (int i = 0; i < keys.size(); i++) {
            SQLRunnable action = toPerform.get(i);
            menuOptions.put(keys.get(i), action);
        }
        return menuOptions;
    }
    public Map<String, SQLRunnableVoid> createMenuVoid(){
        this.menuOptionsVoid = new HashMap<>();
        for (int i = 0; i < keysVoid.size(); i++) {
            SQLRunnableVoid action = toPerformVoid.get(i);
            menuOptionsVoid.put(keysVoid.get(i), action);
        }
        return menuOptionsVoid;
    }
}
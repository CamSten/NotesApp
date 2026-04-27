package View.UserIO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MenuOptions {
    Map<String, SQLRunnable> menuOptions;
    private final List<String> keys;
    private final List<SQLRunnable> toPerform;

    public MenuOptions(List<String> input, List<SQLRunnable> toPerform) {
        this.keys = input;
        this.toPerform = toPerform;
        if(keys.size() != toPerform.size()){
            throw new IllegalArgumentException("Keys and actions must match");
        }
    }

    public Map<String, SQLRunnable> createMenu() {
        this.menuOptions = new HashMap<>();
        for (int i = 0; i < keys.size(); i++) {
            SQLRunnable action = toPerform.get(i);
            menuOptions.put(keys.get(i), action);
        }
        return menuOptions;
    }
}
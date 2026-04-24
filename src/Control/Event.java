package Control;

public class Event {
    Object data;
    Prompts prompts;

    public Event(Object data, Prompts prompts) {
        this.data = data;
        this.prompts = prompts;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public Prompts getPrompts() {
        return prompts;
    }

    public void setPrompts(Prompts prompts) {
        this.prompts = prompts;
    }
}

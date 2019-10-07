package core.api;

import java.util.ArrayList;

public class Response {
    public int __uid;
    private ArrayList<Object> commands = new ArrayList<>();

    public void addCommand(Object command) {
        commands.add(command);
    }
}

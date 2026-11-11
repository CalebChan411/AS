/** Common interface for the eleven operations shown in the class diagram. */
public interface Command {
    void execute();
    void undo();
    void redo();
    String displayName();
    boolean isUndoable();
}

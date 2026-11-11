/** Default behaviour for commands which do not change undoable state. */
public abstract class BaseCommand implements Command {
    public void undo() { }

    public void redo() { }

    public boolean isUndoable() {
        return false;
    }

    public abstract void execute();

    public abstract String displayName();
}

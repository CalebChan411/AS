/** Shared receiver and contract for the five state-changing commands. */
public abstract class UndoableCommand extends BaseCommand {
    protected FleetManager manager;

    public UndoableCommand(FleetManager manager) {
        this.manager = manager;
    }

    public boolean isUndoable() {
        return true;
    }

    public abstract void undo();

    public abstract void redo();
}

/** Delegates the history operation to the invoker without recording itself. */
public class ListCommand extends BaseCommand {
    private CommandInvoker invoker;

    public ListCommand(CommandInvoker invoker) {
        this.invoker = invoker;
    }

    public void execute() {
        invoker.showUndoRedoList();
    }

    public String displayName() {
        return "List undo/redo";
    }
}

/** Delegates the history operation to the invoker without recording itself. */
public class UndoCommand extends BaseCommand {
    private CommandInvoker invoker;

    public UndoCommand(CommandInvoker invoker) {
        this.invoker = invoker;
    }

    public void execute() {
        invoker.undo();
    }

    public String displayName() {
        return "Undo last command";
    }
}

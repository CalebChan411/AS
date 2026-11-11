/** Delegates the history operation to the invoker without recording itself. */
public class RedoCommand extends BaseCommand {
    private CommandInvoker invoker;

    public RedoCommand(CommandInvoker invoker) {
        this.invoker = invoker;
    }

    public void execute() {
        invoker.redo();
    }

    public String displayName() {
        return "Redo last undone command";
    }
}

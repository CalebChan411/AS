/** Creates the request; undo history is changed only when it is executed. */
public class UndoCommandFactory implements CommandFactory {
    private CommandInvoker invoker;

    public UndoCommandFactory(CommandInvoker invoker) {
        this.invoker = invoker;
    }

    public Command createCommand() {
        return new UndoCommand(invoker);
    }
}

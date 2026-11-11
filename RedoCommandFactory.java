/** Creates a redo request, leaving the invoker to reuse the history command. */
public class RedoCommandFactory implements CommandFactory {
    private CommandInvoker invoker;

    public RedoCommandFactory(CommandInvoker invoker) {
        this.invoker = invoker;
    }

    public Command createCommand() {
        return new RedoCommand(invoker);
    }
}

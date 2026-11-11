/** Creates a history-list request without printing or changing the stacks. */
public class ListCommandFactory implements CommandFactory {
    private CommandInvoker invoker;

    public ListCommandFactory(CommandInvoker invoker) {
        this.invoker = invoker;
    }

    public Command createCommand() {
        return new ListCommand(invoker);
    }
}

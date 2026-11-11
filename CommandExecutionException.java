/** Signals a command validation or execution failure. */
public class CommandExecutionException extends RuntimeException {
    public CommandExecutionException(String message) {
        super(message);
    }
}

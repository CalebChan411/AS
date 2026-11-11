import java.util.Stack;

/** Maintains LIFO history. Only successful undoable commands affect the stacks. */
public class CommandInvoker {
    private Stack<Command> undoStack;
    private Stack<Command> redoStack;

    public CommandInvoker() {
        undoStack = new Stack<Command>();
        redoStack = new Stack<Command>();
    }

    public void execute(Command command) {
        if (command == null) return;
        try {
            command.execute();
            if (command.isUndoable()) {
                undoStack.push(command);
                redoStack.clear();
            }
        } catch (CommandExecutionException ex) {
            System.out.println(ex.getMessage());
        } catch (IllegalArgumentException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void undo() {
        if (undoStack.empty()) {
            System.out.println("Nothing to undo.");
            return;
        }
        Command command = undoStack.peek();
        try {
            command.undo();
            // Move the entry only after restoration succeeds.
            undoStack.pop();
            redoStack.push(command);
            System.out.println("Command (" + command.displayName() + ") is undone.");
        } catch (CommandExecutionException ex) {
            System.out.println(ex.getMessage());
        } catch (IllegalArgumentException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void redo() {
        if (redoStack.empty()) {
            System.out.println("Nothing to redo.");
            return;
        }
        Command command = redoStack.peek();
        try {
            command.redo();
            redoStack.pop();
            undoStack.push(command);
            System.out.println("Command (" + command.displayName() + ") is redone.");
        } catch (CommandExecutionException ex) {
            System.out.println(ex.getMessage());
        } catch (IllegalArgumentException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void showUndoRedoList() {
        System.out.println("Undo List");
        for (Command command : undoStack) {
            System.out.println(command.displayName());
        }
        System.out.println("-- End of undo list --");
        System.out.println("Redo List");
        for (Command command : redoStack) {
            System.out.println(command.displayName());
        }
        System.out.println("-- End of redo list --");
    }
}

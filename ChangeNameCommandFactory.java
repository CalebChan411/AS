/** Creates a rename request; the command saves the previous name on execution. */
public class ChangeNameCommandFactory implements CommandFactory {
    private FleetManager manager;
    private ConsoleInput input;

    public ChangeNameCommandFactory(FleetManager manager, ConsoleInput input) {
        this.manager = manager;
        this.input = input;
    }

    public Command createCommand() {
        String newName = input.readLine("Please input new name of the current fleet:- ");
        if (newName == null) return null;
        return new ChangeNameCommand(manager, newName.trim());
    }
}

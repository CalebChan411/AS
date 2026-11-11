/** Creates a deletion request without removing its target driver. */
public class DeleteDriverCommandFactory implements CommandFactory {
    private FleetManager manager;
    private ConsoleInput input;

    public DeleteDriverCommandFactory(FleetManager manager, ConsoleInput input) {
        this.manager = manager;
        this.input = input;
    }

    public Command createCommand() {
        String driverID = input.readLine("Please input driver ID:- ");
        if (driverID == null) return null;
        return new DeleteDriverCommand(manager, driverID.trim());
    }
}

/** Creates a selection command from this request's fleet ID. */
public class SetCurrentFleetCommandFactory implements CommandFactory {
    private FleetManager manager;
    private ConsoleInput input;

    public SetCurrentFleetCommandFactory(FleetManager manager, ConsoleInput input) {
        this.manager = manager;
        this.input = input;
    }

    public Command createCommand() {
        String fleetID = input.readLine("Please input fleet ID:- ");
        if (fleetID == null) return null;
        return new SetCurrentFleetCommand(manager, fleetID.trim());
    }
}

/** Reads one create request and creates its command without changing any fleet. */
public class CreateFleetCommandFactory implements CommandFactory {
    private FleetManager manager;
    private FleetFactoryRegistry fleetRegistry;
    private ConsoleInput input;

    public CreateFleetCommandFactory(FleetManager manager, FleetFactoryRegistry fleetRegistry,
                                     ConsoleInput input) {
        this.manager = manager;
        this.fleetRegistry = fleetRegistry;
        this.input = input;
    }

    public Command createCommand() {
        String type = input.readLine("Enter fleet type (v = delivery van | r = refrigerated truck) :- ");
        if (type == null) return null;
        String fleetID = input.readLine("Fleet ID:- ");
        if (fleetID == null) return null;
        String fleetName = input.readLine("Fleet Name:- ");
        if (fleetName == null) return null;
        return new CreateFleetCommand(manager, fleetRegistry, type.trim().toLowerCase(),
                                      fleetID.trim(), fleetName.trim());
    }
}

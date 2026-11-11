/** Uses polymorphic display supplied by the original concrete fleet class. */
public class ShowFleetCommand extends BaseCommand {
    private FleetManager manager;

    public ShowFleetCommand(FleetManager manager) {
        this.manager = manager;
    }

    public void execute() {
        VehicleFleet fleet = manager.getCurrentFleet();
        if (fleet == null) throw new CommandExecutionException("No current fleet selected.");
        fleet.showFleet();
    }

    public String displayName() {
        return "Show current fleet";
    }
}

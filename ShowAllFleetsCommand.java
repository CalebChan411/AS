/** Displays every fleet through its existing polymorphic showFleet operation. */
public class ShowAllFleetsCommand extends BaseCommand {
    private FleetManager manager;

    public ShowAllFleetsCommand(FleetManager manager) {
        this.manager = manager;
    }

    public void execute() {
        if (manager.getFleetCount() == 0) {
            System.out.println("No fleets available.");
            return;
        }
        for (VehicleFleet fleet : manager.getFleets()) fleet.showFleet();
    }

    public String displayName() {
        return "Display all fleets";
    }
}

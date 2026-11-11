/** Displays fleet summaries using names supplied by each concrete factory. */
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
        for (VehicleFleet fleet : manager.getFleets()) {
            FleetFactory factory = manager.getFleetFactory(fleet);
            System.out.println(factory.getFleetTypeName() + " " + fleet.getName()
                    + " (" + fleet.getFleetID() + ")");
        }
    }

    public String displayName() {
        return "Display all fleets";
    }
}

/** Changes only the current selection, so it does not enter undo history. */
public class SetCurrentFleetCommand extends BaseCommand {
    private FleetManager manager;
    private String fleetID;

    public SetCurrentFleetCommand(FleetManager manager, String fleetID) {
        this.manager = manager;
        this.fleetID = fleetID;
    }

    public void execute() {
        VehicleFleet fleet = manager.findFleet(fleetID);
        if (fleet == null) throw new CommandExecutionException("Fleet " + fleetID + " is not found!");
        manager.setCurrentFleet(fleet);
        System.out.println("Changed current fleet to " + fleet.getFleetID() + ".");
    }

    public String displayName() {
        return "Set current fleet, " + fleetID;
    }
}

import java.util.List;

/** Retains the deleted object and its original order without changing VehicleFleet. */
public class DeleteDriverCommand extends UndoableCommand {
    private String driverID;
    private VehicleFleet fleet;
    private Driver driver;
    private List<Driver> originalDriverOrder;

    public DeleteDriverCommand(FleetManager manager, String driverID) {
        super(manager);
        this.driverID = driverID;
    }

    public void execute() {
        fleet = manager.getCurrentFleet();
        if (fleet == null) throw new CommandExecutionException("No current fleet selected.");
        driver = FleetUtil.findDriver(fleet, driverID);
        if (driver == null) {
            throw new CommandExecutionException("Driver " + driverID + " is not found in the current fleet!");
        }
        originalDriverOrder = FleetUtil.getAllDrivers(fleet);
        redo();
        System.out.println("Driver is deleted.");
    }

    public void undo() {
        if (originalDriverOrder == null || FleetUtil.findDriver(fleet, driverID) != null) {
            throw new CommandExecutionException("Unable to undo deleting driver.");
        }
        FleetUtil.restoreDriverOrder(fleet, originalDriverOrder);
    }

    public void redo() {
        if (driver == null || FleetUtil.findDriver(fleet, driverID) != driver) {
            throw new CommandExecutionException("Unable to redo deleting driver.");
        }
        fleet.dropDriver(driver);
    }

    public String displayName() {
        return "Delete driver, " + driverID;
    }
}

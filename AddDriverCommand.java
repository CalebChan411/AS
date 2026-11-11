/** Adds a factory-created driver to the fleet captured on first execution. */
public class AddDriverCommand extends UndoableCommand {
    private DriverFactory driverFactory;
    private String driverID;
    private String driverName;
    private int role;
    private VehicleFleet fleet;
    private Driver driver;

    public AddDriverCommand(FleetManager manager, DriverFactory factory,
                            String driverID, String driverName, int role) {
        super(manager);
        this.driverFactory = factory;
        this.driverID = driverID;
        this.driverName = driverName;
        this.role = role;
    }

    public void execute() {
        fleet = manager.getCurrentFleet();
        if (fleet == null) throw new CommandExecutionException("No current fleet selected.");
        if (FleetUtil.findDriver(fleet, driverID) != null) {
            throw new CommandExecutionException("Driver " + driverID + " already exists in the current fleet!");
        }
        driver = driverFactory.createDriver(driverID, driverName);
        driver.setRole(role);
        redo();
        System.out.println("Driver is added.");
    }

    public void undo() {
        if (fleet == null || driver == null || FleetUtil.findDriver(fleet, driverID) != driver) {
            throw new CommandExecutionException("Unable to undo adding driver.");
        }
        fleet.dropDriver(driver);
    }

    public void redo() {
        if (fleet == null || driver == null || manager.findFleet(fleet.getFleetID()) != fleet) {
            throw new CommandExecutionException("Unable to redo adding driver.");
        }
        if (FleetUtil.findDriver(fleet, driverID) != null) {
            throw new CommandExecutionException("Driver " + driverID + " already exists in the target fleet!");
        }
        fleet.addDriver(driver);
        try {
            // The concrete fleet validates its own roles through the original API.
            fleet.updateDriverRole();
        } catch (IllegalArgumentException ex) {
            fleet.dropDriver(driver);
            throw new CommandExecutionException(ex.getMessage());
        }
    }

    public String displayName() {
        return "Add driver, " + driverID + ", " + driverName + ", role " + role;
    }
}

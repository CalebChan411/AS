/** Keeps an old-role Memento; redo reapplies the requested role to the same driver. */
public class ModifyRoleCommand extends UndoableCommand {
    private String driverID;
    private int newRole;
    private VehicleFleet fleet;
    private Driver driver;
    private DriverRoleMemento memento;
    private FleetFactory fleetFactory;

    public ModifyRoleCommand(FleetManager manager, String driverID, int newRole) {
        super(manager);
        this.driverID = driverID;
        this.newRole = newRole;
    }

    public void execute() {
        fleet = manager.getCurrentFleet();
        if (fleet == null) throw new CommandExecutionException("No current fleet selected.");
        driver = FleetUtil.findDriver(fleet, driverID);
        if (driver == null) {
            throw new CommandExecutionException("Driver " + driverID + " is not found in the current fleet!");
        }
        fleetFactory = manager.getFleetFactory(fleet);
        memento = new DriverRoleMemento(driver.getDriverID(), driver.getRole());
        redo();
        System.out.println("Driver role is updated.");
    }

    public void undo() {
        if (memento == null) throw new CommandExecutionException("Unable to undo driver role modification.");
        memento.restore(driver);
        fleet.updateDriverRole();
    }

    public void redo() {
        if (driver == null || FleetUtil.findDriver(fleet, driverID) != driver) {
            throw new CommandExecutionException("Unable to redo driver role modification.");
        }
        int previousRole = driver.getRole();
        driver.setRole(newRole);
        try {
            fleet.updateDriverRole();
        } catch (IllegalArgumentException ex) {
            driver.setRole(previousRole);
            throw new CommandExecutionException(ex.getMessage());
        }
    }

    public String displayName() {
        String roleName = fleetFactory == null ? String.valueOf(newRole) : fleetFactory.getDriverRoleName(newRole);
        return "Modify driver role, " + driverID + ", " + roleName;
    }
}

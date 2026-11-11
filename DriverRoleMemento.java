/** Saves the old role value; only restore exposes the snapshot's behaviour. */
public class DriverRoleMemento {
    private String driverID;
    private int oldRole;

    public DriverRoleMemento(String driverID, int oldRole) {
        this.driverID = driverID;
        this.oldRole = oldRole;
    }

    public void restore(Driver driver) {
        if (driver == null || !driver.getDriverID().equals(driverID)) {
            throw new CommandExecutionException("Driver does not match the saved role.");
        }
        driver.setRole(oldRole);
    }
}

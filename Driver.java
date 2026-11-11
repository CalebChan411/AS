/** Represents a driver assigned to a vehicle fleet. */
public class Driver {
    private String driverID;
    private String driverName;
    private int role;

    public Driver(String driverID) {
        if (driverID == null || driverID.trim().isEmpty()) {
            throw new IllegalArgumentException("Driver ID cannot be empty.");
        }
        this.driverID = driverID.trim();
        this.driverName = "";
        this.role = 0;
    }

    public String getDriverID() {
        return driverID;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public String getName() {
        return driverName;
    }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Driver name cannot be empty.");
        }
        this.driverName = name.trim();
    }
}

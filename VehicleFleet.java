import java.util.AbstractList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Vector;

/**
 * Common abstract representation of a delivery vehicle fleet.
 * Its attributes and operations follow the original assignment UML.
 */
public abstract class VehicleFleet {
    private String fleetID;
    private String fleetName;
    @SuppressWarnings("rawtypes")
    private AbstractList drivers;

    public VehicleFleet(String fleetID) {
        if (fleetID == null || fleetID.trim().isEmpty()) {
            throw new IllegalArgumentException("Fleet ID cannot be empty.");
        }
        this.fleetID = fleetID.trim();
        this.fleetName = "";
        this.drivers = new Vector<Driver>();
    }

    public String getFleetID() {
        return fleetID;
    }

    public String getName() {
        return fleetName;
    }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Fleet name cannot be empty.");
        }
        this.fleetName = name.trim();
    }

    @SuppressWarnings("unchecked")
    public void addDriver(Driver driver) {
        if (driver == null) {
            throw new IllegalArgumentException("Driver cannot be null.");
        }
        drivers.add(driver);
    }

    public void dropDriver(Driver driver) {
        if (driver == null) {
            throw new IllegalArgumentException("Driver cannot be null.");
        }
        for (int i = 0; i < drivers.size(); i++) {
            if (drivers.get(i) == driver) {
                drivers.remove(i);
                return;
            }
        }
    }

    @SuppressWarnings("unchecked")
    public Iterator<Driver> getDrivers() {
        return (Iterator<Driver>) Collections.unmodifiableList(drivers).iterator();
    }

    public abstract void updateDriverRole();

    public abstract void showFleet();
}

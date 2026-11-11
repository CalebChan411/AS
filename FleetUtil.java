import java.util.Iterator;
import java.util.List;
import java.util.Vector;

/** Driver lookup and order restoration using only the original public API. */
public class FleetUtil {
    public static Driver findDriver(VehicleFleet fleet, String driverID) {
        if (fleet == null || driverID == null) return null;
        Iterator<Driver> drivers = fleet.getDrivers();
        while (drivers.hasNext()) {
            Driver driver = drivers.next();
            if (driver.getDriverID().equalsIgnoreCase(driverID.trim())) return driver;
        }
        return null;
    }

    public static int indexOfDriver(VehicleFleet fleet, String driverID) {
        if (fleet == null || driverID == null) return -1;
        Iterator<Driver> drivers = fleet.getDrivers();
        int index = 0;
        while (drivers.hasNext()) {
            if (drivers.next().getDriverID().equalsIgnoreCase(driverID.trim())) return index;
            index++;
        }
        return -1;
    }

    public static List<Driver> getAllDrivers(VehicleFleet fleet) {
        List<Driver> result = new Vector<Driver>();
        Iterator<Driver> drivers = fleet.getDrivers();
        while (drivers.hasNext()) result.add(drivers.next());
        return result;
    }

    public static void restoreDriverOrder(VehicleFleet fleet, List<Driver> order) {
        // Snapshot first: dropping a driver invalidates an active iterator.
        List<Driver> current = getAllDrivers(fleet);
        List<Driver> saved = new Vector<Driver>(order);
        for (Driver driver : current) fleet.dropDriver(driver);
        for (Driver driver : saved) fleet.addDriver(driver);
    }
}

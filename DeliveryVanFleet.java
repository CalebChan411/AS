import java.util.Iterator;

/** Fleet implementation for delivery vans. */
public class DeliveryVanFleet extends VehicleFleet {
    private final int VAN_DRIVER_ROLE = 1;
    private final int COURIER_DRIVER_ROLE = 2;

    public DeliveryVanFleet(String fleetID) {
        super(fleetID);
    }

    @Override
    public void updateDriverRole() {
        for (Iterator<Driver> it = getDrivers(); it.hasNext();) {
            int role = it.next().getRole();
            if (role != VAN_DRIVER_ROLE && role != COURIER_DRIVER_ROLE) {
                throw new IllegalArgumentException("Invalid driver role for delivery van fleet.");
            }
        }
    }

    @Override
    public void showFleet() {
        updateDriverRole();

        System.out.println("Delivery Van Fleet " + getName() + " (" + getFleetID() + ")");
        System.out.println();

        boolean foundVanDriver = false;
        for (Iterator<Driver> it = getDrivers(); it.hasNext();) {
            Driver driver = it.next();
            if (driver.getRole() == VAN_DRIVER_ROLE) {
                if (!foundVanDriver) {
                    System.out.println("Van Driver:");
                    System.out.println();
                    foundVanDriver = true;
                }
                System.out.println(driver.getDriverID() + ", " + driver.getName());
                System.out.println();
            }
        }
        if (!foundVanDriver) {
            System.out.println("Van Driver: NIL");
            System.out.println();
        }

        boolean foundCourierDriver = false;
        for (Iterator<Driver> it = getDrivers(); it.hasNext();) {
            Driver driver = it.next();
            if (driver.getRole() == COURIER_DRIVER_ROLE) {
                if (!foundCourierDriver) {
                    System.out.println("Courier Driver:");
                    System.out.println();
                    foundCourierDriver = true;
                }
                System.out.println(driver.getDriverID() + ", " + driver.getName());
                System.out.println();
            }
        }
        if (!foundCourierDriver) {
            System.out.println("Courier Driver: NIL");
            System.out.println();
        }
    }
}

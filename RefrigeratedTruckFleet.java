import java.util.Iterator;

/** Fleet implementation for refrigerated trucks. */
public class RefrigeratedTruckFleet extends VehicleFleet {
    private final int REFRIGERATED_TRUCK_DRIVER_ROLE = 1;
    private final int COLD_CHAIN_DELIVERY_DRIVER_ROLE = 2;

    public RefrigeratedTruckFleet(String fleetID) {
        super(fleetID);
    }

    @Override
    public void updateDriverRole() {
        for (Iterator<Driver> it = getDrivers(); it.hasNext();) {
            int role = it.next().getRole();
            if (role != REFRIGERATED_TRUCK_DRIVER_ROLE && role != COLD_CHAIN_DELIVERY_DRIVER_ROLE) {
                throw new IllegalArgumentException("Invalid driver role for refrigerated truck fleet.");
            }
        }
    }

    @Override
    public void showFleet() {
        updateDriverRole();

        System.out.println("Refrigerated Truck Fleet " + getName() + " (" + getFleetID() + ")");
        System.out.println();

        boolean foundRefrigeratedTruckDriver = false;
        for (Iterator<Driver> it = getDrivers(); it.hasNext();) {
            Driver driver = it.next();
            if (driver.getRole() == REFRIGERATED_TRUCK_DRIVER_ROLE) {
                if (!foundRefrigeratedTruckDriver) {
                    System.out.println("Refrigerated-Truck Driver:");
                    System.out.println();
                    foundRefrigeratedTruckDriver = true;
                }
                System.out.println(driver.getDriverID() + ", " + driver.getName());
                System.out.println();
            }
        }
        if (!foundRefrigeratedTruckDriver) {
            System.out.println("Refrigerated-Truck Driver: NIL");
            System.out.println();
        }

        boolean foundColdChainDriver = false;
        for (Iterator<Driver> it = getDrivers(); it.hasNext();) {
            Driver driver = it.next();
            if (driver.getRole() == COLD_CHAIN_DELIVERY_DRIVER_ROLE) {
                if (!foundColdChainDriver) {
                    System.out.println("Cold-Chain Delivery Driver:");
                    System.out.println();
                    foundColdChainDriver = true;
                }
                System.out.println(driver.getDriverID() + ", " + driver.getName());
                System.out.println();
            }
        }
        if (!foundColdChainDriver) {
            System.out.println("Cold-Chain Delivery Driver: NIL");
            System.out.println();
        }
    }
}

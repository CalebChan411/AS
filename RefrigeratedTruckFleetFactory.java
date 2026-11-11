/** Concrete factory for refrigerated truck fleets. */
public class RefrigeratedTruckFleetFactory implements FleetFactory {
    public VehicleFleet createFleet(String fleetID) {
        return new RefrigeratedTruckFleet(fleetID);
    }

    public String getType() {
        return "r";
    }
}

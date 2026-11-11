/** Concrete factory for refrigerated truck fleets. */
public class RefrigeratedTruckFleetFactory implements FleetFactory {
    public VehicleFleet createFleet(String fleetID) {
        return new RefrigeratedTruckFleet(fleetID);
    }

    public String getType() {
        return "r";
    }

    public String getFleetTypeName() {
        return "Refrigerated Truck Fleet";
    }

    public String getDriverRoleName(int role) {
        if (role == 1) return "refrigerated-truck driver";
        if (role == 2) return "cold-chain delivery driver";
        throw new IllegalArgumentException("Invalid driver role. Please enter 1 or 2.");
    }
}

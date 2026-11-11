/** Concrete factory for delivery van fleets. */
public class DeliveryVanFleetFactory implements FleetFactory {
    public VehicleFleet createFleet(String fleetID) {
        return new DeliveryVanFleet(fleetID);
    }

    public String getType() {
        return "v";
    }

    public String getFleetTypeName() {
        return "Delivery Van Fleet";
    }

    public String getDriverRoleName(int role) {
        if (role == 1) return "van driver";
        if (role == 2) return "courier driver";
        throw new IllegalArgumentException("Invalid driver role. Please enter 1 or 2.");
    }
}

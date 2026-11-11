/** Concrete factory for delivery van fleets. */
public class DeliveryVanFleetFactory implements FleetFactory {
    public VehicleFleet createFleet(String fleetID) {
        return new DeliveryVanFleet(fleetID);
    }

    public String getType() {
        return "v";
    }
}

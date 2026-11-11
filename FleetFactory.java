/** A concrete factory supplies one kind of fleet without changing commands. */
public interface FleetFactory {
    VehicleFleet createFleet(String fleetID);
    String getType();
    String getFleetTypeName();
    String getDriverRoleName(int role);
}

/** Saves the old name value and restores it through the original public setter. */
public class FleetNameMemento {
    private String fleetID;
    private String oldName;

    public FleetNameMemento(String fleetID, String oldName) {
        this.fleetID = fleetID;
        this.oldName = oldName;
    }

    public void restore(VehicleFleet fleet) {
        if (fleet == null || !fleet.getFleetID().equals(fleetID)) {
            throw new CommandExecutionException("Fleet does not match the saved name.");
        }
        fleet.setName(oldName);
    }
}

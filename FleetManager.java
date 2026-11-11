import java.util.List;
import java.util.Vector;

/** Owns fleet membership and the current selection; commands own history. */
public class FleetManager {
    private List<VehicleFleet> fleets;
    private VehicleFleet currentFleet;

    public FleetManager() {
        fleets = new Vector<VehicleFleet>();
    }

    public void addFleet(VehicleFleet fleet) {
        if (fleet == null) throw new CommandExecutionException("Fleet cannot be null.");
        if (hasFleet(fleet.getFleetID())) {
            throw new CommandExecutionException("Fleet ID " + fleet.getFleetID() + " already exists!");
        }
        fleets.add(fleet);
    }

    public void removeFleet(VehicleFleet fleet) {
        if (fleet == null || !fleets.remove(fleet)) {
            throw new CommandExecutionException("Fleet is not managed by the system.");
        }
        if (currentFleet == fleet) currentFleet = null;
    }

    public List<VehicleFleet> getFleets() {
        // A copy prevents callers from changing membership behind the manager.
        return new Vector<VehicleFleet>(fleets);
    }

    public VehicleFleet findFleet(String fleetID) {
        if (fleetID == null) return null;
        for (VehicleFleet fleet : fleets) {
            if (fleet.getFleetID().equalsIgnoreCase(fleetID.trim())) return fleet;
        }
        return null;
    }

    public void setCurrentFleet(VehicleFleet fleet) {
        if (fleet != null && !fleets.contains(fleet)) {
            throw new CommandExecutionException("Fleet is not managed by the system.");
        }
        currentFleet = fleet;
    }

    public VehicleFleet getCurrentFleet() {
        return currentFleet;
    }

    public int getFleetCount() {
        return fleets.size();
    }

    public boolean hasFleet(String fleetID) {
        return findFleet(fleetID) != null;
    }
}

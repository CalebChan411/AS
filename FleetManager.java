import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Vector;

/** Owns fleet membership and the current selection; commands own history. */
public class FleetManager {
    private List<VehicleFleet> fleets;
    private VehicleFleet currentFleet;
    private Map<VehicleFleet, FleetFactory> fleetFactories;

    public FleetManager() {
        fleets = new Vector<VehicleFleet>();
        fleetFactories = new HashMap<VehicleFleet, FleetFactory>();
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
        fleetFactories.remove(fleet);
    }

    /** Keeps type information outside the four original domain classes. */
    public void addFleet(VehicleFleet fleet, FleetFactory factory) {
        if (factory == null) throw new IllegalArgumentException("Fleet factory cannot be null.");
        addFleet(fleet);
        fleetFactories.put(fleet, factory);
    }

    public FleetFactory getFleetFactory(VehicleFleet fleet) {
        FleetFactory factory = fleetFactories.get(fleet);
        if (factory == null) throw new CommandExecutionException("No factory registered for fleet.");
        return factory;
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

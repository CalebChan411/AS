/** Creates and selects a fleet; retains that same object for undo/redo. */
public class CreateFleetCommand extends UndoableCommand {
    private FleetFactoryRegistry fleetRegistry;
    private String type;
    private String fleetID;
    private String fleetName;
    private VehicleFleet fleet;
    private VehicleFleet previousCurrentFleet;

    public CreateFleetCommand(FleetManager manager, FleetFactoryRegistry registry,
                              String type, String fleetID, String fleetName) {
        super(manager);
        this.fleetRegistry = registry;
        this.type = type;
        this.fleetID = fleetID;
        this.fleetName = fleetName;
    }

    public void execute() {
        FleetFactory factory = fleetRegistry.getFactory(type);
        if (factory == null) throw new CommandExecutionException("Invalid fleet type!");
        if (manager.hasFleet(fleetID)) {
            throw new CommandExecutionException("Fleet ID " + fleetID + " already exists!");
        }
        fleet = factory.createFleet(fleetID);
        fleet.setName(fleetName);
        previousCurrentFleet = manager.getCurrentFleet();
        redo();
        System.out.println("Fleet " + fleet.getFleetID() + " is created.");
        System.out.println("Current fleet is changed to " + fleet.getFleetID() + ".");
    }

    public void undo() {
        if (fleet == null || manager.findFleet(fleet.getFleetID()) != fleet) {
            throw new CommandExecutionException("Unable to undo fleet creation.");
        }
        boolean wasCurrent = manager.getCurrentFleet() == fleet;
        manager.removeFleet(fleet);
        // A later non-undoable selection of another fleet remains valid.
        if (wasCurrent) {
            if (previousCurrentFleet != null
                    && manager.findFleet(previousCurrentFleet.getFleetID()) == previousCurrentFleet) {
                manager.setCurrentFleet(previousCurrentFleet);
            } else {
                manager.setCurrentFleet(null);
            }
        }
    }

    public void redo() {
        if (fleet == null) throw new CommandExecutionException("Fleet has not been created.");
        manager.addFleet(fleet);
        manager.setCurrentFleet(fleet);
    }

    public String displayName() {
        return "Create fleet, " + type + ", " + fleetID + ", " + fleetName;
    }
}

/** Creates and selects a fleet; retains that same object for undo/redo. */
public class CreateFleetCommand extends UndoableCommand {
    private FleetFactoryRegistry fleetRegistry;
    private FleetFactory factory;
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
        factory = fleetRegistry.getFactory(type);
        if (factory == null) throw new CommandExecutionException("Invalid fleet type!");
        if (manager.hasFleet(fleetID)) {
            throw new CommandExecutionException("Fleet ID " + fleetID + " already exists!");
        }
        fleet = factory.createFleet(fleetID);
        fleet.setName(fleetName);
        previousCurrentFleet = manager.getCurrentFleet();
        redo();
        String typeName = factory.getFleetTypeName().toLowerCase();
        System.out.println(Character.toUpperCase(typeName.charAt(0))
                + typeName.substring(1) + " is created.");
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
        manager.addFleet(fleet, factory);
        manager.setCurrentFleet(fleet);
    }

    public String displayName() {
        FleetFactory fleetFactory = factory == null ? fleetRegistry.getFactory(type) : factory;
        String typeName = fleetFactory == null ? type : fleetFactory.getFleetTypeName().toLowerCase();
        return "Create " + typeName + ", " + fleetID + ", " + fleetName;
    }
}

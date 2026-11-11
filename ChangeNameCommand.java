/** Stores the previous fleet name in a Memento and retains the target fleet. */
public class ChangeNameCommand extends UndoableCommand {
    private String newName;
    private VehicleFleet fleet;
    private FleetNameMemento memento;

    public ChangeNameCommand(FleetManager manager, String newName) {
        super(manager);
        this.newName = newName;
    }

    public void execute() {
        fleet = manager.getCurrentFleet();
        if (fleet == null) throw new CommandExecutionException("No current fleet selected.");
        memento = new FleetNameMemento(fleet.getFleetID(), fleet.getName());
        redo();
        System.out.println("Fleet name is updated.");
    }

    public void undo() {
        if (memento == null) throw new CommandExecutionException("Unable to undo fleet name change.");
        memento.restore(fleet);
    }

    public void redo() {
        if (fleet == null) throw new CommandExecutionException("Unable to redo fleet name change.");
        fleet.setName(newName);
    }

    public String displayName() {
        return "Change fleet name, " + (fleet == null ? "" : fleet.getFleetID() + ", ") + newName;
    }
}

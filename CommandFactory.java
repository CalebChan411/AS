/** Creates all eleven commands and supplies their diagram-defined collaborators. */
public class CommandFactory {
    private FleetManager manager;
    private CommandInvoker invoker;
    private FleetFactoryRegistry fleetRegistry;
    private DriverFactory driverFactory;

    public CommandFactory(FleetManager manager, CommandInvoker invoker,
                          FleetFactoryRegistry fleetRegistry, DriverFactory driverFactory) {
        this.manager = manager;
        this.invoker = invoker;
        this.fleetRegistry = fleetRegistry;
        this.driverFactory = driverFactory;
    }

    public Command createFleetCommand(String type, String fleetID, String fleetName) {
        return new CreateFleetCommand(manager, fleetRegistry, type, fleetID, fleetName);
    }

    public Command createSetCurrentFleetCommand(String fleetID) {
        return new SetCurrentFleetCommand(manager, fleetID);
    }

    public Command createAddDriverCommand(String driverID, String driverName, int role) {
        return new AddDriverCommand(manager, driverFactory, driverID, driverName, role);
    }

    public Command createModifyRoleCommand(String driverID, int role) {
        return new ModifyRoleCommand(manager, driverID, role);
    }

    public Command createDeleteDriverCommand(String driverID) {
        return new DeleteDriverCommand(manager, driverID);
    }

    public Command createChangeNameCommand(String newName) {
        return new ChangeNameCommand(manager, newName);
    }

    public Command createShowFleetCommand() {
        return new ShowFleetCommand(manager);
    }

    public Command createShowAllFleetsCommand() {
        return new ShowAllFleetsCommand(manager);
    }

    public Command createUndoCommand() {
        return new UndoCommand(invoker);
    }

    public Command createRedoCommand() {
        return new RedoCommand(invoker);
    }

    public Command createListCommand() {
        return new ListCommand(invoker);
    }
}

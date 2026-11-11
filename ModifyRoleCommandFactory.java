/** Creates a role-change command; the command captures its Memento on execution. */
public class ModifyRoleCommandFactory implements CommandFactory {
    private FleetManager manager;
    private ConsoleInput input;

    public ModifyRoleCommandFactory(FleetManager manager, ConsoleInput input) {
        this.manager = manager;
        this.input = input;
    }

    public Command createCommand() {
        VehicleFleet current = manager.getCurrentFleet();
        if (current == null) throw new CommandExecutionException("No current fleet selected.");
        String driverID = input.readLine("Please input driver ID:- ");
        if (driverID == null) return null;
        FleetFactory factory = manager.getFleetFactory(current);
        Integer role;
        try {
            role = input.readInt("Role (1 = " + factory.getDriverRoleName(1)
                    + " | 2 = " + factory.getDriverRoleName(2) + ") :- ");
        } catch (NumberFormatException ex) {
            throw new CommandExecutionException("Invalid driver role. Please enter 1 or 2.");
        }
        if (role == null) return null;
        return new ModifyRoleCommand(manager, driverID.trim(), role);
    }
}

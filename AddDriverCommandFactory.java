/** Reads driver information; DriverFactory is used later by the new command. */
public class AddDriverCommandFactory implements CommandFactory {
    private FleetManager manager;
    private DriverFactory driverFactory;
    private ConsoleInput input;

    public AddDriverCommandFactory(FleetManager manager, DriverFactory driverFactory,
                                   ConsoleInput input) {
        this.manager = manager;
        this.driverFactory = driverFactory;
        this.input = input;
    }

    public Command createCommand() {
        VehicleFleet current = manager.getCurrentFleet();
        if (current == null) throw new CommandExecutionException("No current fleet selected.");
        String information = input.readLine("Please input driver information (id, name):- ");
        if (information == null) return null;
        String[] details = parseDriverDetails(information);
        FleetFactory factory = manager.getFleetFactory(current);
        Integer role;
        try {
            role = input.readInt("Role (1 = " + factory.getDriverRoleName(1)
                    + " | 2 = " + factory.getDriverRoleName(2) + ") :- ");
        } catch (NumberFormatException ex) {
            throw new CommandExecutionException("Invalid driver role. Please enter 1 or 2.");
        }
        if (role == null) return null;
        return new AddDriverCommand(manager, driverFactory, details[0], details[1], role);
    }

    private String[] parseDriverDetails(String information) {
        int comma = information.indexOf(',');
        if (comma < 1 || comma == information.length() - 1) {
            throw new IllegalArgumentException("Please enter driver information in the format: ID, Name.");
        }
        String driverID = information.substring(0, comma).trim();
        String driverName = information.substring(comma + 1).trim();
        if (driverID.isEmpty() || driverName.isEmpty()) {
            throw new IllegalArgumentException("Driver ID and name cannot be empty.");
        }
        return new String[] { driverID, driverName };
    }
}

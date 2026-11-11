/** Supplies a fresh fleet-summary command for each request. */
public class ShowAllFleetsCommandFactory implements CommandFactory {
    private FleetManager manager;

    public ShowAllFleetsCommandFactory(FleetManager manager) {
        this.manager = manager;
    }

    public Command createCommand() {
        return new ShowAllFleetsCommand(manager);
    }
}

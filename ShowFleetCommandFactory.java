/** Supplies a fresh display command for each request. */
public class ShowFleetCommandFactory implements CommandFactory {
    private FleetManager manager;

    public ShowFleetCommandFactory(FleetManager manager) {
        this.manager = manager;
    }

    public Command createCommand() {
        return new ShowFleetCommand(manager);
    }
}

import java.util.Scanner;

/** Wires the factories and dispatches every request through the same loop. */
public class Main {
    private static boolean interactive;

    private static final String MENU_PROMPT =
            "Please enter command [ c | s | a | m | d | sf | sa | cn | u | r | l | x ] :- ";

    public static void main(String[] args) {
        interactive = System.console() != null;
        Scanner scanner = new Scanner(System.in);
        ConsoleInput input = new ConsoleInput(scanner, interactive);
        FleetManager manager = new FleetManager();
        FleetFactoryRegistry fleetRegistry = new FleetFactoryRegistry();
        fleetRegistry.registerFactory(new DeliveryVanFleetFactory());
        fleetRegistry.registerFactory(new RefrigeratedTruckFleetFactory());
        DriverFactory driverFactory = new DriverFactory();
        CommandInvoker invoker = new CommandInvoker();

        CommandFactoryRegistry commandFactories = new CommandFactoryRegistry();
        commandFactories.registerFactory("c",
                new CreateFleetCommandFactory(manager, fleetRegistry, input));
        commandFactories.registerFactory("s", new SetCurrentFleetCommandFactory(manager, input));
        commandFactories.registerFactory("a", new AddDriverCommandFactory(manager, driverFactory, input));
        commandFactories.registerFactory("m", new ModifyRoleCommandFactory(manager, input));
        commandFactories.registerFactory("d", new DeleteDriverCommandFactory(manager, input));
        commandFactories.registerFactory("sf", new ShowFleetCommandFactory(manager));
        commandFactories.registerFactory("sa", new ShowAllFleetsCommandFactory(manager));
        commandFactories.registerFactory("cn", new ChangeNameCommandFactory(manager, input));
        commandFactories.registerFactory("u", new UndoCommandFactory(invoker));
        commandFactories.registerFactory("r", new RedoCommandFactory(invoker));
        commandFactories.registerFactory("l", new ListCommandFactory(invoker));

        try {
            while (true) {
                printHeader(manager);
                String code = input.readLine(MENU_PROMPT);
                if (code == null) break;
                code = code.trim().toLowerCase();
                if (code.equals("x")) {
                    System.out.println("Thank you for using SDFMS. System terminated.");
                    break;
                }

                CommandFactory factory = commandFactories.getFactory(code);
                if (factory == null) {
                    System.out.println("Invalid command!");
                    continue;
                }
                try {
                    Command command = factory.createCommand();
                    if (command == null) break;
                    invoker.execute(command);
                } catch (CommandExecutionException ex) {
                    System.out.println(ex.getMessage());
                } catch (IllegalArgumentException ex) {
                    System.out.println(ex.getMessage());
                }
            }
        } finally {
            scanner.close();
        }
    }

    private static void printHeader(FleetManager manager) {
        if (!interactive) return;
        System.out.println();
        System.out.println("Smart Delivery Fleet Management System (SDFMS)");
        System.out.println();
        showMenu();
        VehicleFleet current = manager.getCurrentFleet();
        if (current != null) {
            System.out.println("The current fleet is " + current.getFleetID() + " " + current.getName() + ".");
            System.out.println();
        }
    }

    private static void showMenu() {
        System.out.println("c = create fleet, s = set current fleet, a = add driver, m = modify driver role,");
        System.out.println("d = delete driver, sf = show fleet, sa = display all fleets, cn = change fleet name,");
        System.out.println("u = undo, r = redo, l = list undo/redo, x = exit system");
        System.out.println();
    }
}

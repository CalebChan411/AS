import java.util.Scanner;

/** Entry point and console user interface for SDFMS. */
public class Main {
    private static boolean interactive;
    private static Scanner scanner;

    private static final String MENU_PROMPT =
            "Please enter command [ c | s | a | m | d | sf | sa | cn | u | r | l | x ] :- ";

    public static void main(String[] args) {
        interactive = System.console() != null;
        FleetManager manager = new FleetManager();
        FleetFactoryRegistry registry = new FleetFactoryRegistry();
        registry.registerFactory(new DeliveryVanFleetFactory());
        registry.registerFactory(new RefrigeratedTruckFleetFactory());

        CommandInvoker invoker = new CommandInvoker();
        DriverFactory driverFactory = new DriverFactory();
        CommandFactory commandFactory = new CommandFactory(manager, invoker, registry, driverFactory);

        scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            printHeader(manager);
            printPrompt(MENU_PROMPT);
            String command = readInput();
            if (command == null) break;
            command = command.toLowerCase();

            try {
                switch (command) {
                    case "c":
                        running = handleCreateFleet(scanner, commandFactory, invoker);
                        break;
                    case "s":
                        running = handleSetCurrentFleet(scanner, commandFactory, invoker);
                        break;
                    case "a":
                        running = handleAddDriver(scanner, manager, commandFactory, invoker);
                        break;
                    case "m":
                        running = handleModifyDriverRole(scanner, manager, commandFactory, invoker);
                        break;
                    case "d":
                        running = handleDeleteDriver(scanner, commandFactory, invoker);
                        break;
                    case "sf":
                        invoker.execute(commandFactory.createShowFleetCommand());
                        break;
                    case "sa":
                        invoker.execute(commandFactory.createShowAllFleetsCommand());
                        break;
                    case "cn":
                        running = handleChangeFleetName(scanner, commandFactory, invoker);
                        break;
                    case "u":
                        invoker.execute(commandFactory.createUndoCommand());
                        break;
                    case "r":
                        invoker.execute(commandFactory.createRedoCommand());
                        break;
                    case "l":
                        invoker.execute(commandFactory.createListCommand());
                        break;
                    case "x":
                        System.out.println("Thank you for using SDFMS. System terminated.");
                        running = false;
                        break;
                    default:
                        System.out.println("Invalid command!");
                        break;
                }
            } catch (CommandExecutionException ex) {
                System.out.println(ex.getMessage());
            } catch (IllegalArgumentException ex) {
                System.out.println(ex.getMessage());
            }
        }
        scanner.close();
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

    private static String readInput() {
        return scanner.hasNextLine() ? scanner.nextLine().trim() : null;
    }

    private static boolean handleCreateFleet(Scanner scanner, CommandFactory factory, CommandInvoker invoker) {
        printPrompt("Enter fleet type (v = delivery van | r = refrigerated truck) :- ");
        if (!scanner.hasNextLine()) return false;
        String type = scanner.nextLine().trim().toLowerCase();
        printPrompt("Fleet ID:- ");
        if (!scanner.hasNextLine()) return false;
        String fleetID = scanner.nextLine().trim();
        printPrompt("Fleet Name:- ");
        if (!scanner.hasNextLine()) return false;
        String fleetName = scanner.nextLine().trim();
        invoker.execute(factory.createFleetCommand(type, fleetID, fleetName));
        return true;
    }

    private static boolean handleSetCurrentFleet(Scanner scanner, CommandFactory factory, CommandInvoker invoker) {
        printPrompt("Please input fleet ID:- ");
        if (!scanner.hasNextLine()) return false;
        String fleetID = scanner.nextLine().trim();
        invoker.execute(factory.createSetCurrentFleetCommand(fleetID));
        return true;
    }

    private static boolean handleAddDriver(Scanner scanner, FleetManager manager,
                                           CommandFactory factory, CommandInvoker invoker) {
        VehicleFleet current = manager.getCurrentFleet();
        if (current == null) {
            System.out.println("No current fleet selected.");
            return true;
        }
        printPrompt("Please input driver information (id, name):- ");
        if (!scanner.hasNextLine()) return false;
        String[] details = parseDriverDetails(scanner.nextLine());
        Integer role = readRole(scanner);
        if (role == null) return true;
        invoker.execute(factory.createAddDriverCommand(details[0], details[1], role));
        return true;
    }

    private static boolean handleModifyDriverRole(Scanner scanner, FleetManager manager,
                                                   CommandFactory factory, CommandInvoker invoker) {
        VehicleFleet current = manager.getCurrentFleet();
        if (current == null) {
            System.out.println("No current fleet selected.");
            return true;
        }
        printPrompt("Please input driver ID:- ");
        if (!scanner.hasNextLine()) return false;
        String driverID = scanner.nextLine().trim();
        Integer role = readRole(scanner);
        if (role == null) return true;
        invoker.execute(factory.createModifyRoleCommand(driverID, role));
        return true;
    }

    private static boolean handleDeleteDriver(Scanner scanner, CommandFactory factory, CommandInvoker invoker) {
        printPrompt("Please input driver ID:- ");
        if (!scanner.hasNextLine()) return false;
        String driverID = scanner.nextLine().trim();
        invoker.execute(factory.createDeleteDriverCommand(driverID));
        return true;
    }

    private static boolean handleChangeFleetName(Scanner scanner, CommandFactory factory, CommandInvoker invoker) {
        printPrompt("Please input new name of the current fleet:- ");
        if (!scanner.hasNextLine()) return false;
        String newName = scanner.nextLine().trim();
        invoker.execute(factory.createChangeNameCommand(newName));
        return true;
    }

    private static Integer readRole(Scanner scanner) {
        printPrompt("Role (1 or 2) :- ");
        if (!scanner.hasNextLine()) return null;
        String input = scanner.nextLine().trim();
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException ex) {
            System.out.println("Invalid driver role. Please enter 1 or 2.");
            return null;
        }
    }

    private static void printPrompt(String prompt) {
        if (interactive) System.out.print(prompt);
    }

    private static String[] parseDriverDetails(String input) {
        int comma = input.indexOf(',');
        if (comma < 1 || comma == input.length() - 1) {
            throw new IllegalArgumentException("Please enter driver information in the format: ID, Name.");
        }
        String id = input.substring(0, comma).trim();
        String name = input.substring(comma + 1).trim();
        if (id.isEmpty() || name.isEmpty()) {
            throw new IllegalArgumentException("Driver ID and name cannot be empty.");
        }
        return new String[] { id, name };
    }
}

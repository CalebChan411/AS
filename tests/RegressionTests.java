import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/** Standalone regression checks: javac -d build *.java tests/RegressionTests.java. */
public class RegressionTests {
    private static int checks;
    private static int groups;

    private static class Context {
        FleetManager manager = new FleetManager();
        CommandInvoker invoker = new CommandInvoker();
        FleetFactoryRegistry registry = new FleetFactoryRegistry();
        DriverFactory drivers = new DriverFactory();
        CommandFactory commands;

        Context() {
            registry.registerFactory(new DeliveryVanFleetFactory());
            registry.registerFactory(new RefrigeratedTruckFleetFactory());
            commands = new CommandFactory(manager, invoker, registry, drivers);
        }

        VehicleFleet baseline(String type, String id, String name) {
            VehicleFleet fleet = registry.getFactory(type).createFleet(id);
            fleet.setName(name);
            manager.addFleet(fleet);
            manager.setCurrentFleet(fleet);
            return fleet;
        }

        Driver baselineDriver(VehicleFleet fleet, String id, int role) {
            Driver driver = drivers.createDriver(id, "Name " + id);
            driver.setRole(role);
            fleet.addDriver(driver);
            return driver;
        }

        void execute(Command command) {
            invoker.execute(command);
        }

        void undo() {
            execute(commands.createUndoCommand());
        }

        void redo() {
            execute(commands.createRedoCommand());
        }
    }

    public static void main(String[] args) {
        PrintStream console = System.out;
        ByteArrayOutputStream log = new ByteArrayOutputStream();
        System.setOut(new PrintStream(log));
        try {
            factoriesAndHierarchy();
            createUndoRedoAndSelection();
            addUndoRedoTargetsOriginalFleet();
            roleMementoRepeatedCycles();
            nameMementoRepeatedCycles();
            deleteRestoresIdentityAndOrder();
            readsAndHistoryCommandsStayOutOfHistory();
            noCurrentAndEmptyHistory();
            invalidMutationsKeepStateAndHistory();
            newMutationClearsRedo();
            System.setOut(console);
            console.println("PASS: " + checks + " checks in " + groups + " regression groups.");
        } catch (RuntimeException error) {
            System.setOut(console);
            console.println(log.toString());
            throw error;
        } catch (AssertionError error) {
            System.setOut(console);
            console.println(log.toString());
            throw error;
        } finally {
            System.setOut(console);
        }
    }

    private static void factoriesAndHierarchy() {
        Context c = new Context();
        check(c.registry.getFactory("v").createFleet("V1") instanceof DeliveryVanFleet,
                "van factory creates van fleet");
        check(c.registry.getFactory("r").createFleet("R1") instanceof RefrigeratedTruckFleet,
                "truck factory creates truck fleet");
        check("v".equals(c.registry.getFactory("v").getType()), "van type code");
        check("r".equals(c.registry.getFactory("r").getType()), "truck type code");
        Driver driver = c.drivers.createDriver("D1", "Alex Chan");
        check("D1".equals(driver.getDriverID()) && "Alex Chan".equals(driver.getName()),
                "driver factory initializes identity and name");

        Command[] commands = {
            c.commands.createFleetCommand("v", "F1", "Fleet One"),
            c.commands.createAddDriverCommand("D1", "Alex", 1),
            c.commands.createModifyRoleCommand("D1", 2),
            c.commands.createDeleteDriverCommand("D1"),
            c.commands.createChangeNameCommand("New Name"),
            c.commands.createSetCurrentFleetCommand("F1"),
            c.commands.createShowFleetCommand(),
            c.commands.createShowAllFleetsCommand(),
            c.commands.createUndoCommand(),
            c.commands.createRedoCommand(),
            c.commands.createListCommand()
        };
        check(commands[0] instanceof CreateFleetCommand, "factory creates create command");
        check(commands[1] instanceof AddDriverCommand, "factory creates add command");
        check(commands[2] instanceof ModifyRoleCommand, "factory creates modify command");
        check(commands[3] instanceof DeleteDriverCommand, "factory creates delete command");
        check(commands[4] instanceof ChangeNameCommand, "factory creates rename command");
        check(commands[5] instanceof SetCurrentFleetCommand, "factory creates select command");
        check(commands[6] instanceof ShowFleetCommand, "factory creates show command");
        check(commands[7] instanceof ShowAllFleetsCommand, "factory creates show all command");
        check(commands[8] instanceof UndoCommand, "factory creates undo command");
        check(commands[9] instanceof RedoCommand, "factory creates redo command");
        check(commands[10] instanceof ListCommand, "factory creates list command");
        for (int i = 0; i < commands.length; i++) {
            Command command = commands[i];
            check(command instanceof BaseCommand, "all commands use BaseCommand");
            check(command.isUndoable() == (i < 5), "exactly five mutating commands undoable");
            check((command instanceof UndoableCommand) == (i < 5), "undoable hierarchy");
            check(command.displayName() != null && command.displayName().trim().length() > 0,
                    "every command describes itself");
            if (i >= 5) {
                command.undo();
                command.redo();
                check(c.manager.getFleetCount() == 0, "base undo and redo are no-op");
            }
        }
        groups++;
    }

    private static void createUndoRedoAndSelection() {
        Context c = new Context();
        c.execute(c.commands.createFleetCommand("v", "F1", "First"));
        VehicleFleet first = c.manager.findFleet("F1");
        check(first != null && first == c.manager.getCurrentFleet(), "create selects new fleet");
        for (int i = 0; i < 3; i++) {
            c.undo();
            check(c.manager.getFleetCount() == 0 && c.manager.getCurrentFleet() == null,
                    "undo first creation leaves no dangling selection");
            c.redo();
            check(c.manager.getFleetCount() == 1 && c.manager.getCurrentFleet() == first,
                    "redo creation reuses original fleet object");
        }

        VehicleFleet second = c.baseline("r", "F2", "Second");
        c.manager.setCurrentFleet(first);
        c.execute(c.commands.createFleetCommand("v", "F3", "Third"));
        VehicleFleet third = c.manager.getCurrentFleet();
        c.execute(c.commands.createSetCurrentFleetCommand("F2"));
        c.undo();
        check(c.manager.findFleet("F3") == null && c.manager.getCurrentFleet() == second,
                "undo creation preserves another valid selected fleet");
        c.redo();
        check(c.manager.getCurrentFleet() == third && c.manager.findFleet("F3") == third,
                "redo creation reselects recreated fleet");
        c.undo();
        check(c.manager.getCurrentFleet() == first, "undo selected creation restores captured previous fleet");
        groups++;
    }

    private static void addUndoRedoTargetsOriginalFleet() {
        Context c = new Context();
        VehicleFleet a = c.baseline("v", "A", "Fleet A");
        VehicleFleet b = c.baseline("r", "B", "Fleet B");
        c.manager.setCurrentFleet(a);
        c.execute(c.commands.createAddDriverCommand("D1", "Alex", 1));
        Driver added = drivers(a).get(0);
        c.execute(c.commands.createSetCurrentFleetCommand("B"));
        for (int i = 0; i < 3; i++) {
            c.undo();
            check(drivers(a).isEmpty() && drivers(b).isEmpty(), "undo add affects original fleet");
            c.redo();
            check(drivers(a).size() == 1 && drivers(a).get(0) == added && drivers(b).isEmpty(),
                    "redo add preserves driver identity and original fleet");
            check(c.manager.getCurrentFleet() == b, "driver undo redo leaves current selection alone");
        }
        groups++;
    }

    private static void roleMementoRepeatedCycles() {
        Context c = new Context();
        VehicleFleet a = c.baseline("v", "A", "Fleet A");
        VehicleFleet b = c.baseline("r", "B", "Fleet B");
        Driver target = c.baselineDriver(a, "D1", 1);
        Driver other = c.baselineDriver(b, "D1", 1);
        c.manager.setCurrentFleet(a);
        c.execute(c.commands.createModifyRoleCommand("D1", 2));
        check(target.getRole() == 2, "modify applies new role");
        c.execute(c.commands.createSetCurrentFleetCommand("B"));
        for (int i = 0; i < 3; i++) {
            c.undo();
            check(target.getRole() == 1 && other.getRole() == 1, "memento restores original role");
            c.redo();
            check(target.getRole() == 2 && other.getRole() == 1, "redo role targets captured driver");
        }
        c.undo();
        c.manager.setCurrentFleet(a);
        c.execute(c.commands.createModifyRoleCommand("D1", 2));
        c.execute(c.commands.createModifyRoleCommand("D1", 1));
        c.undo();
        check(target.getRole() == 2, "separate mementos retain separate prior roles");
        c.undo();
        check(target.getRole() == 1, "older role memento remains intact");
        groups++;
    }

    private static void nameMementoRepeatedCycles() {
        Context c = new Context();
        VehicleFleet a = c.baseline("v", "A", "Original A");
        VehicleFleet b = c.baseline("r", "B", "Original B");
        c.manager.setCurrentFleet(a);
        c.execute(c.commands.createChangeNameCommand("Changed A"));
        check("Changed A".equals(a.getName()), "rename applies new name");
        c.execute(c.commands.createSetCurrentFleetCommand("B"));
        for (int i = 0; i < 3; i++) {
            c.undo();
            check("Original A".equals(a.getName()) && "Original B".equals(b.getName()),
                    "name memento restores original fleet name");
            c.redo();
            check("Changed A".equals(a.getName()) && "Original B".equals(b.getName()),
                    "rename redo targets original fleet");
        }
        c.manager.setCurrentFleet(a);
        c.execute(c.commands.createChangeNameCommand("Newest A"));
        c.undo();
        check("Changed A".equals(a.getName()), "new name memento retains intermediate name");
        c.undo();
        check("Original A".equals(a.getName()), "old name memento remains intact");
        groups++;
    }

    private static void deleteRestoresIdentityAndOrder() {
        Context c = new Context();
        VehicleFleet a = c.baseline("v", "A", "Fleet A");
        VehicleFleet b = c.baseline("r", "B", "Fleet B");
        Driver first = c.baselineDriver(a, "D1", 1);
        Driver middle = c.baselineDriver(a, "D2", 2);
        Driver last = c.baselineDriver(a, "D3", 1);
        c.manager.setCurrentFleet(a);
        c.execute(c.commands.createDeleteDriverCommand("D2"));
        c.execute(c.commands.createSetCurrentFleetCommand("B"));
        for (int i = 0; i < 3; i++) {
            expectOrder(a, first, last);
            c.undo();
            expectOrder(a, first, middle, last);
            check(drivers(b).isEmpty() && middle.getRole() == 2, "delete undo preserves identity and role");
            c.redo();
        }
        groups++;
    }

    private static void readsAndHistoryCommandsStayOutOfHistory() {
        Context c = new Context();
        VehicleFleet a = c.baseline("v", "A", "Original");
        c.baseline("r", "B", "Other");
        c.manager.setCurrentFleet(a);
        c.execute(c.commands.createChangeNameCommand("Changed"));
        c.undo();
        String before = history(c);
        c.execute(c.commands.createShowFleetCommand());
        c.execute(c.commands.createShowAllFleetsCommand());
        c.execute(c.commands.createSetCurrentFleetCommand("B"));
        c.execute(c.commands.createListCommand());
        check(before.equals(history(c)), "read select list commands preserve both history stacks");
        c.redo();
        check("Changed".equals(a.getName()), "redo survives read commands");
        c.undo();
        check("Original".equals(a.getName()), "undo targets mutation rather than history command");
        c.undo();
        check("Original".equals(a.getName()), "undo command did not enter its own history");
        c.redo();
        check("Changed".equals(a.getName()), "redo command did not clear redo stack");
        groups++;
    }

    private static void noCurrentAndEmptyHistory() {
        Context c = new Context();
        String empty = history(c);
        c.undo();
        c.redo();
        Command[] attempts = {
            c.commands.createAddDriverCommand("D1", "Alex", 1),
            c.commands.createModifyRoleCommand("D1", 2),
            c.commands.createDeleteDriverCommand("D1"),
            c.commands.createChangeNameCommand("Name"),
            c.commands.createShowFleetCommand(),
            c.commands.createSetCurrentFleetCommand("Missing")
        };
        for (Command command : attempts) {
            c.execute(command);
            check(c.manager.getFleetCount() == 0 && c.manager.getCurrentFleet() == null,
                    "no-current operation leaves manager unchanged");
            check(empty.equals(history(c)), "failed no-current command leaves history empty");
        }
        c.execute(c.commands.createShowAllFleetsCommand());
        c.execute(c.commands.createListCommand());
        check(empty.equals(history(c)), "empty show all and list preserve history");
        groups++;
    }

    private static void invalidMutationsKeepStateAndHistory() {
        Context c = new Context();
        VehicleFleet fleet = c.baseline("v", "A", "Original");
        Driver driver = c.baselineDriver(fleet, "D1", 1);
        c.execute(c.commands.createChangeNameCommand("Changed"));
        c.undo();
        String before = history(c);
        Command[] attempts = {
            c.commands.createFleetCommand("v", "a", "Duplicate"),
            c.commands.createFleetCommand("bad", "B", "Unknown Type"),
            c.commands.createFleetCommand("v", " ", "Blank ID"),
            c.commands.createFleetCommand("v", "B", " "),
            c.commands.createAddDriverCommand("d1", "Duplicate", 1),
            c.commands.createAddDriverCommand("D2", "Invalid role", 3),
            c.commands.createAddDriverCommand("D2", "Invalid role", 0),
            c.commands.createAddDriverCommand(" ", "Blank ID", 1),
            c.commands.createAddDriverCommand("D2", " ", 1),
            c.commands.createModifyRoleCommand("D1", 3),
            c.commands.createModifyRoleCommand("D1", 0),
            c.commands.createModifyRoleCommand("Missing", 2),
            c.commands.createDeleteDriverCommand("Missing"),
            c.commands.createChangeNameCommand(" "),
            c.commands.createSetCurrentFleetCommand("Missing")
        };
        for (Command command : attempts) {
            c.execute(command);
            check(c.manager.getFleetCount() == 1 && c.manager.getCurrentFleet() == fleet,
                    "invalid command preserves fleets and selection");
            expectOrder(fleet, driver);
            check(driver.getRole() == 1 && "Original".equals(fleet.getName()),
                    "invalid command rolls back all domain changes");
            check(before.equals(history(c)), "invalid command preserves undo and redo history");
        }
        c.redo();
        check("Changed".equals(fleet.getName()), "valid redo remains after all rejected commands");
        groups++;
    }

    private static void newMutationClearsRedo() {
        Context c = new Context();
        VehicleFleet fleet = c.baseline("r", "A", "Original");
        c.execute(c.commands.createChangeNameCommand("Discarded"));
        c.undo();
        c.execute(c.commands.createAddDriverCommand("D1", "Alex", 2));
        c.redo();
        check("Original".equals(fleet.getName()) && drivers(fleet).size() == 1,
                "successful new mutation clears old redo branch");
        c.undo();
        check(drivers(fleet).isEmpty(), "new mutation is in undo history");
        c.redo();
        check(drivers(fleet).size() == 1 && "Original".equals(fleet.getName()),
                "new mutation still supports redo after branch reset");
        groups++;
    }

    private static List<Driver> drivers(VehicleFleet fleet) {
        List<Driver> result = new ArrayList<Driver>();
        for (Iterator<Driver> iterator = fleet.getDrivers(); iterator.hasNext();) {
            result.add(iterator.next());
        }
        return result;
    }

    private static void expectOrder(VehicleFleet fleet, Driver... expected) {
        List<Driver> actual = drivers(fleet);
        check(actual.size() == expected.length, "driver count restored");
        for (int i = 0; i < expected.length; i++) {
            check(actual.get(i) == expected[i], "driver identity and position " + i);
        }
    }

    private static String history(Context c) {
        PrintStream previous = System.out;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output));
        try {
            c.invoker.showUndoRedoList();
        } finally {
            System.setOut(previous);
        }
        return output.toString();
    }

    private static void check(boolean condition, String message) {
        checks++;
        if (!condition) throw new AssertionError(message);
    }
}

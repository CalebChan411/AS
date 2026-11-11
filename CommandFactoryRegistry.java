import java.util.HashMap;
import java.util.Map;

/** Maps each command code to its factory so the execution loop stays generic. */
public class CommandFactoryRegistry {
    private Map<String, CommandFactory> factories;

    public CommandFactoryRegistry() {
        factories = new HashMap<String, CommandFactory>();
    }

    public void registerFactory(String code, CommandFactory factory) {
        if (code == null || code.trim().isEmpty() || factory == null) {
            throw new IllegalArgumentException("Factory registration details cannot be empty.");
        }
        String key = code.trim().toLowerCase();
        if (factories.containsKey(key)) {
            throw new IllegalArgumentException("Command factory code is already registered.");
        }
        factories.put(key, factory);
    }

    public CommandFactory getFactory(String code) {
        if (code == null) return null;
        return factories.get(code.trim().toLowerCase());
    }
}

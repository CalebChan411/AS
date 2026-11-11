import java.util.HashMap;
import java.util.Map;

/** Looks up registered fleet factories using their input type codes. */
public class FleetFactoryRegistry {
    private Map<String, FleetFactory> factories;

    public FleetFactoryRegistry() {
        factories = new HashMap<String, FleetFactory>();
    }

    public void registerFactory(FleetFactory factory) {
        if (factory == null || factory.getType() == null || factory.getType().trim().isEmpty()) {
            throw new IllegalArgumentException("Factory type cannot be empty.");
        }
        String type = factory.getType().trim().toLowerCase();
        if (factories.containsKey(type)) {
            throw new IllegalArgumentException("Factory type is already registered.");
        }
        factories.put(type, factory);
    }

    public FleetFactory getFactory(String type) {
        if (type == null) return null;
        return factories.get(type.trim().toLowerCase());
    }
}

package project2.compiler;

/**
 * A simple linked list environment.
 */
public class Environment {

    public final String identifier;
    public final Object value;
    public final Environment parent;

    /**
     * Creates a new empty environment.
     */
    public Environment() {
        identifier = null;
        value = null;
        parent = null;
    }

    /**
     * Extends a parent environment by providing a new identifier and a value. This constructor is
     * private, use the define() method instead.
     * @param identifier the identifier for the value to be bound
     * @param value the value to be bound
     * @param parent the parent environment to be extended
     */
    private Environment(String identifier, Object value, Environment parent) {
        this.identifier = identifier;
        this.value = value;
        this.parent = parent;
    }

    /**
     * Creates a new environment that extends the current environment given a new identifier. Note
     * that redefining an identifier shadows the previous value.
     * @param identifier the identifier for the value to be bound
     * @param value the value to be bound
     * @return a new Environment that extends the current environment
     */
    public Environment define(String identifier, Object value) {
        return new Environment(identifier, value, this);
    }

    /**
     * Checks whether an identifier is defined in the current environment.
     * @param identifier the identifier to check
     * @return true if the identifier exists in the current environment
     */
    public boolean isDefined(String identifier) {
        return !(identifier == null || this.identifier == null || this.parent == null)
            && (this.identifier.equals(identifier) || parent.isDefined(identifier));
    }

    /**
     * Gets the bound value of the identifier in the current environment. Returns null if the
     * identifier is not bound.
     * @param identifier the identifier to find
     * @return the bound value associated with this identifier, null if undefined
     */
    public Object getValue(String identifier) {
        if (identifier == null || this.identifier == null || this.parent == null)
            return null;
        return this.identifier.equals(identifier) ? value : parent.getValue(identifier);
    }

}

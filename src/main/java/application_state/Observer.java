package application_state;

/**
 * Implement this interface for any class that you wish to be registered as an observer of another object
 */
public interface Observer {
    /**
     * Receive notification from observed object
     * @param object Whatever data might be passed in by the observed object
     */
    public void notify(Object object);
}
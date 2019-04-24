package edu.wpi.cs3733d19.teamN.application_state;

/**
 * Implemented for any class that is registered as an observer of ObservableBus
 */
public interface Observer {
    /**
     * Receive notification from observed object, the ObservableBus
     * @param object the event from the ObservableBus
     */
    public void notify(Object object);
}
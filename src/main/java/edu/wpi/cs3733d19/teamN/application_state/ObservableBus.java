package edu.wpi.cs3733d19.teamN.application_state;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.util.*;

public class ObservableBus {
    private Event event = new Event();    // The current event
    private HashMap<String, edu.wpi.cs3733d19.teamN.application_state.Observer> registeredObservers = new HashMap();    // The objects observing the event

    /**
     * Set the old event to the new event and notify all observers.
     * @param ev the new event
     */
    public void updateEvent(Event ev){
        event = ev;
        notifyObservers();
    }

    /**
     * @return the current event
     */
    public Event getEvent(){
        return event;
    }

    /**
     * Register this object as an observer. For a given observer, give it a name
     * so that when a new observer of the same getType is added it replaces the old one in the
     * HashMap, allowing the old object to be garbage collected.
     * @param name the name of the observer
     * @param o the observer
     */
    public void register(String name, Observer o){
        registeredObservers.put(name, o);
    }

    /**
     * Notify all observers.
     */
    @SuppressFBWarnings(value = "WMI_WRONG_MAP_ITERATOR")
    private void notifyObservers(){
        Set<String> observers = registeredObservers.keySet();
        for (String o: observers) {
            registeredObservers.get(o).notify(event);
        }
    }
}

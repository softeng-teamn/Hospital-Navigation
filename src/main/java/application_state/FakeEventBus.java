package application_state;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.util.*;

public class FakeEventBus {
    private Event event = new Event();
    private HashMap<String, Observer> registeredObservers = new HashMap();

    public void updateEvent(Event ev){
        event = ev;
        notifyObservers();
    }

    public Event getEvent(){
        return event;
    }

    public void register(String name, Observer o){
        registeredObservers.put(name, o);
    }

    @SuppressFBWarnings(value = "WMI_WRONG_MAP_ITERATOR")
    public void notifyObservers(){
        Set<String> observers = registeredObservers.keySet();
        for (String o: observers) {
            registeredObservers.get(o).notify(event);
        }
    }
}

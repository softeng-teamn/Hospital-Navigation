package application_state;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class FakeEventBus {
    private Event event = new Event();
    private HashMap<String, Observer> registeredObservers = new HashMap();

    public void updateEvent(Event ev){
        // Make sure the input is 140 characters or less and contains only letters and spaces
        event = ev;
        System.out.println("        Event NAME: " + event.getEventName());
        notifyObservers();
    }

    public Event getEvent(){
        return event;
    }

    public void register(String name, Observer o){
        registeredObservers.put(name, o);
    }

//    public void deregister(Observer o) {
//        observers.remove(o);
//    }

    public void notifyObservers(){
        Set<String> observers = registeredObservers.keySet();
        for (String o: observers) {
            registeredObservers.get(o).notify(event);
        }
    }
}

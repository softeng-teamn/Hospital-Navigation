package application_state;

import java.util.ArrayList;
import java.util.List;

public class FakeEventBus {
    private Event event = new Event();
    private List<Observer> observers = new ArrayList<Observer>();

    public void updateEvent(Event ev){
        // Make sure the input is 140 characters or less and contains only letters and spaces
        event = ev;
        notifyObservers();
    }

    public Event getEvent(){
        return event;
    }

    public void register(Observer o){
        observers.add(o);
    }

    public void notifyObservers(){
        for (Observer o: observers) {
            o.notify(event);
        }
    }
}

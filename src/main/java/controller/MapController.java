package controller;

import service.MapService;

public class MapController {

    static MapService map;

    // Creates static variables when compiled
    static {
        System.out.println("MapService was run");
        map = new MapService();
    }

    // Initializes the Map ???? if static method works, then this is not needed.
    // Question for implementor: How will we know when the map needs to be built?
    public void init() {

    }



}

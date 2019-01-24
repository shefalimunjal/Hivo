package edu.sjsu.hivo.events;

import java.util.List;

import edu.sjsu.hivo.model.Property;

public class MapScreenData {
    private final List<Property> properties;

    public MapScreenData(List<Property> properties) {
        this.properties = properties;
    }

    public List<Property> getProperties() {
        return properties;
    }
}

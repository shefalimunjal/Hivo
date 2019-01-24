package edu.sjsu.hivo.events;

import edu.sjsu.hivo.model.Property;

public class DetailActivityData {
    private final Property property;

    public DetailActivityData(Property property) {
        this.property = property;
    }

    public Property getProperty() {
        return property;
    }
}

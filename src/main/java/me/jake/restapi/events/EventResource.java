package me.jake.restapi.events;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import org.springframework.hateoas.RepresentationModel;

//https://docs.spring.io/spring-hateoas/docs/current/reference/html/
//        ResourceSupport is now RepresentationModel
//        Resource is now EntityModel
//        Resources is now CollectionModel
//        PagedResources is now PagedModel
public class EventResource extends RepresentationModel {

    @JsonUnwrapped
    private Event event;

    public EventResource(Event event) {
        this.event = event;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }
}

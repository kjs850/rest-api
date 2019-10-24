package me.jake.restapi.events;


import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EventTest {

    @Test
    public void builder(){
        Event event = Event.builder()
                .name("rest api")
                .description("rest api description")
                .build();
        assertThat(event).isNotNull();
    }

    @Test
    public void javaBean(){
        //given
        String name = "name";
        String description = "descriptions";

        //when
        Event event = new Event();
        event.setName(name);
        event.setDescription(description);

        //then
        assertThat(event.getName()).isEqualTo(name);
        assertThat(event.getDescription()).isEqualTo(description);
    }
}
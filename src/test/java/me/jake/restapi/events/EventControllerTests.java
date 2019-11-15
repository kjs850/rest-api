package me.jake.restapi.events;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.jake.restapi.common.TestDescription;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.stream.IntStream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class EventControllerTests {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    EventRepository eventRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Test
    @TestDescription("정상적으로 실행되어야 한다.")
    public void createEvent() throws Exception {
        EventDto eventDto = EventDto.builder()
                .name("name")
                .description("desc")
                .beginEnrollmentDateTime(LocalDateTime.of(2018, 11, 12, 14,22))
                .closeEnrollmentDateTime(LocalDateTime.of(2018, 11, 13, 14,22))
                .beginEventDateTime(LocalDateTime.of(2018, 11, 20, 14,22))
                .endEventDateTime(LocalDateTime.of(2018, 11, 21, 14,22))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("강남")
                .build();

        mockMvc.perform(post("/api/events")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaTypes.HAL_JSON)
                    .content(objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").exists())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE))
                .andExpect(jsonPath("free").value(false))
                .andExpect(jsonPath("offline").value(true))
                .andExpect(jsonPath("eventStatus").value(EventStatus.DRAFT.name()))
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.query-events").exists())
                .andExpect(jsonPath("_links.update-event").exists())
                ;

    }


    @Test
    @TestDescription("bad request.")
    public void createEvent_BadRequest() throws Exception {
        Event event = Event.builder()
                .id(100)
                .name("name")
                .description("desc")
                .beginEnrollmentDateTime(LocalDateTime.of(2018, 11, 12, 14,22))
                .closeEnrollmentDateTime(LocalDateTime.of(2018, 11, 13, 14,22))
                .beginEventDateTime(LocalDateTime.of(2018, 11, 20, 14,22))
                .endEventDateTime(LocalDateTime.of(2018, 11, 21, 14,22))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("강남")
                .build();


        mockMvc.perform(post("/api/events")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaTypes.HAL_JSON)
                .content(objectMapper.writeValueAsString(event)))
                .andDo(print())
                .andExpect(status().isBadRequest());

        // application-test.properties
        // spring.jackson.deserialization.fail-on-unknown-properties=true

    }

    @Test
    @TestDescription("입력값이 비어있는 경우에 에러가 나는 케이스")
    public void create_Bad_Request_Empty_Input() throws Exception {
        EventDto eventDto = EventDto.builder().build();

        this.mockMvc.perform(post("/api/events")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(this.objectMapper.writeValueAsString(eventDto)))
                .andExpect(status().isBadRequest());
    }


    @Test
    @TestDescription("입력값이 잘못되어 있는 경우에 에러가 나는 케이스")
    public void create_Bad_Request_Wrong_Input() throws Exception {
        EventDto eventDto = EventDto.builder()
                .name("name")
                .description("desc")
                .beginEnrollmentDateTime(LocalDateTime.of(2018, 11, 12, 14,22))
                .closeEnrollmentDateTime(LocalDateTime.of(2018, 11, 13, 14,22))
                .beginEventDateTime(LocalDateTime.of(2018, 11, 20, 14,22))
                .endEventDateTime(LocalDateTime.of(2018, 10, 21, 14,22))  //wrong
                .basePrice(10000)  //wrong
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("강남")
                .build();

        this.mockMvc.perform(post("/api/events")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].objectName").exists())
                .andExpect(jsonPath("$[0].defaultMessage").exists())
                .andExpect(jsonPath("$[0].code").exists())
                ;
    }

    @Test
    @TestDescription("30개의 이벤트를 10개씩 두번째 페이지 조회하기")
    public void queryEvents() throws Exception{
        //given
        IntStream.range(0, 30).forEach(this::generateEvnet);

        //when
        this.mockMvc.perform(get("/api/events")
                    .param("page", "1")
                    .param("size", "10")
                    .param("sort", "name,DESC")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("page").exists())
                ;
    }

    @Test
    @TestDescription("기존의 이벤트를 하나 조회 하기")
    public void getEvent() throws Exception{
        //given
        Event event = this.generateEvnet(100);
        //when
        this.mockMvc.perform(get("/api/events/{id}", event.getId()))
                .andDo(print())
                .andExpect(status().isOk())
        ;
    }

    @Test
    @TestDescription("이벤트를 정상적으로 수정하기")
    public void updateEvent() throws Exception {
        //given
        Event event = this.generateEvnet(200);
        EventDto eventDto = this.modelMapper.map(event, EventDto.class);
        String eventName = "Updated Event";
        eventDto.setName(eventName);

        //when
        this.mockMvc.perform(put("/api/events/{id}", event.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(this.objectMapper.writeValueAsString(eventDto))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("name").value(eventName))
                ;
    }

    @Test
    @TestDescription("입력값이 잘못된 경우에 이벤트 수정 실패")
    public void updateEvent400() throws Exception {
        //given
        Event event = this.generateEvnet(200);
        EventDto eventDto = new EventDto();

        //when
        this.mockMvc.perform(put("/api/events/{id}", event.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(this.objectMapper.writeValueAsString(eventDto))
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
        ;

    }
    private Event generateEvnet(int index){
        Event event = Event.builder()
                .name("event " + index)
                .description("desc")
                .beginEnrollmentDateTime(LocalDateTime.of(2018, 11, 12, 14,22))
                .closeEnrollmentDateTime(LocalDateTime.of(2018, 11, 13, 14,22))
                .beginEventDateTime(LocalDateTime.of(2018, 11, 20, 14,22))
                .endEventDateTime(LocalDateTime.of(2018, 11, 21, 14,22))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("강남")
                .build();
        return this.eventRepository.save(event);
    }
}

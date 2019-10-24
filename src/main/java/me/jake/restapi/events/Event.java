package me.jake.restapi.events;

import lombok.*;
import org.springframework.data.annotation.Id;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import java.time.LocalDateTime;

//https://docs.google.com/document/d/1GFo3W6XxqhxDVVqxiSEtqkuVCX93Tdb3xzINRtTIx10/edit#heading=h.48yww8wm0y1u

@Builder
@AllArgsConstructor @NoArgsConstructor
@Getter @Setter
@EqualsAndHashCode(of = "id")
@Entity
public class Event {

    @Id
    @GeneratedValue
    private Integer id;

    private String name;
    private String description;
    private LocalDateTime beginEnrollmentDateTime;
    private LocalDateTime closeEnrollmentDateTime;
    private LocalDateTime beginEventDateTime;
    private LocalDateTime endEventDateTime;
    private String location; // (optional) 이게 없으면 온라인 모임
    private int basePrice; // (optional)
    private int maxPrice; // (optional)
    private int limitOfEnrollment;

    private boolean offline;
    private boolean free;

    @Enumerated(EnumType.STRING)
    private EventStatus eventStatus;


}

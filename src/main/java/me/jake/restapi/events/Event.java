package me.jake.restapi.events;

import lombok.*;
import me.jake.restapi.accounts.Account;
import org.springframework.hateoas.RepresentationModel;

import javax.persistence.*;
import java.time.LocalDateTime;

//https://docs.google.com/document/d/1GFo3W6XxqhxDVVqxiSEtqkuVCX93Tdb3xzINRtTIx10/edit#heading=h.48yww8wm0y1u

@Builder
@AllArgsConstructor @NoArgsConstructor
@Getter @Setter
@EqualsAndHashCode(of = "id")
@Entity
public class Event extends RepresentationModel<Event> {

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
    private EventStatus eventStatus = EventStatus.DRAFT;

    @ManyToOne
    private Account manager;

    public void update() {
        if(this.basePrice == 0 && this.maxPrice == 0){
            this.free = true;
        }else{
            this.free = false;
        }
        //update offline
        if(this.location == null || this.location.isEmpty()){
            this.offline = false;
        }else{
            this.offline = true;
        }

    }
}

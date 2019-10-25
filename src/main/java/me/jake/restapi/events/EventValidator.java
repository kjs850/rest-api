package me.jake.restapi.events;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Component
public class EventValidator {

    public void validate(EventDto eventDto, Errors errors){
        if(eventDto.getBasePrice() > eventDto.getMaxPrice()
                                            && eventDto.getMaxPrice() != 0){
            errors.rejectValue("basePrice","wrongValue", "basePrice is wrong!");
            errors.rejectValue("maxPrice","wrongValue", "maxPrice is wrong!");
        }

        LocalDateTime endEventDateTime = eventDto.getEndEventDateTime();
        if (endEventDateTime.isBefore(eventDto.getBeginEventDateTime()) ||
                endEventDateTime.isBefore(eventDto.getCloseEnrollmentDateTime()) ||
                endEventDateTime.isBefore(eventDto.getBeginEnrollmentDateTime())) {

            errors.rejectValue("endEventDateTime", "wrongValue", "endEventDateTime is wrong!");
        }


        // TODO : 나머지것들 밸리데이션 체크
    }
}

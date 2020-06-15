package org.ajc2020.spring1.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;
import lombok.experimental.Accessors;
import org.ajc2020.utility.communication.TicketResource;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Data
@Entity
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;

    @Accessors(chain = true)
    private LocalDate targetDay;

    @Accessors(chain = true)
    private OffsetDateTime creationDate;

    @ManyToOne
    @Accessors(chain = true)
    @JsonBackReference
    private Worker worker;

    public TicketResource toResource() {
        return TicketResource.builder()
                .targetDay(targetDay)
                .creationDate(creationDate)
                .build();
    }
}

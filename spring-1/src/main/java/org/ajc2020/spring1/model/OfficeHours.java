package org.ajc2020.spring1.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;
import lombok.experimental.Accessors;
import org.ajc2020.utility.communication.OfficeHoursResource;

import javax.persistence.*;
import java.time.OffsetDateTime;

@Data
@Entity
public class OfficeHours {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @Accessors(chain = true)
    @JsonBackReference
    private Worker worker;

    private OffsetDateTime arrive;

    private OffsetDateTime leave;

    public boolean isLoggedIn() {
        return getLeave() == null;
    }

    public OfficeHoursResource toResource() {
        return OfficeHoursResource.builder()
                .arrive(arrive)
                .leave(leave)
                .build();
    }

}

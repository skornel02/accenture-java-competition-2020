package org.ajc2020.spring1.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.util.Date;

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

    private Date arrive;

    private Date leave;

    public boolean isLoggedIn() {
        return getLeave() == null;
    }

}
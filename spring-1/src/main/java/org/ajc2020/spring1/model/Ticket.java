package org.ajc2020.spring1.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.time.LocalDate;

@Data
@Entity
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;

    @Accessors(chain = true)
    private LocalDate targetDay;

    @Accessors(chain = true)
    private LocalDate creationDate;

    @ManyToOne
    @Accessors(chain = true)
    @JsonBackReference
    private Worker worker;
}

package org.ajc2020.spring1.model;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
public class WaitListItem {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;

    private Date targetDay;

    private Date creationDate;

    @ManyToOne
    private Worker worker;

    public WaitListItem setWorker(Worker worker) {
        this.worker = worker;
        return this;
    }

    public WaitListItem setTargetDay(Date targetDay) {
        this.targetDay = targetDay;
        return this;
    }

    public WaitListItem setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
        return this;
    }
}

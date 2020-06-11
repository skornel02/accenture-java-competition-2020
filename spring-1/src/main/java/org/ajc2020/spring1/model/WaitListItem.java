package org.ajc2020.spring1.model;

import javax.persistence.*;
import java.util.Date;

@Entity
public class WaitListItem {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;

    private Date targetDay;

    private Date creationDate;

    @ManyToOne
    private Worker worker;


    public Worker getWorker() {
        return worker;
    }

    public WaitListItem setWorker(Worker worker) {
        this.worker = worker;
        return this;
    }

    public long getId() {
        return id;
    }

    public Date getTargetDay() {
        return targetDay;
    }

    public WaitListItem setTargetDay(Date targetDay) {
        this.targetDay = targetDay;
        return this;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public WaitListItem setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
        return this;
    }
}

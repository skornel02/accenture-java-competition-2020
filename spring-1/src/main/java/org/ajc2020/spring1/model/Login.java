package org.ajc2020.spring1.model;

import javax.persistence.*;
import java.util.Date;

@Entity
public class Login {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    private Worker worker;

    private Date arrive;

    private Date leave;

    public Date getArrive() {
        return arrive;
    }

    public void setArrive(Date arrive) {
        this.arrive = arrive;
    }

    public Date getLeave() {
        return leave;
    }

    public void setLeave(Date leave) {
        this.leave = leave;
    }

    public Login setWorker(Worker worker) {
        this.worker = worker;
        return this;
    }
}

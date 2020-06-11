package org.ajc2020.spring1.model;

import javax.persistence.*;
import java.util.List;

@Entity
public class Worker {
    @Id
    private String email;

    private String password;

    private String rfid;

    private String name;

    @Enumerated(EnumType.STRING)
    private Status status;

    @OneToMany(mappedBy = "worker")
    private List<Login> loginHistory;
}

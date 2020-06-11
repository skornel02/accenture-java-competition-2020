package org.ajc2020.spring1.model;

import javax.persistence.*;
import java.sql.Date;

@Entity
public class Login {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    private Worker worker;

    private Date arrive;

    private Date leave;
}

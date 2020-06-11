package org.ajc2020.spring1.model;

import javax.persistence.*;

@Entity
public class WaitListItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToOne
    private Worker worker;


}

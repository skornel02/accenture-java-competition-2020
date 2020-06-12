package org.ajc2020.spring1.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.Size;

@Entity
@Data
public class OfficeSettings {

    @Id
    @GeneratedValue
    private long id;

    @Size(min = 1)
    private int capacity;

    @Size(min = 0, max = 1)
    private double operationPercentage;

}

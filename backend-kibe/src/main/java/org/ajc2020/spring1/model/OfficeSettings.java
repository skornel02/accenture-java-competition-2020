package org.ajc2020.spring1.model;

import lombok.Data;
import org.ajc2020.utility.communication.OfficeResource;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Entity
@Data
public class OfficeSettings {

    @Id
    @GeneratedValue
    private long id;

    @Min(1)
    private int capacity;

    @Max(1)
    private double operationPercentage;

    @Min(0)
    private double centimetersBetweenEmployeeStations;

    public int getEffectiveCapacity() {
        return (int) Math.floor(capacity * operationPercentage);
    }

    public void fromResource(OfficeResource newValues) {
        setCapacity(newValues.getCapacity());
        setOperationPercentage(newValues.getPercentage());
        setCentimetersBetweenEmployeeStations(newValues.getCentimetersBetweenEmployeeStations());
    }

    public OfficeResource toResource() {
        return OfficeResource.builder()
                .capacity(capacity)
                .percentage(operationPercentage)
                .centimetersBetweenEmployeeStations(centimetersBetweenEmployeeStations)
                .build();
    }

}

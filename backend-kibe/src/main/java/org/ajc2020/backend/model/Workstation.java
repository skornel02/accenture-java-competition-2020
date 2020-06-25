package org.ajc2020.backend.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.ajc2020.utility.communication.WorkstationCreationRequest;
import org.ajc2020.utility.communication.WorkstationResource;
import org.ajc2020.utility.resource.DeskOrientation;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import java.util.Objects;

@Entity
@Data
@NoArgsConstructor
public class Workstation {

    @Id
    private String id;

    private DeskOrientation orientation;

    private double x;
    private double y;

    private int zone;

    @OneToOne
    private Worker occupier;

    private boolean enabled = true;

    public Workstation(WorkstationCreationRequest request) {
        this.id = request.getId();
        this.orientation = request.getOrientation();
        this.x = request.getX();
        this.y = request.getY();
        this.zone = request.getZone();
        this.enabled = request.isEnabled();
    }

    public double distanceFrom(Workstation station) {
        if (getZone() != station.getZone())
            return Double.MAX_VALUE;
        return Math.sqrt(Math.pow(getX() - station.getX(), 2) + Math.pow(getY() - station.getY(), 2));
    }

    /**
     *
     * @param distance The units are in decimeters
     * @return units in centimeters
     */
    public double unitToCentimeter(double distance) {
        return distance * 5d;
    }

    public void clearOccupier() {
        setOccupier(null);
    }

    public WorkstationResource toResource() {
        return WorkstationResource.builder()
                .id(getId())
                .x(getX())
                .y(getY())
                .orientation(getOrientation())
                .zone(getZone())
                .occupier(getOccupier() != null ? getOccupier().toResource() : null)
                .enabled(isEnabled())
                .build();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Workstation that = (Workstation) o;
        return Double.compare(that.x, x) == 0 &&
                Double.compare(that.y, y) == 0 &&
                Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, x, y);
    }
}

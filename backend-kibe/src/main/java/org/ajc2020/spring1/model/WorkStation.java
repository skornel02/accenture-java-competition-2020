package org.ajc2020.spring1.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import java.util.Objects;

@Entity
@Data
public class WorkStation {

    @Id
    private String id;

    private DeskOrientation orientation;

    private double x;
    private double y;

    @OneToOne
    private Worker occupier;

    public double distanceFrom(WorkStation station) {
        return Math.sqrt(Math.pow(getX() - station.getX(), 2) + Math.pow(getY() - station.getY(), 2));
    }

    public double pixelToCentimeter(double distance) {
        return distance * 39.604d;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WorkStation that = (WorkStation) o;
        return Double.compare(that.x, x) == 0 &&
                Double.compare(that.y, y) == 0 &&
                Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, x, y);
    }
}

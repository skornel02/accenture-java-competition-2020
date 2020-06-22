package org.ajc2020.utility.communication;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.ajc2020.utility.resource.DeskOrientation;
import org.springframework.hateoas.RepresentationModel;

import java.util.Locale;

@JsonIgnoreProperties(ignoreUnknown = true)
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkStationResource extends RepresentationModel<WorkStationResource> {

    private String id;
    private DeskOrientation orientation;
    private double x;
    private double y;
    private int zone;
    private WorkerResource occupier;
    private boolean enabled;

    public String getColor() {
        if (!enabled)
            return "fill-red";
        if (occupier == null)
            return "fill-green";
        return "fill-yellow";
    }

    public String getTransform() {
        switch (orientation) {
            case DOWN:
                return String.format(Locale.ROOT, "matrix(%d, %d, %d, %d, %.7f, %.7f)", 1, 0, 0, 1, x, y);
            case UP:
                return String.format(Locale.ROOT, "matrix(%d, %d, %d, %d, %.7f, %.7f)", -1, 0, 0, -1, x, y);
            case LEFT:
                return String.format(Locale.ROOT, "matrix(%d, %d, %d, %d, %.7f, %.7f)", 0, -1, 1, 0, x, y);
            case RIGHT:
                return String.format(Locale.ROOT, "matrix(%d, %d, %d, %d, %.7f, %.7f)", 0, 1, -1, 0, x, y);
        }
        return "";
    }
}

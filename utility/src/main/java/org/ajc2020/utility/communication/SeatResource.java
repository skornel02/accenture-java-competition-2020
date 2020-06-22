package org.ajc2020.utility.communication;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Locale;

@Slf4j
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeatResource {
    private String id;
    private float x;
    private float y;
    private int rotation;
    private String color = "#008c00";

    public String getTransform() {
        final float MULTIPLIER=1;
        switch (rotation) {
            case 0: return String.format(Locale.ROOT, "matrix(%d, %d, %d, %d, %.7f, %.7f)",  1, 0, 0, 1,x * MULTIPLIER,y * MULTIPLIER);
            case 2: return String.format(Locale.ROOT, "matrix(%d, %d, %d, %d, %.7f, %.7f)", -1, 0, 0,-1,x * MULTIPLIER,y * MULTIPLIER);
            case 1: return String.format(Locale.ROOT, "matrix(%d, %d, %d, %d, %.7f, %.7f)",  0,-1, 1, 0,x * MULTIPLIER,y * MULTIPLIER);
            case 3: return String.format(Locale.ROOT, "matrix(%d, %d, %d, %d, %.7f, %.7f)",  0, 1,-1, 0,x * MULTIPLIER,y * MULTIPLIER);
        }
        return "";
    }

    public String getCoordinates() {
        final float WIDTH = 20;
        final float HEIGHT = 20;
        return String.format("%d, %d, %d, %d", (int)(x - WIDTH), (int)(y - HEIGHT), (int)(x + WIDTH), (int)(y + HEIGHT));
    }
}

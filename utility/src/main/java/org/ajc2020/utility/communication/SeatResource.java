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

    public String getTransform() {
        switch (rotation) {
            case 0: return String.format(Locale.ROOT, "matrix(%.7f, %d, %d, %d, %.7f, %.7f)",  1.0000089f,0,0, 1,x,y);
            case 2: return String.format(Locale.ROOT, "matrix(%.7f, %d, %d, %d, %.7f, %.7f)", -1.0000089f,0,0,-1,x,y);
            case 1: return String.format(Locale.ROOT, "matrix(%d, %.7f, %d, %d, %.7f, %.7f)", 0,-1.0000089,1,0,x,y);
            case 3: return String.format(Locale.ROOT, "matrix(%d, %.7f, %d, %d, %.7f, %.7f)", 0,1.0000089,-1,0,x,y);
        }
        return "";
    }
}

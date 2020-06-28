package org.ajc2020.frontend.communication;

import lombok.*;
import org.ajc2020.utility.communication.RemainingTime;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class RemainingTimeEntry extends RemainingTime {
    private Integer rank;

    public RemainingTimeEntry(RemainingTime body) {
        super(body);
    }
}

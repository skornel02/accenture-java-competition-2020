package org.ajc2020.utility.communication;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
public class WaitingResource {

    @NotNull
    private WorkerResource worker;
    @NotNull
    private TicketResource ticket;
    @NotNull
    private boolean canGoIn;

}

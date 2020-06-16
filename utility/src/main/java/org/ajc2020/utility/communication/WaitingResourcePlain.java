package org.ajc2020.utility.communication;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.validation.constraints.NotNull;

@Slf4j
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WaitingResourcePlain {

    private WorkerResource worker;
    private TicketResourcePlain ticket;
    private boolean canGoIn;

}

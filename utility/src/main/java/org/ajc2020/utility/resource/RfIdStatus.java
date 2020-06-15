package org.ajc2020.utility.resource;

import lombok.Data;

@Data
public class RfIdStatus {
    private Status status;

    private static RfIdStatus okStatus = new RfIdStatus(Status.OK);
    private static RfIdStatus errorStatus = new RfIdStatus(Status.ERROR);
    private static RfIdStatus unknownRfidStatus = new RfIdStatus(Status.UNKNOWN_RF_ID);
    private static RfIdStatus fullHouseStatus = new RfIdStatus(Status.FULL_HOUSE);

    public static RfIdStatus ok() {
        return okStatus;
    }

    public static RfIdStatus error() {
        return errorStatus;
    }

    public static RfIdStatus unknownRfid() {
        return unknownRfidStatus;
    }

    public static RfIdStatus fullHouse() {return fullHouseStatus;}

    public RfIdStatus(Status status) {
        this.status = status;
    }

    public enum Status {
        UNKNOWN_RF_ID,
        OK,
        ERROR,
        FULL_HOUSE
    }
}

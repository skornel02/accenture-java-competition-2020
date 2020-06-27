package org.ajc2020.utility.resource;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
public class RfIdStatus {
    private Status status;
    @Accessors(chain = true)
    private String mapSvg;

    private static RfIdStatus okStatus = new RfIdStatus(Status.OK);
    private static RfIdStatus errorStatus = new RfIdStatus(Status.ERROR);
    private static RfIdStatus unknownRfidStatus = new RfIdStatus(Status.UNKNOWN_RF_ID);
    private static RfIdStatus fullHouseStatus = new RfIdStatus(Status.FULL_HOUSE);
    private static RfIdStatus notInside = new RfIdStatus(Status.NOT_INSIDE);
    private static RfIdStatus notOutside = new RfIdStatus(Status.NOT_OUTSIDE);

    public static RfIdStatus ok() {
        return okStatus;
    }

    public static RfIdStatus okWithMap(String mapSvg) {
        return new RfIdStatus(Status.OK).setMapSvg(mapSvg);
    }

    public static RfIdStatus error() {
        return errorStatus;
    }

    public static RfIdStatus unknownRfid() {
        return unknownRfidStatus;
    }

    public static RfIdStatus fullHouse() {
        return fullHouseStatus;
    }

    public static RfIdStatus notInside() {
        return notInside;
    }

    public static RfIdStatus notOutside() {
        return notOutside;
    }

    public RfIdStatus(Status status) {
        this.status = status;
    }

    public enum Status {
        UNKNOWN_RF_ID,
        OK,
        ERROR,
        FULL_HOUSE,
        NOT_INSIDE,
        NOT_OUTSIDE,
    }
}

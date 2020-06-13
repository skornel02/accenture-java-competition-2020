package org.ajc2020.utilty.resource;

import lombok.Data;

@Data
public class RfIdStatus {
    private Status status;

    private static RfIdStatus okStatus = new RfIdStatus(Status.OK);
    private static RfIdStatus errorStatus = new RfIdStatus(Status.Error);
    private static RfIdStatus unknownRfidStatus = new RfIdStatus(Status.UnknownRfId);

    public static RfIdStatus Ok() {
        return okStatus;
    }

    public static RfIdStatus Error() {
        return errorStatus;
    }

    public static RfIdStatus UnkownRfid() {
        return unknownRfidStatus;
    }

    public RfIdStatus(Status status) {
        this.status = status;
    }

    public enum Status {
        UnknownRfId,
        OK,
        Error
    }
}

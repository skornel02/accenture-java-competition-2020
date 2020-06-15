package org.ajc2020.utility.resource;

import lombok.Data;

@Data
public class RegistrationStatus {
    private Status status;

    private static RegistrationStatus okStatus = new RegistrationStatus(Status.OK);
    private static RegistrationStatus errorStatus = new RegistrationStatus(Status.Error);
    private static RegistrationStatus unknownUserStatus = new RegistrationStatus(Status.UnknownUser);

    public static RegistrationStatus Ok() {
        return okStatus;
    }

    public static RegistrationStatus Error() {
        return errorStatus;
    }

    public static RegistrationStatus UnknownUser() {
        return unknownUserStatus;
    }

    public RegistrationStatus(Status status) {
        this.status = status;
    }

    public enum Status {
        UnknownUser,
        OK,
        Error
    }
}

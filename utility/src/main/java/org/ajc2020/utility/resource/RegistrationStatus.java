package org.ajc2020.utility.resource;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RegistrationStatus {
    private Status status;

    private static RegistrationStatus okStatus = new RegistrationStatus(Status.OK);
    private static RegistrationStatus errorStatus = new RegistrationStatus(Status.Error);
    private static RegistrationStatus unknownUserStatus = new RegistrationStatus(Status.UnknownUser);
    private static RegistrationStatus notRequired = new RegistrationStatus(Status.NotRequired);

    public static RegistrationStatus ok() {
        return okStatus;
    }

    public static RegistrationStatus error() {
        return errorStatus;
    }

    public static RegistrationStatus unknownUser() {
        return unknownUserStatus;
    }

    public static RegistrationStatus notRequired() {
        return notRequired;
    }

    private RegistrationStatus(Status status) {
        this.status = status;
    }

    public enum Status {
        UnknownUser,
        OK,
        Error,
        NotRequired,
    }
}

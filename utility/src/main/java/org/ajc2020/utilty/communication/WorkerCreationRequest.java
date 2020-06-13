package org.ajc2020.utilty.communication;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
public class WorkerCreationRequest {

    @Email
    @NotNull
    private String email;

    @NotEmpty
    @NotNull
    private String name;

    @NotEmpty
    @NotNull
    private String rfId;

    @NotNull
    @NotEmpty
    private String password;

}
package org.ajc2020.utility.communication;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
